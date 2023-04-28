package logisticspipes.proxy.cofh;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsSolidBlock;
import logisticspipes.config.Configs;
import logisticspipes.items.ItemPipeComponents;
import logisticspipes.items.ItemUpgrade;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyReceiver;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyStorage;
import logisticspipes.proxy.interfaces.ICoFHPowerProxy;
import logisticspipes.proxy.interfaces.ICraftingParts;
import logisticspipes.recipes.CraftingDependency;
import logisticspipes.recipes.RecipeManager;
import logisticspipes.recipes.RecipeManager.LocalCraftingManager;

public class CoFHPowerProxy implements ICoFHPowerProxy {

    @Override
    public boolean isEnergyReceiver(TileEntity tile) {
        return tile instanceof IEnergyReceiver;
    }

    @Override
    public ICoFHEnergyReceiver getEnergyReceiver(TileEntity tile) {
        final IEnergyReceiver handler = (IEnergyReceiver) tile;
        return new ICoFHEnergyReceiver() {

            @Override
            public int getMaxEnergyStored(ForgeDirection opposite) {
                return handler.getMaxEnergyStored(opposite);
            }

            @Override
            public int getEnergyStored(ForgeDirection opposite) {
                return handler.getEnergyStored(opposite);
            }

            @Override
            public boolean canConnectEnergy(ForgeDirection opposite) {
                return handler.canConnectEnergy(opposite);
            }

            @Override
            public int receiveEnergy(ForgeDirection opposite, int i, boolean b) {
                return handler.receiveEnergy(opposite, i, b);
            }
        };
    }

    @Override
    public void addCraftingRecipes(ICraftingParts parts) {
        LocalCraftingManager craftingManager = RecipeManager.craftingManager;
        if (!Configs.ENABLE_BETA_RECIPES) {
            craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_RF_SUPPLIER),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PEP",
                    "RBR",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_TRANSPORTATION),
                    'P',
                    Items.paper,
                    'E',
                    parts.getBlockDynamo(),
                    'T',
                    parts.getPowerCoilSilver(),
                    'R',
                    parts.getPowerCoilGold());
            craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock,
                            1,
                            LogisticsSolidBlock.LOGISTICS_RF_POWERPROVIDER),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PEP",
                    "RBR",
                    "PTP",
                    'B',
                    Blocks.redstone_block,
                    'P',
                    Items.paper,
                    'E',
                    parts.getBlockDynamo(),
                    'T',
                    parts.getPowerCoilSilver(),
                    'R',
                    parts.getPowerCoilGold());
        }
        if (Configs.ENABLE_BETA_RECIPES) {
            craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.POWER_RF_SUPPLIER),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PEP",
                    "RBR",
                    "PTP",
                    'B',
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_LOGICEXPANDER),
                    'P',
                    Items.paper,
                    'E',
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_POWERACCEPT),
                    'T',
                    parts.getPowerCoilSilver(),
                    'R',
                    parts.getPowerCoilGold());
            craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock,
                            1,
                            LogisticsSolidBlock.LOGISTICS_RF_POWERPROVIDER),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PEP",
                    "RBR",
                    "PTP",
                    'B',
                    Blocks.glowstone,
                    'P',
                    Items.iron_ingot,
                    'E',
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_FOCUSLENSE),
                    'T',
                    parts.getPowerCoilSilver(),
                    'R',
                    parts.getPowerCoilGold());
        }
    }

    @Override
    public ICoFHEnergyStorage getEnergyStorage(int i) {
        final EnergyStorage energy = new EnergyStorage(i);
        return new ICoFHEnergyStorage() {

            @Override
            public int extractEnergy(int space, boolean b) {
                return energy.extractEnergy(space, b);
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                return energy.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int getEnergyStored() {
                return energy.getEnergyStored();
            }

            @Override
            public int getMaxEnergyStored() {
                return energy.getMaxEnergyStored();
            }

            @Override
            public void readFromNBT(NBTTagCompound nbt) {
                energy.readFromNBT(nbt);
            }

            @Override
            public void writeToNBT(NBTTagCompound nbt) {
                energy.writeToNBT(nbt);
            }
        };
    }

    @Override
    public boolean isAvailable() {
        return true;
    }
}
