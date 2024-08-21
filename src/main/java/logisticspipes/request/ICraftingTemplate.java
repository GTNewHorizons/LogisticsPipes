package logisticspipes.request;

import java.util.List;

import logisticspipes.interfaces.IStack;
import logisticspipes.interfaces.routing.IAdditionalTargetInformation;
import logisticspipes.interfaces.routing.ICraft;
import logisticspipes.request.resources.IResource;
import logisticspipes.utils.tuples.Pair;

public interface ICraftingTemplate extends Comparable<ICraftingTemplate> {

    List<Pair<IResource, IAdditionalTargetInformation>> getComponents(int nCraftingSets);

    List<IExtraPromise> getByproducts(int workSets);

    IPromise generatePromise(int nCraftingSetsNeeded);

    ICraft getCrafter();

    int getPriority();

    boolean canCraft(IResource requestType);

    IResource getResultResource();

    IStack getResultStack();
}
