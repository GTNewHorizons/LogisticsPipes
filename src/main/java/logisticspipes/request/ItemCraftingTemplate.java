/*
 * Copyright (c) Krapht, 2011 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0,
 * or MMPL. Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package logisticspipes.request;

import java.util.ArrayList;
import java.util.List;

import logisticspipes.interfaces.routing.ICraftItems;
import logisticspipes.request.resources.DictResource;
import logisticspipes.request.resources.IResource;
import logisticspipes.request.resources.ItemResource;
import logisticspipes.routing.LogisticsExtraPromise;
import logisticspipes.routing.LogisticsPromise;
import logisticspipes.routing.order.IOrderInfoProvider.ResourceType;
import logisticspipes.utils.item.ItemIdentifierStack;

public class ItemCraftingTemplate extends BaseCraftingTemplate {

    protected ItemIdentifierStack _result;
    protected ICraftItems _crafter;

    protected ArrayList<ItemIdentifierStack> _byproduct = new ArrayList<>(9);

    public ItemCraftingTemplate(ItemIdentifierStack result, ICraftItems crafter, int priority) {
        super(9, priority);
        _result = result;
        _crafter = crafter;
    }

    public void addByproduct(ItemIdentifierStack stack) {
        for (ItemIdentifierStack i : _byproduct) {
            if (i.getItem().equals(stack.getItem())) {
                i.setStackSize(i.getStackSize() + stack.getStackSize());
                return;
            }
        }
        _byproduct.add(stack);
    }

    @Override
    public LogisticsPromise generatePromise(int nResultSets) {
        return new LogisticsPromise(
                _result.getItem(),
                _result.getStackSize() * nResultSets,
                _crafter,
                ResourceType.CRAFTING);
    }

    // TODO: refactor so that other classes don't reach through the template to the crafter.
    // needed to get the crafter todo, in order to sort
    @Override
    public ICraftItems getCrafter() {
        return _crafter;
    }

    @Override
    public boolean canCraft(IResource type) {
        if (type instanceof ItemResource) {
            return ((ItemResource) type).getItem().equals(_result.getItem());
        } else if (type instanceof DictResource) {
            return type.matches(_result.getItem(), IResource.MatchSettings.NORMAL);
        }
        return false;
    }

    @Override
    public IResource getResultResource() {
        return new ItemResource(_result, null);
    }

    @Override
    public ItemIdentifierStack getResultStack() {
        return _result;
    }

    @Override
    public List<IExtraPromise> getByproducts(int workSets) {
        List<IExtraPromise> list = new ArrayList<>();
        for (ItemIdentifierStack stack : _byproduct) {
            list.add(new LogisticsExtraPromise(stack.getItem(), stack.getStackSize() * workSets, getCrafter(), false));
        }
        return list;
    }
}
