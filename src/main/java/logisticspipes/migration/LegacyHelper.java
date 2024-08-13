package logisticspipes.migration;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.*;

import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.item.ItemIdentifierInventory;

public class LegacyHelper {

    public static void readItemIdentifierInventoryAndConvertToTank(FluidTank tank, NBTTagCompound compound,
            String prefix) {
        ItemIdentifierInventory inv = makeSingleFluidInventory();
        inv.readFromNBT(compound, prefix);

        FluidIdentifier fluidIdentifier = FluidIdentifier.get(inv.getIDStackInSlot(0).getItem());
        FluidStack fluidStack = fluidIdentifier.makeFluidStack(1);
        tank.setFluid(fluidStack);
    }

    private static ItemIdentifierInventory makeSingleFluidInventory() {
        return new ItemIdentifierInventory(1, "DummyInventory", 127, true);
    }
}
