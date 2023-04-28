package logisticspipes.network.packets.orderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.request.resources.IResource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class MissingItems extends ModernPacket {

    @Getter
    @Setter
    private Collection<IResource> items = new ArrayList<>();

    @Setter
    @Getter
    private boolean flag;

    public MissingItems(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new MissingItems(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        MainProxy.proxy.processMissingItemsPacket(this, player);
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeCollection(items, LPDataOutputStream::writeIResource);
        data.writeBoolean(isFlag());
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        items = data.readList(LPDataInputStream::readIResource);
        setFlag(data.readBoolean());
    }
}
