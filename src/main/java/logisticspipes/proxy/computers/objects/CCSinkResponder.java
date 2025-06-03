package logisticspipes.proxy.computers.objects;

import logisticspipes.interfaces.IQueueCCEvent;
import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.CCType;
import logisticspipes.proxy.computers.interfaces.ILPCCTypeHolder;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;
import lombok.Getter;

@CCType(name = "CCItemSinkRequest")
public class CCSinkResponder implements ILPCCTypeHolder {

    private Object ccType;

    @Getter
    private final ItemIdentifierStack stack;

    @Getter
    private final int routerId;

    @Getter
    private boolean done = false;

    @Getter
    private final IQueueCCEvent queuer;

    @Getter
    private int canSink;

    @Getter
    private int priority;

    private boolean destroy = false;

    public CCSinkResponder(ItemIdentifierStack stack, int id, IQueueCCEvent queuer) {
        this.stack = stack;
        routerId = id;
        this.queuer = queuer;
    }

    @CCCommand(description = "Returns the ItemIdentifier for the item that should be sinked")
    public ItemIdentifier getItemIdentifier() {
        return stack.getItem();
    }

    @CCCommand(description = "Returns the amount of items that should be sinked")
    public int getAmount() {
        return stack.getStackSize();
    }

    @CCCommand(description = "Sends the response to the CC QuickSort module to deny the sink")
    public void denySink() {
        done = true;
        canSink = -1;
    }

    @CCCommand(
            description = "Sends the response to the CC QuickSort module to accept the sink for the givven amount with the givven priority")
    public void acceptSink(Long amount, Long priority) {
        canSink = ((Long) (amount > 0 ? amount : 0L)).intValue();
        this.priority = priority.intValue();
        done = true;
    }

    @Override
    public void setCCType(Object type) {
        ccType = type;
    }

    @Override
    public Object getCCType() {
        return ccType;
    }

    public boolean isDestroy() {
        return destroy;
    }

    public void onDestroy() {
        destroy = true;
    }
}
