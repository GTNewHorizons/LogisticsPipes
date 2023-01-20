package logisticspipes.network;

import java.io.IOException;

public interface IWriteListObject<T> {

    void writeObject(LPDataOutputStream data, T object) throws IOException;
}
