package logisticspipes.ticks;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.UUID;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.packets.BufferTransfer;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.tuples.Pair;

public class ServerPacketBufferHandlerThread {

    private static class ServerCompressorThread extends Thread {

        // Map of Players to lists of S->C packets to be serialized and compressed
        private final HashMap<UUID, LinkedList<ModernPacket>> serverList = new HashMap<>();
        // Map of Players to serialized but still uncompressed S->C data
        private final HashMap<UUID, byte[]> serverBuffer = new HashMap<>();
        // used to cork the compressor so we can queue up a whole bunch of packets at once
        private boolean pause = false;
        // Clear content on next tick
        private final Queue<UUID> playersToClear = new LinkedList<>();

        public ServerCompressorThread() {
            super("LogisticsPipes Packet Compressor Server");
            setDaemon(true);
            start();
        }

        @Override
        public void run() {
            while (true) {
                try {
                    synchronized (serverList) {
                        if (!pause) {
                            for (Entry<UUID, LinkedList<ModernPacket>> entry : serverList.entrySet()) {
                                ByteArrayOutputStream out = new ByteArrayOutputStream();
                                DataOutputStream data = new DataOutputStream(out);
                                byte[] towrite = serverBuffer.get(entry.getKey());
                                if (towrite != null) {
                                    data.write(towrite);
                                }
                                LinkedList<ModernPacket> packets = entry.getValue();
                                for (ModernPacket packet : packets) {
                                    LPDataOutputStream t = new LPDataOutputStream();
                                    t.writeShort(packet.getId());
                                    t.writeInt(packet.getDebugId());
                                    try {
                                        packet.writeData(t);
                                    } catch (ConcurrentModificationException e) {
                                        throw new RuntimeException(
                                                "LogisticsPipes error (please report): Method writeData is not thread-safe in packet "
                                                        + packet.getClass().getSimpleName(),
                                                e);
                                    }
                                    data.writeInt(t.size());
                                    data.write(t.toByteArray());
                                }
                                serverBuffer.put(entry.getKey(), out.toByteArray());
                            }
                            serverList.clear();
                        }
                    }
                    // Send Content
                    for (Entry<UUID, byte[]> entry : serverBuffer.entrySet()) {
                        while (entry.getValue().length > 32 * 1024) {
                            byte[] sendbuffer = Arrays.copyOf(entry.getValue(), 1024 * 32);
                            byte[] newbuffer = Arrays.copyOfRange(entry.getValue(), 1024 * 32, entry.getValue().length);
                            entry.setValue(newbuffer);
                            byte[] compressed = ServerPacketBufferHandlerThread.compress(sendbuffer);
                            MainProxy.sendPacketToPlayer(
                                    PacketHandler.getPacket(BufferTransfer.class).setContent(compressed),
                                    entry.getKey());
                        }
                        byte[] sendbuffer = entry.getValue();
                        byte[] compressed = ServerPacketBufferHandlerThread.compress(sendbuffer);
                        MainProxy.sendPacketToPlayer(
                                PacketHandler.getPacket(BufferTransfer.class).setContent(compressed),
                                entry.getKey());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serverBuffer.clear();
                synchronized (serverList) {
                    while (pause || serverList.isEmpty()) {
                        try {
                            serverList.wait();
                        } catch (InterruptedException ignored) {}
                    }
                }
                synchronized (playersToClear) {
                    UUID player;
                    do {
                        player = playersToClear.poll();
                        if (player != null) {
                            serverBuffer.remove(player);
                        }
                    } while (player != null);
                }
            }
        }

        public void addPacketToCompressor(ModernPacket packet, EntityPlayer player) {
            synchronized (serverList) {
                LinkedList<ModernPacket> packetList = serverList
                        .computeIfAbsent(player.getUniqueID(), k -> new LinkedList<>());
                packetList.add(packet);
                if (!pause) {
                    serverList.notify();
                }
            }
        }

        public void setPause(boolean flag) {
            synchronized (serverList) {
                pause = flag;
                if (!pause) {
                    serverList.notify();
                }
            }
        }

        public void clear(EntityPlayer player) {
            synchronized (serverList) {
                serverList.remove(player.getUniqueID());
            }
            synchronized (playersToClear) {
                playersToClear.add(player.getUniqueID());
            }
        }
    }

    private final ServerCompressorThread serverCompressorThread = new ServerCompressorThread();

    private static class ServerDecompressorThread extends Thread {

        // Map of Player to received compressed C->S data
        private final HashMap<UUID, LinkedList<byte[]>> queue = new HashMap<>();
        // Map of Player to decompressed serialized C->S data
        private final HashMap<UUID, byte[]> ByteBuffer = new HashMap<>();
        // FIFO for deserialized C->S packets, decompressor adds, tickEnd removes
        private final LinkedList<Pair<UUID, byte[]>> PacketBuffer = new LinkedList<>();
        // Clear content on next tick
        private final Queue<UUID> playersToClear = new LinkedList<>();

        public ServerDecompressorThread() {
            super("LogisticsPipes Packet Decompressor Server");
            setDaemon(true);
            start();
        }

        public void serverTickEnd() {
            boolean flag;
            do {
                flag = false;
                Pair<UUID, byte[]> part = null;
                synchronized (PacketBuffer) {
                    if (!PacketBuffer.isEmpty()) {
                        flag = true;
                        part = PacketBuffer.pop();
                    }
                }
                if (flag) {
                    try {
                        PacketHandler.onPacketData(new LPDataInputStream(part.getValue2()), part.getValue1());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } while (flag);

            synchronized (playersToClear) {
                UUID player;
                do {
                    player = playersToClear.poll();
                    if (player != null) {
                        ByteBuffer.remove(player);
                    }
                } while (player != null);
            }
        }

        @Override
        public void run() {
            while (true) {
                boolean flag;
                do {
                    flag = false;
                    byte[] buffer = null;
                    UUID playerId = null;
                    synchronized (queue) {
                        if (!queue.isEmpty()) {
                            for (Iterator<Entry<UUID, LinkedList<byte[]>>> it = queue.entrySet().iterator(); it
                                    .hasNext();) {
                                Entry<UUID, LinkedList<byte[]>> entry = it.next();
                                if (!entry.getValue().isEmpty()) {
                                    flag = true;
                                    buffer = entry.getValue().getFirst();
                                    playerId = entry.getKey();
                                    if (entry.getValue().size() > 1) {
                                        entry.getValue().removeFirst();
                                    } else {
                                        it.remove();
                                    }
                                    break;
                                } else {
                                    it.remove();
                                }
                            }
                        }
                    }
                    if (flag && buffer != null && playerId != null) {
                        byte[] ByteBufferForPlayer = ByteBuffer.computeIfAbsent(playerId, k -> new byte[] {});
                        byte[] packetbytes = ServerPacketBufferHandlerThread.decompress(buffer);
                        byte[] newBuffer = new byte[packetbytes.length + ByteBufferForPlayer.length];
                        System.arraycopy(ByteBufferForPlayer, 0, newBuffer, 0, ByteBufferForPlayer.length);
                        System.arraycopy(packetbytes, 0, newBuffer, ByteBufferForPlayer.length, packetbytes.length);
                        ByteBuffer.put(playerId, newBuffer);
                    }
                } while (flag);

                for (Entry<UUID, byte[]> entry : ByteBuffer.entrySet()) {
                    while (entry.getValue().length >= 4) {
                        byte[] ByteBufferForPlayer = entry.getValue();
                        int size = ((ByteBufferForPlayer[0] & 255) << 24) + ((ByteBufferForPlayer[1] & 255) << 16)
                                + ((ByteBufferForPlayer[2] & 255) << 8)
                                + ((ByteBufferForPlayer[3] & 255) << 0);
                        if (size + 4 > ByteBufferForPlayer.length) {
                            break;
                        }
                        byte[] packet = Arrays.copyOfRange(ByteBufferForPlayer, 4, size + 4);
                        ByteBufferForPlayer = Arrays
                                .copyOfRange(ByteBufferForPlayer, size + 4, ByteBufferForPlayer.length);
                        entry.setValue(ByteBufferForPlayer);
                        synchronized (PacketBuffer) {
                            PacketBuffer.add(new Pair<>(entry.getKey(), packet));
                        }
                    }
                }
                ByteBuffer.values().removeIf(ByteBufferForPlayer -> ByteBufferForPlayer.length == 0);

                synchronized (queue) {
                    while (queue.isEmpty()) {
                        try {
                            queue.wait();
                        } catch (InterruptedException ignored) {}
                    }
                }
            }
        }

        public void handlePacket(byte[] content, EntityPlayer player) {
            synchronized (queue) {
                LinkedList<byte[]> list = queue.computeIfAbsent(player.getUniqueID(), k -> new LinkedList<>());
                list.addLast(content);
                queue.notify();
            }
        }

        public void clear(EntityPlayer player) {
            synchronized (queue) {
                queue.remove(player.getUniqueID());
            }
            synchronized (playersToClear) {
                playersToClear.add(player.getUniqueID());
            }
        }
    }

    private final ServerDecompressorThread serverDecompressorThread = new ServerDecompressorThread();

    public ServerPacketBufferHandlerThread() {}

    public void serverTick(ServerTickEvent event) {
        if (event.phase != Phase.END) {
            return;
        }
        serverDecompressorThread.serverTickEnd();
    }

    public void setPause(boolean flag) {
        serverCompressorThread.setPause(flag);
    }

    public void addPacketToCompressor(ModernPacket packet, EntityPlayer player) {
        serverCompressorThread.addPacketToCompressor(packet, player);
    }

    public void handlePacket(byte[] content, EntityPlayer player) {
        serverDecompressorThread.handlePacket(content, player);
    }

    private static byte[] compress(byte[] content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(contentBytes));
            int buffer;
            while ((buffer = gzip.read()) != -1) {
                out.write(buffer);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }

    public void clear(final EntityPlayer player) {
        new Thread(() -> {
            serverCompressorThread.clear(player);
            serverDecompressorThread.clear(player);
        }).start();
    }
}
