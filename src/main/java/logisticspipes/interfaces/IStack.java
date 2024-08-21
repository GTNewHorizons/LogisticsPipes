package logisticspipes.interfaces;

import org.jetbrains.annotations.NotNull;

import logisticspipes.utils.item.ItemIdentifier;

public interface IStack extends Comparable<IStack> {

    ItemIdentifier getItemIdentifier();

    int getStackSize();

    @Override
    default int compareTo(@NotNull IStack o) {
        int c = getItemIdentifier().compareTo(o.getItemIdentifier());
        if (c != 0) return c;

        return Integer.compare(getStackSize(), o.getStackSize());
    }
}
