package logisticspipes.network.packets.pipe;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.client.FMLClientHandler;
import logisticspipes.gui.GuiInvSysConnector;
import logisticspipes.network.abstractpackets.InventoryModuleCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class InvSysConContent extends InventoryModuleCoordinatesPacket {

    public InvSysConContent(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new InvSysConContent(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiInvSysConnector) {
            ((GuiInvSysConnector) FMLClientHandler.instance().getClient().currentScreen)
                    .handleContentAnswer(getIdentList());
        }
    }
}
