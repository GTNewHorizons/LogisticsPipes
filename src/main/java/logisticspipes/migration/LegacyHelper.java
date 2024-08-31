package logisticspipes.migration;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.item.ItemIdentifierInventory;
import logisticspipes.utils.item.ItemIdentifierStack;

public class LegacyHelper {

    public static void readItemIdentifierInventoryAndConvertToTank(FluidTank tank, NBTTagCompound compound,
            String prefix) {
        ItemIdentifierInventory inv = makeSingleFluidInventory();
        inv.readFromNBT(compound, prefix);

        ItemIdentifierStack savedFluid = inv.getIDStackInSlot(0);

        FluidStack fluidStack = null;
        if (savedFluid != null) {
            FluidIdentifier fluidIdentifier = FluidIdentifier.get(savedFluid.getItem());
            fluidStack = fluidIdentifier.makeFluidStack(1);
        }

        tank.setFluid(fluidStack);
    }

    private static ItemIdentifierInventory makeSingleFluidInventory() {
        return new ItemIdentifierInventory(1, "DummyInventory", 127, true);
    }
}
