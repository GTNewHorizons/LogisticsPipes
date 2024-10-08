package logisticspipes.network.packets.pipe;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.pipes.PipeItemsFluidSupplier;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.MainProxy;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class FluidSupplierMode extends IntegerCoordinatesPacket {

    public FluidSupplierMode(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new FluidSupplierMode(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        final LogisticsTileGenericPipe pipe = this.getPipe(player.worldObj);
        if (pipe == null) {
            return;
        }
        if (MainProxy.isClient(player.worldObj)) {
            if (pipe.pipe instanceof PipeItemsFluidSupplier) {
                ((PipeItemsFluidSupplier) pipe.pipe).setRequestingPartials((getInteger() % 10) == 1);
            }
        } else {
            if (pipe.pipe instanceof PipeItemsFluidSupplier) {
                PipeItemsFluidSupplier liquid = (PipeItemsFluidSupplier) pipe.pipe;
                liquid.setRequestingPartials((getInteger() % 10) == 1);
            }
        }
    }
}
