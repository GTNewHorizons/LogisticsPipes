package logisticspipes.network.guis.block;

import logisticspipes.blocks.powertile.LogisticsIC2PowerProviderTileEntity;
import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.powertile.LogisticsPowerProviderTileEntity;
import logisticspipes.gui.GuiPowerProvider;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.abstractguis.GuiProvider;
import logisticspipes.utils.gui.DummyContainer;

public class PowerProviderGui extends CoordinatesGuiProvider {

    public PowerProviderGui(int id) {
        super(id);
    }

    @Override
    public Object getClientGui(EntityPlayer player) {
        var tile = this.getTile(player.getEntityWorld(), LogisticsPowerProviderTileEntity.class);
        if (tile == null) return null;

        var gui = new GuiPowerProvider(player, tile);
        gui.inventorySlots = getContainer(player);
        return gui;
    }

    @Override
    public DummyContainer getContainer(EntityPlayer player) {
        var tile = this.getTile(player.getEntityWorld(), LogisticsPowerProviderTileEntity.class);
        if (tile == null) return null;

        if (tile instanceof LogisticsIC2PowerProviderTileEntity ic2Power) {
            DummyContainer dummy = new DummyContainer(player, ic2Power, ic2Power);
            for (int i = 0; i < 9; i++){
                int finalI = i;
                dummy.addRestrictedSlot(i, ic2Power, 8 + 18 * i,58, itemStack -> ic2Power.checkSlot(finalI, itemStack));
            }
            dummy.addNormalSlotsForPlayerInventory(8, 80);
            return dummy;
        }

        DummyContainer dummy = new DummyContainer(player, null, tile);
        dummy.addNormalSlotsForPlayerInventory(8, 80);
        return dummy;
    }

    @Override
    public GuiProvider template() {
        return new PowerProviderGui(getId());
    }
}
