package logisticspipes.network.packets.modules;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.client.FMLClientHandler;
import logisticspipes.gui.GuiProviderPipe;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.pipes.PipeItemsProviderLogistics;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;

public class ProviderPipeInclude extends IntegerCoordinatesPacket {

    public ProviderPipeInclude(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new ProviderPipeInclude(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        final LogisticsTileGenericPipe pipe = this.getPipe(player.worldObj);
        if (pipe == null) {
            return;
        }
        if (!(pipe.pipe instanceof PipeItemsProviderLogistics)) {
            return;
        }
        ((PipeItemsProviderLogistics) pipe.pipe).setFilterExcluded(getInteger() == 1);
        if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiProviderPipe) {
            ((GuiProviderPipe) FMLClientHandler.instance().getClient().currentScreen).refreshInclude();
        }
    }
}
