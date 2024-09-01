package logisticspipes.request;

import java.util.Collections;
import java.util.List;

import logisticspipes.interfaces.IStack;
import logisticspipes.interfaces.routing.ICraftFluids;
import logisticspipes.request.resources.FluidResource;
import logisticspipes.request.resources.IResource;
import logisticspipes.routing.FluidLogisticsPromise;
import logisticspipes.routing.order.IOrderInfoProvider;
import logisticspipes.utils.FluidIdentifierStack;
import logisticspipes.utils.item.ItemIdentifierStack;

public class FluidCraftingTemplate extends BaseCraftingTemplate {

    private final FluidResource result;
    private final ICraftFluids crafter;

    public FluidCraftingTemplate(FluidResource result, ICraftFluids crafter, int priority) {
        super(3, priority);
        this.result = result;
        this.crafter = crafter;
    }

    // TODO FluidCrafting: FIX
    @Override
    public void addByproduct(ItemIdentifierStack byproductItem) {

    }

    // TODO FluidCrafting: FIX
    @Override
    public List<IExtraPromise> getByproducts(int workSets) {
        return Collections.emptyList();
    }

    @Override
    public boolean canCraft(IResource type) {
        if (type instanceof FluidResource) {
            return ((FluidResource) type).isFluidIdentifierSame(result.getFluid());
        }

        return false;
    }

    @Override
    public FluidLogisticsPromise generatePromise(int nResultSets) {
        return new FluidLogisticsPromise(
                result.getFluid(),
                result.getRequestedAmount() * nResultSets,
                crafter,
                IOrderInfoProvider.ResourceType.CRAFTING);
    }

    @Override
    public IResource getResultResource() {
        return result;
    }

    @Override
    public IStack getResultStack() {
        return new FluidIdentifierStack(result.getFluid(), result.getRequestedAmount());
    }

    @Override
    public ICraftFluids getCrafter() {
        return crafter;
    }
}
