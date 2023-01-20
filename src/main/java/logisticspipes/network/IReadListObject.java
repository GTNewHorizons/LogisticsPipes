package logisticspipes.network;

import java.io.IOException;

public interface IReadListObject<T> {

    T readObject(LPDataInputStream data) throws IOException;
}
