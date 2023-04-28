package logisticspipes.transport;

import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;

import logisticspipes.pipes.PipeItemsInvSysConnector;
import logisticspipes.routing.ItemRoutingInformation;
import logisticspipes.utils.item.ItemIdentifierStack;

public class TransportInvConnection extends PipeTransportLogistics {

    public TransportInvConnection() {
        super(true);
    }

    @Override
    protected boolean isItemExitable(ItemIdentifierStack stack) {
        return true;
    }

    @Override
    protected void insertedItemStack(ItemRoutingInformation info, TileEntity tile) {
        if (tile instanceof IInventory) {
            ((PipeItemsInvSysConnector) container.pipe).handleItemEnterInv(info, tile);
        }
    }
}
