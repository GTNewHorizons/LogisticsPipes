package logisticspipes.request;

import java.util.ArrayList;
import java.util.List;

import logisticspipes.interfaces.routing.IAdditionalTargetInformation;
import logisticspipes.request.resources.IResource;
import logisticspipes.utils.tuples.Pair;
import lombok.Getter;

public abstract class BaseCraftingTemplate implements IReqCraftingTemplate {

    private final ArrayList<Pair<IResource, IAdditionalTargetInformation>> ingredients;
    @Getter
    private final int priority;

    public BaseCraftingTemplate(int ingredientDefaultCount, int priority) {
        this.ingredients = new ArrayList<>(ingredientDefaultCount);
        this.priority = priority;
    }

    public void addIngredient(IResource requirement, IAdditionalTargetInformation info) {
        ingredients.add(new Pair<>(requirement, info));
    }

    @Override
    public List<Pair<IResource, IAdditionalTargetInformation>> getComponents(int nCraftingSetsNeeded) {
        List<Pair<IResource, IAdditionalTargetInformation>> stacks = new ArrayList<>(ingredients.size());

        // for each thing needed to satisfy this promise
        for (Pair<IResource, IAdditionalTargetInformation> stack : ingredients) {
            Pair<IResource, IAdditionalTargetInformation> pair = new Pair<>(
                    stack.getValue1().clone(nCraftingSetsNeeded),
                    stack.getValue2());
            stacks.add(pair);
        }
        return stacks;
    }

    @Override
    public int compareTo(ICraftingTemplate o) {
        int c = Integer.compare(getPriority(), o.getPriority());
        if (c != 0) return c;

        c = getResultStack().compareTo(o.getResultStack());
        if (c != 0) return c;

        c = getCrafter().compareTo(o.getCrafter());
        return c;
    }
}
