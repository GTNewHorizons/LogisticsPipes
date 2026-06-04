package logisticspipes.interfaces;

import java.util.Collection;

import logisticspipes.utils.item.ItemIdentifierStack;

public interface IModuleBufferInventoryReceive {

    void handleBufferInvContent(Collection<ItemIdentifierStack> _allItems);
}
