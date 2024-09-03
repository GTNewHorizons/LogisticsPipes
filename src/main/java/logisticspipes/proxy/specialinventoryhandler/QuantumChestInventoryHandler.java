package logisticspipes.proxy.specialinventoryhandler;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.common.tileentities.storage.MTEQuantumChest;

public class QuantumChestInventoryHandler extends DSULikeInventoryHandler {

    private final MTEQuantumChest _mte;

    private QuantumChestInventoryHandler(MTEQuantumChest tile, boolean hideOnePerStack, boolean hideOne, int cropStart,
            int cropEnd) {
        _mte = tile;
        _hideOnePerStack = hideOnePerStack || hideOne;
    }

    public QuantumChestInventoryHandler() {
        _mte = null;
        _hideOnePerStack = false;
    }

    @Override
    public boolean init() {
        return true;
    }

    @Override
    public boolean isType(TileEntity tile) {
        if (!(tile instanceof IGregTechTileEntity)) return false;
        return ((IGregTechTileEntity) tile).getMetaTileEntity() instanceof MTEQuantumChest;
    }

    @Override
    public SpecialInventoryHandler getUtilForTile(TileEntity tile, ForgeDirection dir, boolean hideOnePerStack,
            boolean hideOne, int cropStart, int cropEnd) {
        return new QuantumChestInventoryHandler(
                ((MTEQuantumChest) ((IGregTechTileEntity) tile).getMetaTileEntity()),
                hideOnePerStack,
                hideOne,
                cropStart,
                cropEnd);
    }

    @Override
    boolean isEmpty() {
        return _mte.mItemCount == 0 || _mte.mItemStack == null;
    }

    @Override
    int getSize() {
        return _mte.getMaxItemCount();
    }

    @Override
    int getCurrent() {
        return _mte.mItemCount;
    }

    @Override
    ItemStack getType() {
        return _mte.mItemStack;
    }

    @Override
    void setContent(int count) {
        _mte.mItemCount = count;
    }

    @Override
    void setContent(ItemStack stack, int size) {
        _mte.mItemStack = stack.copy();
        _mte.mItemCount = size;
    }

    @Override
    void markDirty() {
        _mte.markDirty();
    }
}
