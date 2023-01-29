package logisticspipes.proxy.interfaces;

import java.util.List;

import net.minecraft.tileentity.TileEntity;

public interface IEnderIOProxy {

    boolean isHyperCube(TileEntity tile);

    boolean isTransceiver(TileEntity tile);

    List<TileEntity> getConnectedHyperCubes(TileEntity tile);

    List<TileEntity> getConnectedTransceivers(TileEntity tile);

    boolean isSendAndReceive(TileEntity tile);

    boolean isEnderIO();
}
