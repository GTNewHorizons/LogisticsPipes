package logisticspipes.proxy.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IIC2Proxy extends IEUProxy {

    boolean isElectricItem(ItemStack stack);

    boolean isSimilarElectricItem(ItemStack stack, ItemStack template);

    boolean isFullyCharged(ItemStack stack);

    boolean isFullyDischarged(ItemStack stack);

    boolean isPartiallyCharged(ItemStack stack);

    void addCraftingRecipes(ICraftingParts parts);

    boolean hasIC2();

    void registerToEneryNet(TileEntity tile);

    void unregisterToEneryNet(TileEntity tile);

    @Override
    default long maxInputAmperage(TileEntity sink) {
        return 1;
    }

    default long injectEnergyUnits(TileEntity sink, ForgeDirection sinkSide, long amount, long amperage){
        return (long) injectEnergyUnits(sink, sinkSide, amount);
    }

    double injectEnergyUnits(TileEntity tile, ForgeDirection opposite, double d);
}
