package logisticspipes.network.packets.pipe;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.abstractpackets.IntegerModuleCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class FluidCraftingPipeAdvancedSatellitePrevPacket extends IntegerModuleCoordinatesPacket {

    public FluidCraftingPipeAdvancedSatellitePrevPacket(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new FluidCraftingPipeAdvancedSatellitePrevPacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        ModuleCrafter module = this.getLogisticsModule(player, ModuleCrafter.class);
        if (module == null) {
            return;
        }
        module.setPrevFluidSatellite(player, getInteger());
    }
}
