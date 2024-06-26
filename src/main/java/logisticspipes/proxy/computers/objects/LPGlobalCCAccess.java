package logisticspipes.proxy.computers.objects;

import java.util.Map;

import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.CCType;
import logisticspipes.proxy.computers.interfaces.ILPCCTypeHolder;

@CCType(name = "LP Global Access")
public class LPGlobalCCAccess implements ILPCCTypeHolder {

    private Object ccType;

    @CCCommand(description = "Tryes to give more information about the givven object")
    public String identify(Object object) {
        if (object instanceof Map<?, ?>) {
            StringBuilder builder = new StringBuilder("Map: ");
            if (((Map<?, ?>) object).isEmpty()) {
                return builder.append("empty").toString();
            }
            builder.append("key [");
            if (((Map<?, ?>) object).keySet().toArray()[0] != null) {
                builder.append(((Map<?, ?>) object).keySet().toArray()[0].getClass());
            } else {
                builder.append("null");
            }
            builder.append("], [");
            if (((Map<?, ?>) object).values().toArray()[0] != null) {
                builder.append(((Map<?, ?>) object).values().toArray()[0].getClass());
            } else {
                builder.append("null");
            }
            builder.append("]");
            return builder.toString();
        }
        if (object == null) {
            return "null";
        }
        return object + " [" + object.getClass() + "]";
    }

    @CCCommand(description = "Creates a new ItemIdentifier Builder")
    public CCItemIdentifierBuilder getItemIdentifierBuilder() {
        return new CCItemIdentifierBuilder();
    }

    @Override
    public void setCCType(Object type) {
        ccType = type;
    }

    @Override
    public Object getCCType() {
        return ccType;
    }
}
