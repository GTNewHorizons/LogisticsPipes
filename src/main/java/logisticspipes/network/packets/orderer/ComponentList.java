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
        data.writeCollection(used, new IWriteListObject<IResource>() {

            @Override
            public void writeObject(LPDataOutputStream data, IResource object) throws IOException {
                data.writeIResource(object);
            }
        });
        data.writeCollection(missing, new IWriteListObject<IResource>() {

            @Override
            public void writeObject(LPDataOutputStream data, IResource object) throws IOException {
                data.writeIResource(object);
            }
        });
        data.write(0);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        used = data.readList(new IReadListObject<IResource>() {

            @Override
            public IResource readObject(LPDataInputStream data) throws IOException {
                return data.readIResource();
            }
        });
        missing = data.readList(new IReadListObject<IResource>() {

            @Override
            public IResource readObject(LPDataInputStream data) throws IOException {
                return data.readIResource();
            }
        });
    }
}
