package logisticspipes.proxy.ic2;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.IC2Items;
import ic2.api.item.IElectricItem;
import ic2.api.recipe.Recipes;
import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsSolidBlock;
import logisticspipes.config.Configs;
import logisticspipes.items.ItemModule;
import logisticspipes.items.ItemPipeComponents;
import logisticspipes.items.ItemUpgrade;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.interfaces.ICraftingParts;
import logisticspipes.proxy.interfaces.IIC2Proxy;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

public class IC2Proxy implements IIC2Proxy {

    /**
     * @return Boolean, true if itemstack is a ic2 electric item.
     * @param stack
     *            The stack to check.
     */
    @Override
    public boolean isElectricItem(ItemStack stack) {
        return stack != null && (stack.getItem() instanceof IElectricItem);
    }

    /**
     * @return Boolean, true if stack is the same type of ic2 electric item as
     *         template.
     * @param stack
     *            The stack to check
     * @param template
     *            The stack to compare to
     */
    @Override
    public boolean isSimilarElectricItem(ItemStack stack, ItemStack template) {
        if (stack == null || template == null || !isElectricItem(template)) {
            return false;
        }
        if (((IElectricItem) template.getItem()).getEmptyItem(stack) == stack.getItem()) {
            return true;
        }
        if (((IElectricItem) template.getItem()).getChargedItem(stack) == stack.getItem()) {
            return true;
        }
        return false;
    }

    /**
     * @return Int value of current charge on electric item.
     * @param stack
     *            The stack to get charge for.
     */
    private double getCharge(ItemStack stack) {
        if ((stack.getItem() instanceof IElectricItem) && stack.hasTagCompound()) {
            return stack.getTagCompound().getDouble("charge");
        } else {
            return 0;
        }
    }

    /**
     * @return Int value of maximum charge on electric item.
     * @param stack
     *            The stack to get max charge for.
     */
    private double getMaxCharge(ItemStack stack) {
        if (!(stack.getItem() instanceof IElectricItem)) {
            return 0;
        }
        return ((IElectricItem) stack.getItem()).getMaxCharge(stack);
    }

    /**
     * @return Boolean, true if electric item is fully charged.
     * @param stack
     *            The stack to check if its fully charged.
     */
    @Override
    public boolean isFullyCharged(ItemStack stack) {
        if (!isElectricItem(stack)) {
            return false;
        }
        if (((IElectricItem) stack.getItem()).getChargedItem(stack) != stack.getItem()) {
            return false;
        }
        double charge = getCharge(stack);
        double maxCharge = getMaxCharge(stack);
        return charge == maxCharge;
    }

    /**
     * @return Boolean, true if electric item is fully discharged.
     * @param stack
     *            The stack to check if its fully discharged.
     */
    @Override
    public boolean isFullyDischarged(ItemStack stack) {
        if (!isElectricItem(stack)) {
            return false;
        }
        if (((IElectricItem) stack.getItem()).getEmptyItem(stack) != stack.getItem()) {
            return false;
        }
        double charge = getCharge(stack);
        return charge == 0;
    }

    /**
     * @return Boolean, true if electric item contains charge but is not full.
     * @param stack
     *            The stack to check if its partially charged.
     */
    @Override
    public boolean isPartiallyCharged(ItemStack stack) {
        if (!isElectricItem(stack)) {
            return false;
        }
        if (((IElectricItem) stack.getItem()).getChargedItem(stack) != stack.getItem()) {
            return false;
        }
        double charge = getCharge(stack);
        double maxCharge = getMaxCharge(stack);
        return charge != maxCharge;
    }

    /**
     * Adds crafting recipes to "IC2 Crafting"
     */
    @Override
    public void addCraftingRecipes(ICraftingParts parts) {
        if (!Configs.ENABLE_BETA_RECIPES) {
            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICBUFFER),
                    "CGC",
                    "rBr",
                    "CrC",
                    'C',
                    IC2Items.getItem("advancedCircuit"),
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICBUFFER),
                    " G ",
                    "rBr",
                    "CrC",
                    'C',
                    IC2Items.getItem("advancedCircuit"),
                    'G',
                    parts.getChipTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    "CGD",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'D',
                    IC2Items.getItem("reBattery"),
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    "CGD",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'D',
                    IC2Items.getItem("chargedReBattery"),
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    "CGc",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'c',
                    IC2Items.getItem("reBattery"),
                    'D',
                    IC2Items.getItem("chargedReBattery"),
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    "CGc",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'c',
                    IC2Items.getItem("chargedReBattery"),
                    'D',
                    IC2Items.getItem("reBattery"),
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    " G ",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'D',
                    IC2Items.getItem("reBattery"),
                    'G',
                    parts.getChipTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    " G ",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'D',
                    IC2Items.getItem("chargedReBattery"),
                    'G',
                    parts.getChipTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_LV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_TRANSPORTATION),
                    'S',
                    IC2Items.getItem("energyStorageUpgrade"),
                    'O',
                    IC2Items.getItem("overclockerUpgrade"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_MV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_LV_SUPPLIER),
                    'S',
                    IC2Items.getItem("energyStorageUpgrade"),
                    'O',
                    IC2Items.getItem("overclockerUpgrade"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_HV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_MV_SUPPLIER),
                    'S',
                    IC2Items.getItem("energyStorageUpgrade"),
                    'O',
                    IC2Items.getItem("overclockerUpgrade"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_EV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_HV_SUPPLIER),
                    'S',
                    IC2Items.getItem("energyStorageUpgrade"),
                    'O',
                    IC2Items.getItem("overclockerUpgrade"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_IC2_POWERPROVIDER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    Blocks.redstone_block,
                    'S',
                    IC2Items.getItem("energyStorageUpgrade"),
                    'O',
                    IC2Items.getItem("overclockerUpgrade"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.paper);
        }
        if (Configs.ENABLE_BETA_RECIPES) {
            ItemStack packager =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_MICROPACKAGER);
            ItemStack expand =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_LOGICEXPANDER);
            ItemStack lense =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_FOCUSLENSE);
            ItemStack accept =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_POWERACCEPT);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICBUFFER),
                    "CGC",
                    "rBr",
                    "CrC",
                    'C',
                    IC2Items.getItem("advancedCircuit"),
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ELECTRICMANAGER),
                    "CGD",
                    "rBr",
                    "DrC",
                    'C',
                    IC2Items.getItem("electronicCircuit"),
                    'D',
                    IC2Items.getItem("reBattery"),
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_LV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    expand,
                    'S',
                    accept,
                    'O',
                    IC2Items.getItem("coil"),
                    'T',
                    IC2Items.getItem("reBattery"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_MV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    expand,
                    'S',
                    accept,
                    'O',
                    IC2Items.getItem("coil"),
                    'T',
                    IC2Items.getItem("advBattery"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_HV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    expand,
                    'S',
                    accept,
                    'O',
                    IC2Items.getItem("coil"),
                    'T',
                    IC2Items.getItem("energyCrystal"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_IC2_EV_SUPPLIER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    expand,
                    'S',
                    accept,
                    'O',
                    IC2Items.getItem("coil"),
                    'T',
                    IC2Items.getItem("lapotronCrystal"),
                    'P',
                    Items.paper);

            Recipes.advRecipes.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_IC2_POWERPROVIDER),
                    "PSP",
                    "OBO",
                    "PTP",
                    'B',
                    Blocks.glowstone,
                    'S',
                    lense,
                    'O',
                    IC2Items.getItem("coil"),
                    'T',
                    IC2Items.getItem("transformerUpgrade"),
                    'P',
                    Items.iron_ingot);
        }
    }

    /**
     * Registers an TileEntity to the IC2 EnergyNet
     *
     * @param tile has to be an instance of IEnergyTile
     */
    @Override
    public void registerToEneryNet(TileEntity tile) {
        if (MainProxy.isServer(tile.getWorldObj())) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
        }
    }

    /**
     * Removes an TileEntity from the IC2 EnergyNet
     *
     * @param tile has to be an instance of IEnergyTile
     */
    @Override
    public void unregisterToEneryNet(TileEntity tile) {
        if (MainProxy.isServer(tile.getWorldObj())) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tile));
        }
    }

    /**
     * @return If IC2 is loaded, returns true.
     */
    @Override
    public boolean hasIC2() {
        return true;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity energy, TileEntity tile, ForgeDirection opposite) {
        return ((IEnergySink) energy).acceptsEnergyFrom(tile, opposite);
    }

    @Override
    public boolean isEnergySink(TileEntity tile) {
        return tile instanceof IEnergySink;
    }

    @Override
    public double demandedEnergyUnits(TileEntity tile) {
        return ((IEnergySink) tile).getDemandedEnergy();
    }

    @Override
    public double injectEnergyUnits(TileEntity tile, ForgeDirection opposite, double d) {
        return ((IEnergySink) tile).injectEnergy(opposite, d, 1); // TODO check the voltage
    }
}
