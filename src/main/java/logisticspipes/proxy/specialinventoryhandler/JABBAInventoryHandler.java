package logisticspipes.proxy.specialinventoryhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import mcp.mobius.betterbarrels.common.blocks.IBarrelStorage;
import mcp.mobius.betterbarrels.common.blocks.TileEntityBarrel;

public class JABBAInventoryHandler extends DSULikeInventoryHandler {

    private final TileEntityBarrel _tile;
    private final IBarrelStorage _storage;

    private JABBAInventoryHandler(TileEntity tile, boolean hideOnePerStack, boolean hideOne, int cropStart,
            int cropEnd) {
        _tile = (TileEntityBarrel) tile;
        _storage = _tile.getStorage();
        _hideOnePerStack = hideOnePerStack || hideOne;
    }

    public JABBAInventoryHandler() {
        _tile = null;
        _storage = null;
        _hideOnePerStack = false;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean isType(TileEntity tile) {
        return tile instanceof TileEntityBarrel;
    }

    @Override
    public SpecialInventoryHandler getUtilForTile(TileEntity tile, ForgeDirection dir, boolean hideOnePerStack,
            boolean hideOne, int cropStart, int cropEnd) {
        return new JABBAInventoryHandler(tile, hideOnePerStack, hideOne, cropStart, cropEnd);
    }

    @Override
    boolean isEmpty() {
        return _tile.getStoredItemType() != null;
    }

    @Override
    int getSize() {
        return _storage.getMaxStoredCount();
    }

    @Override
    int getCurrent() {
        return isEmpty() ? 0 : _storage.getStoredItemType().stackSize;
    }

    @Override
    ItemStack getType() {
        return _storage.getStoredItemType();
    }

    @Override
    void setContent(int count) {
        _storage.setStoredItemCount(count);
    }

    @Override
    void setContent(ItemStack stack, int size) {
        _storage.setStoredItemType(stack, size);
    }

    @Override
    void markDirty() {
        _tile.markDirty();
    }
}
