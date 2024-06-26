package logisticspipes.utils.transactor;

import java.util.Iterator;

import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

class InventoryIteratorSided implements Iterable<IInvSlot> {

    private final ISidedInventory inv;
    private final int side;

    InventoryIteratorSided(ISidedInventory inv, ForgeDirection side) {
        this.inv = inv;
        this.side = side.ordinal();
    }

    @Override
    public Iterator<IInvSlot> iterator() {
        return new Iterator<IInvSlot>() {

            final int[] slots = inv.getAccessibleSlotsFromSide(side);
            int index = 0;

            @Override
            public boolean hasNext() {
                return index < slots.length;
            }

            @Override
            public IInvSlot next() {
                return new InvSlot(slots[index++]);
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Remove not supported.");
            }
        };
    }

    private class InvSlot implements IInvSlot {

        private final int slot;

        public InvSlot(int slot) {
            this.slot = slot;
        }

        @Override
        public ItemStack getStackInSlot() {
            return inv.getStackInSlot(slot);
        }

        @Override
        public void setStackInSlot(ItemStack stack) {
            inv.setInventorySlotContents(slot, stack);
        }

        @Override
        public boolean canPutStackInSlot(ItemStack stack) {
            return inv.canInsertItem(slot, stack, side);
        }
    }
}
