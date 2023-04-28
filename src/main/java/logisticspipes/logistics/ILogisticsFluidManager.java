package logisticspipes.logistics;

import java.util.List;
import java.util.TreeSet;

import net.minecraftforge.fluids.FluidStack;

import logisticspipes.routing.ExitRoute;
import logisticspipes.routing.IRouter;
import logisticspipes.utils.item.ItemIdentifierStack;
import logisticspipes.utils.tuples.Pair;

public interface ILogisticsFluidManager {

    Pair<Integer, Integer> getBestReply(FluidStack stack, IRouter sourceRouter, List<Integer> jamList);

    ItemIdentifierStack getFluidContainer(FluidStack stack);

    FluidStack getFluidFromContainer(ItemIdentifierStack stack);

    TreeSet<ItemIdentifierStack> getAvailableFluid(List<ExitRoute> list);
}
