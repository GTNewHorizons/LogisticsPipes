package logisticspipes.proxy.buildcraft.subproxies;

import java.io.IOException;

import buildcraft.transport.PipePluggableState;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;

public class LPBCPluggableState extends PipePluggableState implements IBCPluggableState {

    private ByteBuf oldBuffer = Unpooled.buffer(128);
    private ByteBuf dirtyCheckBuffer = Unpooled.buffer(128);

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        ByteBuf buf = Unpooled.buffer(128);
        this.writeData(buf);
        data.writeByteBuf(buf);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        ByteBuf buf = data.readByteBuf();
        this.readData(buf);
    }

    @Override
    public synchronized boolean isDirty() {
        dirtyCheckBuffer.clear();
        this.writeData(dirtyCheckBuffer);

        boolean isDirty = !dirtyCheckBuffer.equals(oldBuffer);
        if (isDirty) {
            // Instead of copying the content of `dirtyCheckBuffer` into `oldBuffer`
            // we simply swap them. So `dirtyCheckBuffer` becomes the new `oldBuffer`
            // and we'll use `oldBuffer` the next time for checking.
            ByteBuf tmp = oldBuffer;
            oldBuffer = dirtyCheckBuffer;
            dirtyCheckBuffer = tmp;
        }
        return isDirty;
    }
}
