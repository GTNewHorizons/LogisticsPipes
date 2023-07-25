package logisticspipes.network.packets.debuggui;

import java.io.*;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;

import logisticspipes.commands.chathelper.LPChatListener;
import logisticspipes.commands.commands.debug.DebugGuiController;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.packets.gui.OpenChatGui;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.string.ChatColor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class DebugTargetResponse extends ModernPacket {

    public DebugTargetResponse(int id) {
        super(id);
    }

    public enum TargetMode {
        Block,
        Entity,
        None
    }

    @Getter
    @Setter
    private TargetMode mode;

    @Getter
    @Setter
    private int[] additions = new int[0];

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        mode = TargetMode.values()[data.readByte()];
        int size = data.readInt();
        additions = new int[size];
        for (int i = 0; i < size; i++) {
            additions[i] = data.readInt();
        }
    }

    @Override
    public void processPacket(final EntityPlayer player) {
        if (mode == TargetMode.None) {
            player.addChatComponentMessage(new ChatComponentText(ChatColor.RED + "No Target Found"));
        } else if (mode == TargetMode.Block) {
            int x = additions[0];
            int y = additions[1];
            int z = additions[2];
            player.addChatComponentMessage(new ChatComponentText("Checking Block at: x:" + x + " y:" + y + " z:" + z));
            Block id = player.worldObj.getBlock(x, y, z);
            player.addChatComponentMessage(new ChatComponentText("Found Block with Id: " + id.getClass()));
            final TileEntity tile = player.worldObj.getTileEntity(x, y, z);
            if (tile == null) {
                player.addChatComponentMessage(new ChatComponentText(ChatColor.RED + "No TileEntity found"));
            } else {
                LPChatListener.addTask(() -> {
                    player.addChatComponentMessage(
                            new ChatComponentText(
                                    ChatColor.GREEN + "Starting debuging of TileEntity: "
                                            + ChatColor.BLUE
                                            + ChatColor.UNDERLINE
                                            + tile.getClass().getSimpleName()));
                    DebugGuiController.instance().startWatchingOf(tile, player);
                    MainProxy.sendPacketToPlayer(PacketHandler.getPacket(OpenChatGui.class), player);
                    return true;
                }, player);
                player.addChatComponentMessage(
                        new ChatComponentText(
                                ChatColor.AQUA + "Start debuging of TileEntity: "
                                        + ChatColor.BLUE
                                        + ChatColor.UNDERLINE
                                        + tile.getClass().getSimpleName()
                                        + ChatColor.AQUA
                                        + "? "
                                        + ChatColor.RESET
                                        + "<"
                                        + ChatColor.GREEN
                                        + "yes"
                                        + ChatColor.RESET
                                        + "/"
                                        + ChatColor.RED
                                        + "no"
                                        + ChatColor.RESET
                                        + ">"));
                MainProxy.sendPacketToPlayer(PacketHandler.getPacket(OpenChatGui.class), player);
            }
        } else if (mode == TargetMode.Entity) {
            int entityId = (Integer) additions[0];
            final Entity entity = player.worldObj.getEntityByID(entityId);
            if (entity == null) {
                player.addChatComponentMessage(new ChatComponentText(ChatColor.RED + "No Entity found"));
            } else {
                LPChatListener.addTask(() -> {
                    player.addChatComponentMessage(
                            new ChatComponentText(
                                    ChatColor.GREEN + "Starting debuging of Entity: "
                                            + ChatColor.BLUE
                                            + ChatColor.UNDERLINE
                                            + entity.getClass().getSimpleName()));
                    DebugGuiController.instance().startWatchingOf(entity, player);
                    MainProxy.sendPacketToPlayer(PacketHandler.getPacket(OpenChatGui.class), player);
                    return true;
                }, player);
                player.addChatComponentMessage(
                        new ChatComponentText(
                                ChatColor.AQUA + "Start debuging of Entity: "
                                        + ChatColor.BLUE
                                        + ChatColor.UNDERLINE
                                        + entity.getClass().getSimpleName()
                                        + ChatColor.AQUA
                                        + "? "
                                        + ChatColor.RESET
                                        + "<"
                                        + ChatColor.GREEN
                                        + "yes"
                                        + ChatColor.RESET
                                        + "/"
                                        + ChatColor.RED
                                        + "no"
                                        + ChatColor.RESET
                                        + ">"));
                MainProxy.sendPacketToPlayer(PacketHandler.getPacket(OpenChatGui.class), player);
            }
        }
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeByte(mode.ordinal());
        data.writeInt(additions.length);
        for (int addition : additions) {
            data.writeInt(addition);
        }
    }

    @Override
    public ModernPacket template() {
        return new DebugTargetResponse(getId());
    }

    @Override
    public boolean isCompressable() {
        return true;
    }
}
