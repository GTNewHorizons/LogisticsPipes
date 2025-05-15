package logisticspipes.utils;

import java.util.ArrayList;

import logisticspipes.interfaces.IChainAddList;

public class ChainAddArrayList<T> extends ArrayList<T> implements IChainAddList<T> {

    private static final long serialVersionUID = 8165175652994154959L;

    @Override
    public T addChain(T add) {
        this.add(add);
        return add;
    }
}
