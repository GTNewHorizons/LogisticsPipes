package logisticspipes.logistics;

import java.util.List;
import java.util.TreeSet;
import logisticspipes.routing.ExitRoute;
import logisticspipes.routing.IRouter;
import logisticspipes.utils.item.ItemIdentifierStack;
import logisticspipes.utils.tuples.Pair;
import net.minecraftforge.fluids.FluidStack;

public interface ILogisticsFluidManager {

    Pair<Integer, Integer> getBestReply(FluidStack stack, IRouter sourceRouter, List<Integer> jamList);

    ItemIdentifierStack getFluidContainer(FluidStack stack);

    FluidStack getFluidFromContainer(ItemIdentifierStack stack);

    TreeSet<ItemIdentifierStack> getAvailableFluid(List<ExitRoute> list);
}
