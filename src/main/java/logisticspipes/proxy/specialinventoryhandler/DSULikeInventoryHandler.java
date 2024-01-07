package logisticspipes.proxy.specialinventoryhandler;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.utils.item.ItemIdentifier;

abstract class DSULikeInventoryHandler extends SpecialInventoryHandler {

    protected boolean _hideOnePerStack;

    @Override
    public int itemCount(ItemIdentifier itemIdent) {
        if (isEmpty() || itemIdent.tag != null) return 0;
        return getTypeIdent().equals(itemIdent) ? getCurrent() : 0;
    }

    @Override
    public ItemStack getMultipleItems(ItemIdentifier itemIdent, int count) {
        if (isEmpty()) return null;
        if (!getTypeIdent().equals(itemIdent)) {
            return null;
        }
        int current = getCurrent();
        int toTake = Math.max(Math.min(_hideOnePerStack ? current - 1 : current, count), 0);
        setContent(current - toTake);
        markDirty();
        return itemIdent.makeNormalStack(toTake);
    }

    @Override
    public Set<ItemIdentifier> getItems() {
        Set<ItemIdentifier> result = new TreeSet<>();
        if (!isEmpty()) {
            result.add(getTypeIdent());
        }
        return result;
    }

    @Override
    public HashMap<ItemIdentifier, Integer> getItemsAndCount() {
        HashMap<ItemIdentifier, Integer> result = new HashMap<>();
        if (!isEmpty()) {
            result.put(getTypeIdent(), getReportedCount());
        }
        return result;
    }

    @Override
    public ItemStack getSingleItem(ItemIdentifier itemIdent) {
        return getMultipleItems(itemIdent, 1);
    }

    @Override
    public boolean containsUndamagedItem(ItemIdentifier itemIdent) {
        return !isEmpty() && getTypeIdent().getUndamaged().equals(itemIdent);
    }

    int roomForItemNoTag(ItemStack stack) {
        if (stack.stackTagCompound != null) {
            return 0;
        }
        if (isEmpty()) {
            return getSize();
        }
        if (stack.isItemEqual(getType())) {
            return getSize() - getCurrent();
        }
        return 0;
    }

    @Override
    public int roomForItem(ItemIdentifier item) {
        return roomForItem(item, 0);
    }

    @Override
    public int roomForItem(ItemIdentifier itemIdent, int count) {
        if (itemIdent.tag != null) {
            return 0;
        }
        if (isEmpty()) {
            return getSize();
        }
        if (getTypeIdent().equals(itemIdent)) {
            return getSize() - getCurrent();
        }
        return 0;
    }

    @Override
    public ItemStack add(ItemStack stack, ForgeDirection from, boolean doAdd) {
        ItemStack st = stack.copy();
        st.stackSize = 0;
        if (stack.getTagCompound() != null) {
            return st;
        }
        st.stackSize = Math.min(roomForItemNoTag(stack), stack.stackSize);
        if (st.stackSize == 0) {
            return st;
        }
        if (doAdd) {
            if (isEmpty()) {
                setContent(st, st.stackSize);
            } else {
                setContent(getCurrent() + st.stackSize);
            }
            markDirty();
        }
        return st;
    }

    @Override
    public boolean isSpecialInventory() {
        return true;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int i) {
        if (i != 0 || isEmpty()) return null;
        ItemStack res = getType();
        res.stackSize = getReportedCount();
        return res;
    }

    @Override
    public ItemStack decrStackSize(int i, int j) {
        if (i != 0 || isEmpty()) return null;
        return getMultipleItems(ItemIdentifier.get(getType()), j);
    }

    /**
     * @return return false if this does not have anything inside, including ghosts with 0 stack size. return true
     *         otherwise
     */
    abstract boolean isEmpty();

    abstract int getSize();

    abstract int getCurrent();

    abstract ItemStack getType();

    ItemIdentifier getTypeIdent() {
        return ItemIdentifier.get(getType());
    }

    abstract void setContent(int count);

    abstract void setContent(ItemStack stack, int size);

    abstract void markDirty();

    int getReportedCount() {
        if (_hideOnePerStack) return Math.max(0, getCurrent() - 1);
        return getCurrent();
    }
}
