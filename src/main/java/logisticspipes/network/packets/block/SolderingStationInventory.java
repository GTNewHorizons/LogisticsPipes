package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import logisticspipes.blocks.LogisticsSolderingTileEntity;
import logisticspipes.network.abstractpackets.InventoryModuleCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class SolderingStationInventory extends InventoryModuleCoordinatesPacket {

    public SolderingStationInventory(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new SolderingStationInventory(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        final LogisticsSolderingTileEntity tile = this.getTile(player.worldObj, LogisticsSolderingTileEntity.class);
        if (tile != null) {
            for (int i = 0; i < tile.getSizeInventory(); i++) {
                if (i >= getStackList().size()) {
                    break;
                }
                ItemStack stack = getStackList().get(i);
                tile.setInventorySlotContents(i, stack);
            }
        }
    }
}
