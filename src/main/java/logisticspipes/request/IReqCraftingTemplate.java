package logisticspipes.request;

import logisticspipes.interfaces.routing.IAdditionalTargetInformation;
import logisticspipes.request.resources.IResource;
import logisticspipes.utils.item.ItemIdentifierStack;

public interface IReqCraftingTemplate extends ICraftingTemplate {

    void addIngredient(IResource requirement, IAdditionalTargetInformation info);

    void addByproduct(ItemIdentifierStack byproductItem);
}
