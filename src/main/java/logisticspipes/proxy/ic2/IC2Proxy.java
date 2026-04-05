package logisticspipes.proxy.ic2;

import ic2.api.item.ElectricItem;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;

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

public class IC2Proxy implements IIC2Proxy {

    @Override
    public boolean isElectricItem(ItemStack stack) {
        return stack != null && stack.getItem() != null && (stack.getItem() instanceof IElectricItem);
    }

    @Override
    public boolean isSimilarElectricItem(ItemStack stack, ItemStack template) {
        if (stack == null || template == null || !isElectricItem(stack) || !isElectricItem(template)) return false;
        var electricItem = (IElectricItem) template.getItem();
        return electricItem.getEmptyItem(stack) == stack.getItem() || electricItem.getChargedItem(stack) == stack.getItem();
    }

    public double getCurrentCharge(ItemStack stack) {
        if (isElectricItem(stack)) return ElectricItem.manager.getCharge(stack);
        else return 0;
    }

    public double getMaxCharge(ItemStack stack) {
        if (!isElectricItem(stack)) return 0.0;
        return ((IElectricItem) stack.getItem()).getMaxCharge(stack);
    }

    public double getVoltage(ItemStack stack) {
        if (!isElectricItem(stack)) return 0.0;
        return ((IElectricItem) stack.getItem()).getTransferLimit(stack);
    }

    @Override
    public double chargeElectricItem(ItemStack stack, double amount) {
        if (!isElectricItem(stack)) return 0.0;
        var charge = Math.min(Math.min(getMaxCharge(stack) - getCurrentCharge(stack), amount), getVoltage(stack));
        return ElectricItem.manager.charge(stack, charge, Integer.MAX_VALUE, true, false);
    }

    @Override
    public double dischargeElectricItem(ItemStack stack, double amount) {
        if (!isElectricItem(stack)) return 0.0;
        var discharge = Math.min(Math.min(getCurrentCharge(stack), amount), getVoltage(stack));
        ElectricItem.manager.discharge(stack, discharge, Integer.MAX_VALUE, true, false, false);
        return discharge;
    }

    @Override
    public boolean isFullyCharged(ItemStack stack) {
        return isElectricItem(stack) && getCurrentCharge(stack) == getMaxCharge(stack);
    }

    @Override
    public boolean isFullyDischarged(ItemStack stack) {
        return isElectricItem(stack) && getCurrentCharge(stack) == 0.0;
    }

    @Override
    public boolean isPartiallyCharged(ItemStack stack) {
        return isElectricItem(stack) && getCurrentCharge(stack) > 0.0 && getCurrentCharge(stack) < getMaxCharge(stack);
    }

    @Override
    public void addCraftingRecipes(ICraftingParts parts) {
        if (LogisticsPipes.isGTNH) {
            return;
        }
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
                            LogisticsPipes.LogisticsSolidBlock,
                            1,
                            LogisticsSolidBlock.LOGISTICS_IC2_POWERPROVIDER),
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
            ItemStack packager = new ItemStack(
                    LogisticsPipes.LogisticsPipeComponents,
                    1,
                    ItemPipeComponents.ITEM_MICROPACKAGER);
            ItemStack expand = new ItemStack(
                    LogisticsPipes.LogisticsPipeComponents,
                    1,
                    ItemPipeComponents.ITEM_LOGICEXPANDER);
            ItemStack lense = new ItemStack(
                    LogisticsPipes.LogisticsPipeComponents,
                    1,
                    ItemPipeComponents.ITEM_FOCUSLENSE);
            ItemStack accept = new ItemStack(
                    LogisticsPipes.LogisticsPipeComponents,
                    1,
                    ItemPipeComponents.ITEM_POWERACCEPT);

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
                            LogisticsPipes.LogisticsSolidBlock,
                            1,
                            LogisticsSolidBlock.LOGISTICS_IC2_POWERPROVIDER),
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

    @Override
    public void registerToEneryNet(TileEntity tile) {
        if (MainProxy.isServer(tile.getWorldObj())) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileLoadEvent((IEnergyTile) tile));
        }
    }

    @Override
    public void unregisterToEneryNet(TileEntity tile) {
        if (MainProxy.isServer(tile.getWorldObj())) {
            MinecraftForge.EVENT_BUS.post(new EnergyTileUnloadEvent((IEnergyTile) tile));
        }
    }

    @Override
    public boolean hasIC2() {
        return true;
    }

    @Override
    public boolean acceptsEnergyFrom(TileEntity sink, TileEntity source, ForgeDirection opposite) {
        if (sink instanceof IEnergySink iEnergySink) {
            return iEnergySink.acceptsEnergyFrom(source, opposite);
        }
        return false;
    }

    @Override
    public boolean isEnergySink(TileEntity tile) {
        return tile instanceof IEnergySink;
    }

    @Override
    public double demandedEnergyUnits(TileEntity sink) {
        if (sink instanceof IEnergySink iEnergySink) {
            return iEnergySink.getDemandedEnergy();
        }
        return 0;
    }

    @Override
    public double injectEnergyUnits(TileEntity sink, ForgeDirection opposite, double amount) {
        if (sink instanceof IEnergySink iEnergySink) {
            return iEnergySink.injectEnergy(opposite, amount, 1);
        }
        return 0;
    }
}
