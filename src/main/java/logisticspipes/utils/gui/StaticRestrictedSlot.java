package logisticspipes.utils.gui;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;

import logisticspipes.interfaces.ISlotCheck;

public class StaticRestrictedSlot extends RestrictedSlot {

    int limit;

    public StaticRestrictedSlot(IInventory iinventory, int i, int j, int k, Item item, int stackLimit) {
        super(iinventory, i, j, k, item);
        limit = stackLimit;
    }

    public StaticRestrictedSlot(IInventory iinventory, int i, int j, int k, ISlotCheck slotCheck, int stackLimit) {
        super(iinventory, i, j, k, slotCheck);
        limit = stackLimit;
    }

    /**
     * Return whether this slot's stack can be taken from this slot.
     */
    @Override
    public boolean canTakeStack(EntityPlayer par1EntityPlayer) {
        return false;
    }

    /**
     * Returns the maximum stack size for a given slot (usually the same as getInventoryStackLimit(), but 1 in the case
     * of armor slots)
     */
    @Override
    public int getSlotStackLimit() {
        return limit;
    }
}
