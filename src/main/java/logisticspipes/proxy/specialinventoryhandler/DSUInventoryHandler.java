package logisticspipes.proxy.specialinventoryhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import powercrystals.minefactoryreloaded.api.IDeepStorageUnit;

public class DSUInventoryHandler extends DSULikeInventoryHandler {

    private final IDeepStorageUnit _tile;

    private DSUInventoryHandler(TileEntity tile, boolean hideOnePerStack, boolean hideOne, int cropStart, int cropEnd) {
        _tile = (IDeepStorageUnit) tile;
        _hideOnePerStack = hideOnePerStack || hideOne;
    }

    public DSUInventoryHandler() {
        _tile = null;
        _hideOnePerStack = false;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean isType(TileEntity tile) {
        return tile instanceof IDeepStorageUnit;
    }

    @Override
    public SpecialInventoryHandler getUtilForTile(TileEntity tile, ForgeDirection dir, boolean hideOnePerStack,
            boolean hideOne, int cropStart, int cropEnd) {
        return new DSUInventoryHandler(tile, hideOnePerStack, hideOne, cropStart, cropEnd);
    }

    @Override
    boolean isEmpty() {
        return _tile.getStoredItemType() != null;
    }

    @Override
    int getSize() {
        return _tile.getMaxStoredCount();
    }

    @Override
    int getCurrent() {
        return isEmpty() ? 0 : _tile.getStoredItemType().stackSize;
    }

    @Override
    ItemStack getType() {
        return _tile.getStoredItemType();
    }

    @Override
    void setContent(int count) {
        _tile.setStoredItemCount(count);
    }

    @Override
    void setContent(ItemStack stack, int size) {
        _tile.setStoredItemType(stack, size);
    }

    @Override
    void markDirty() {
        ((TileEntity) _tile).markDirty();
    }
}
