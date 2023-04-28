package logisticspipes.proxy.enderio;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.tileentity.TileEntity;

import crazypants.enderio.machine.hypercube.HyperCubeRegister;
import crazypants.enderio.machine.hypercube.TileHyperCube;
import crazypants.enderio.machine.hypercube.TileHyperCube.IoMode;
import crazypants.enderio.machine.hypercube.TileHyperCube.SubChannel;
import crazypants.enderio.machine.transceiver.Channel;
import crazypants.enderio.machine.transceiver.ChannelType;
import crazypants.enderio.machine.transceiver.ServerChannelRegister;
import crazypants.enderio.machine.transceiver.TileTransceiver;
import logisticspipes.proxy.interfaces.IEnderIOProxy;

public class EnderIOProxy implements IEnderIOProxy {

    @Override
    public boolean isHyperCube(TileEntity tile) {
        return tile instanceof TileHyperCube;
    }

    @Override
    public boolean isTransceiver(TileEntity tile) {
        return tile instanceof TileTransceiver;
    }

    @Override
    public List<TileEntity> getConnectedHyperCubes(TileEntity tile) {
        List<TileHyperCube> cons = HyperCubeRegister.instance.getCubesForChannel(((TileHyperCube) tile).getChannel());
        List<TileEntity> tiles = new ArrayList<>();
        for (TileHyperCube cube : cons) {
            if (cube != tile) {
                tiles.add(cube);
            }
        }
        return tiles;
    }

    @Override
    public List<TileEntity> getConnectedTransceivers(TileEntity tile) {
        TileTransceiver transceiver = (TileTransceiver) tile;
        List<TileEntity> tiles = new ArrayList<>();
        Channel channel = transceiver.getRecieveChannels(ChannelType.ITEM).iterator().next();
        for (TileTransceiver t : ServerChannelRegister.instance.getIterator(channel)) {
            if (t == transceiver) {
                continue;
            }
            Set<Channel> receiveChannels = t.getRecieveChannels(ChannelType.ITEM);
            Set<Channel> sendChannels = t.getSendChannels(ChannelType.ITEM);
            if (receiveChannels.size() == 1 && sendChannels.size() == 1
                    && channel.equals(receiveChannels.iterator().next())
                    && channel.equals(sendChannels.iterator().next())) {
                tiles.add(t);
            }
        }
        return tiles;
    }

    @Override
    public boolean isSendAndReceive(TileEntity tile) {
        if (tile instanceof TileHyperCube) {
            return IoMode.BOTH == ((TileHyperCube) tile).getModeForChannel(SubChannel.ITEM);
        }
        if (tile instanceof TileTransceiver) {
            Set<Channel> receiveChannels = ((TileTransceiver) tile).getRecieveChannels(ChannelType.ITEM);
            Set<Channel> sendChannels = ((TileTransceiver) tile).getSendChannels(ChannelType.ITEM);
            return receiveChannels.size() == 1 && sendChannels.size() == 1
                    && receiveChannels.iterator().next().equals(sendChannels.iterator().next());
        }
        return false;
    }

    @Override
    public boolean isEnderIO() {
        return true;
    }
}
