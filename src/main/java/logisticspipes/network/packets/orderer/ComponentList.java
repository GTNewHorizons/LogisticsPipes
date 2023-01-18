package logisticspipes.network.packets.orderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import logisticspipes.network.IReadListObject;
import logisticspipes.network.IWriteListObject;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import logisticspipes.request.resources.IResource;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;

@Accessors(chain = true)
public class ComponentList extends ModernPacket {

    @Getter
    @Setter
    private Collection<IResource> used = new ArrayList<IResource>();

    @Getter
    @Setter
    private Collection<IResource> missing = new ArrayList<IResource>();

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
        data.writeCollection(used, (data12, object) -> data12.writeIResource(object));
        data.writeCollection(missing, (data1, object) -> data1.writeIResource(object));
        data.write(0);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        used = data.readList(data12 -> data12.readIResource());
        missing = data.readList(data1 -> data1.readIResource());
    }
}
