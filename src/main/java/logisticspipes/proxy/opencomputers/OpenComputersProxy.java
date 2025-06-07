package logisticspipes.proxy.opencomputers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import logisticspipes.blocks.LogisticsSolidTileEntity;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.interfaces.IOpenComputersProxy;

public class OpenComputersProxy implements IOpenComputersProxy {

    @Override
    public void initLogisticsTileGenericPipe(LogisticsTileGenericPipe tile) {
        tile.node = Network.newNode(tile, Visibility.Network).withComponent("logisticspipe", Visibility.Network)
                .create();
    }

    @Override
    public void initLogisticsSolidTileEntity(LogisticsSolidTileEntity tile) {
        tile.node = Network.newNode(tile, Visibility.Network).withComponent("logisticssolidblock", Visibility.Network)
                .create();
    }

    @Override
    public void addToNetwork(TileEntity tile) {
        Network.joinOrCreateNetwork(tile);
    }

    @Override
    public void handleInvalidate(IOCTile tile) {
        if (tile.getOCNode() != null) {
            ((Node) tile.getOCNode()).remove();
        }
    }

    @Override
    public void handleChunkUnload(IOCTile tile) {
        if (tile.getOCNode() != null) {
            ((Node) tile.getOCNode()).remove();
        }
    }

    @Override
    public void handleReadFromNBT(IOCTile tile, NBTTagCompound nbt) {
        if (tile.getOCNode() != null && ((Node) tile.getOCNode()).host() == tile) {
            ((Node) tile.getOCNode()).load(nbt.getCompoundTag("oc:node"));
        }
    }

    @Override
    public void handleWriteToNBT(IOCTile tile, NBTTagCompound nbt) {
        if (tile.getOCNode() != null && ((Node) tile.getOCNode()).host() == tile) {
            final NBTTagCompound nodeNbt = new NBTTagCompound();
            ((Node) tile.getOCNode()).save(nodeNbt);
            nbt.setTag("oc:node", nodeNbt);
        }
    }

    @Override
    public void queueEvent(String event, Object[] arguments, LogisticsTileGenericPipe tile) {
        if (tile.getOCNode() != null) {
            // Send signal to OpenComputers using the proper API
            // The signal name is the event name, and arguments are passed as-is
            Object[] signalArgs = new Object[arguments.length + 1];
            signalArgs[0] = event;
            System.arraycopy(arguments, 0, signalArgs, 1, arguments.length);
            ((Node) tile.getOCNode()).sendToReachable("computer.signal", signalArgs);
        }
    }
}
