package logisticspipes.utils;

import logisticspipes.interfaces.IStack;
import logisticspipes.utils.item.ItemIdentifier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FluidIdentifierStack implements IStack {

    @Getter
    private final FluidIdentifier fluidIdentifier;
    private final int amount;

    @Override
    public ItemIdentifier getItemIdentifier() {
        return fluidIdentifier.getItemIdentifier();
    }

    @Override
    public int getStackSize() {
        return amount;
    }
}
