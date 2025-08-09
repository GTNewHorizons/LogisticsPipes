package logisticspipes.network.guis.block;

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
        LogisticsPowerProviderTileEntity tile = this
                .getTile(player.getEntityWorld(), LogisticsPowerProviderTileEntity.class);
        if (tile == null) {
            return null;
        }

        var gui = new GuiPowerProvider(player, tile);
        gui.inventorySlots = getContainer(player);
        return gui;
    }

    @Override
    public DummyContainer getContainer(EntityPlayer player) {
        LogisticsPowerProviderTileEntity tile = this
                .getTile(player.getEntityWorld(), LogisticsPowerProviderTileEntity.class);
        if (tile == null) {
            return null;
        }
        DummyContainer dummy = new DummyContainer(player, tile, tile);
        for (int i = 0; i < 9; i++){
            int finalI = i;
            dummy.addRestrictedSlot(i, tile, 8 + 18 * i,58, itemStack -> tile.checkSlot(finalI, itemStack));
        }
        dummy.addNormalSlotsForPlayerInventory(8, 80);
        return dummy;
    }

    @Override
    public GuiProvider template() {
        return new PowerProviderGui(getId());
    }
}
