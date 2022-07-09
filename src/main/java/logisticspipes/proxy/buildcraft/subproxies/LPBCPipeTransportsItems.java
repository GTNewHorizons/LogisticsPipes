package logisticspipes.proxy.buildcraft.subproxies;

import buildcraft.transport.PipeTransportItems;
import buildcraft.transport.TravelingItem;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import net.minecraftforge.common.util.ForgeDirection;

public class LPBCPipeTransportsItems extends PipeTransportItems {

    private final LogisticsTileGenericPipe pipe;

    public LPBCPipeTransportsItems(LogisticsTileGenericPipe pipe) {
        this.pipe = pipe;
    }

    @Override
    public void injectItem(TravelingItem item, ForgeDirection dir) {
        pipe.pipe.transport.injectItem(item, dir);
    }
}
