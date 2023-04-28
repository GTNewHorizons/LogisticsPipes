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
public class ComponentList extends ModernPacket {

    @Getter
    @Setter
    private Collection<IResource> used = new ArrayList<>();

    @Getter
    @Setter
    private Collection<IResource> missing = new ArrayList<>();

    public ComponentList(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new ComponentList(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        MainProxy.proxy.processComponentListPacket(this, player);
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeCollection(used, LPDataOutputStream::writeIResource);
        data.writeCollection(missing, LPDataOutputStream::writeIResource);
        data.write(0);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        used = data.readList(LPDataInputStream::readIResource);
        missing = data.readList(LPDataInputStream::readIResource);
    }
}
