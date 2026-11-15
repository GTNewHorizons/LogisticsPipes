package logisticspipes.proxy.computers.objects;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.CCType;
import logisticspipes.proxy.computers.interfaces.ILPCCTypeHolder;
import logisticspipes.utils.FluidIdentifier;

@CCType(name = "FluidIdentifierBuilder")
public class CCFluidIdentifierBuilder implements ILPCCTypeHolder {

    private Object ccType;

    private String fluidName = null;

    @CCCommand(description = "Set the Forge registry name (e.g. water, molten.rubber) for this FluidIdentifierBuilder")
    public void setFluidName(String name) {
        fluidName = name;
    }

    @CCCommand(description = "Returns the Forge registry name for this FluidIdentifierBuilder")
    public String getFluidName() {
        return fluidName;
    }

    @CCCommand(description = "Returns the FluidIdentifier for this FluidIdentifierBuilder")
    public FluidIdentifier build() {
        if (fluidName == null) {
            throw new UnsupportedOperationException("No fluid name set");
        }
        FluidStack stack = FluidRegistry.getFluidStack(fluidName, 1);
        if (stack == null) {
            throw new UnsupportedOperationException("Not a valid FluidIdentifier: " + fluidName);
        }
        return FluidIdentifier.get(stack);
    }

    @Override
    public void setCCType(Object type) {
        ccType = type;
    }

    @Override
    public Object getCCType() {
        return ccType;
    }
}
