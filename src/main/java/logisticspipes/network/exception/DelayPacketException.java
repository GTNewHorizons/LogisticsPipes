package logisticspipes.network.exception;

public class DelayPacketException extends RuntimeException {

    private static final long serialVersionUID = -4744194928288190347L;

    public DelayPacketException(String message) {
        super(message);
    }

    public DelayPacketException() {
        super();
    }
}
