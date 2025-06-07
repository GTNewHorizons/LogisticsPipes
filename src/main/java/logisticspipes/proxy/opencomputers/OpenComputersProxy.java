package logisticspipes.proxy.opencomputers;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import li.cil.oc.api.Network;
import li.cil.oc.api.machine.Architecture;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.Visibility;
import logisticspipes.blocks.LogisticsSolidTileEntity;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.cc.CCConstants;
import logisticspipes.proxy.computers.wrapper.CCObjectWrapper;
import logisticspipes.proxy.interfaces.IOpenComputersProxy;
import logisticspipes.proxy.opencomputers.asm.BaseWrapperClass;

public class OpenComputersProxy implements IOpenComputersProxy {

    static final private Set<String> targetName = new HashSet<>();
    static final private Set<String> skipName = new HashSet<>();

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
    public void pushSignal(String event, Object[] arguments, IOCTile tile) {
        if (tile.getOCNode() != null) {
            Object[] signalArgs = new Object[arguments.length + 1];
            signalArgs[0] = event;
            System.arraycopy(arguments, 0, signalArgs, 1, arguments.length);
            ((Node) tile.getOCNode()).sendToReachable("computer.signal", signalArgs);
        }
    }

    @Override
    public void handleMesssage(Object sourceId, String receiveId, Object message, IOCTile tile) {
        if (tile.getOCNode() != null && ((Node) tile.getOCNode()).address().equals(receiveId)) {
            ((Node) tile.getOCNode())
                    .sendToNeighbors("computer.signal", CCConstants.LP_CC_MESSAGE_EVENT, sourceId, message);
        }
    }

    @Override
    public String getAddress(IOCTile tile) {
        return (tile.getOCNode() != null) ? ((Node) tile.getOCNode()).address() : null;
    }

    @Override
    public boolean isServerSide(Thread thread) {
        StackTraceElement[] ste = thread.getStackTrace();
        if (ste.length < 8) return false; // 2 operation instead of 8
        for (int i = ste.length - 8; i >= 0; i--) {
            if (skipName.contains(ste[i].getClassName())) continue;
            if (targetName.contains(ste[i].getClassName())) return true;
            else try {
                Class<?> clazz = Class.forName(ste[i].getClassName());
                if (Architecture.class.isAssignableFrom(clazz)) {
                    targetName.add(ste[i].getClassName());
                    return true;
                }
            } catch (ClassNotFoundException ignored) {}
            skipName.add(ste[i].getClassName());
        }
        return false;
    }

    @Override
    public Object getWrappedObject(Object object) {
        return CCObjectWrapper.getWrappedObject(object, BaseWrapperClass.WRAPPER);
    }
}
