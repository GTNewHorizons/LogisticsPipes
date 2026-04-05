package logisticspipes.proxy.interfaces;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IIC2Proxy extends IEUProxy {

    /**
     * @param stack The stack to check.
     * @return Boolean, true if itemstack is a ic2 electric item.
     */
    boolean isElectricItem(ItemStack stack);

    /**
     * @param stack    The stack to check
     * @param template The stack to compare to
     * @return Boolean, true if stack is the same type of ic2 electric item as template.
     */
    boolean isSimilarElectricItem(ItemStack stack, ItemStack template);

    /**
     * @param stack The stack to get charge for.
     * @return Current charge on electric item.
     */
    double getCurrentCharge(ItemStack stack);

    /**
     * @param stack The stack to get max charge for.
     * @return Maximum charge on electric item.
     */
    double getMaxCharge(ItemStack stack);

    /**
     * @param stack The stack to get voltage for.
     * @return voltage of electric item.
     */
    double getVoltage(ItemStack stack);

    /**
     * Charges an electric item. Returns the amount of charge that was accepted.
     * @param stack The stack to charge.
     * @param amount The amount of charge to attempt to add.
     * @return The amount of charge that was accepted.
     */
    double chargeElectricItem(ItemStack stack, double amount);

    /**
     * Discharges an electric item. Returns the amount of charge that was removed.
     * @param stack The stack to discharge.
     * @param amount The amount of charge to attempt to remove.
     * @return The amount of charge that was removed.
     */
    double dischargeElectricItem(ItemStack stack, double amount);

    /**
     * @param stack The stack to check if its fully charged.
     * @return Boolean, true if electric item is fully charged.
     */
    boolean isFullyCharged(ItemStack stack);

    /**
     * @param stack The stack to check if its fully discharged.
     * @return Boolean, true if electric item is fully discharged.
     */
    boolean isFullyDischarged(ItemStack stack);

    /**
     * @param stack The stack to check if its partially charged.
     * @return Boolean, true if electric item contains charge but is not full.
     */
    boolean isPartiallyCharged(ItemStack stack);

    /**
     * Adds crafting recipes to "IC2 Crafting"
     */
    void addCraftingRecipes(ICraftingParts parts);

    /**
     * @return If IC2 is loaded, returns true.
     */
    boolean hasIC2();

    /**
     * Registers an TileEntity to the IC2 EnergyNet
     *
     * @param tile has to be an instance of IEnergyTile
     */
    void registerToEneryNet(TileEntity tile);

    /**
     * Removes an TileEntity from the IC2 EnergyNet
     *
     * @param tile has to be an instance of IEnergyTile
     */
    void unregisterToEneryNet(TileEntity tile);

    @Override
    default long maxInputAmperage(TileEntity sink) {
        return 1;
    }

    default long injectEnergyUnits(TileEntity sink, ForgeDirection sinkSide, long amount, long amperage) {
        return (long) injectEnergyUnits(sink, sinkSide, amount);
    }

    double injectEnergyUnits(TileEntity tile, ForgeDirection opposite, double d);
}
