package logisticspipes.proxy.interfaces;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import logisticspipes.blocks.LogisticsSolidTileEntity;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.opencomputers.IOCTile;

public interface IOpenComputersProxy {

    void initLogisticsTileGenericPipe(LogisticsTileGenericPipe tile);

    void initLogisticsSolidTileEntity(LogisticsSolidTileEntity tile);

    void addToNetwork(TileEntity tile);

    void handleInvalidate(IOCTile tile);

    void handleChunkUnload(IOCTile tile);

    void handleWriteToNBT(IOCTile tile, NBTTagCompound nbt);

    void handleReadFromNBT(IOCTile tile, NBTTagCompound nbt);

    void pushSignal(String event, Object[] arguments, IOCTile tile);

    void handleMesssage(Object sourceId, String receiveId, Object message, IOCTile tile);

    String getAddress(IOCTile tile);

    boolean isServerSide(Thread thread);

    Object getWrappedObject(Object object);
}
