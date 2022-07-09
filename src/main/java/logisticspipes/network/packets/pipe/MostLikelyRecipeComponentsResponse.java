package logisticspipes.network.packets.pipe;

import java.io.IOException;
import java.util.List;
import logisticspipes.network.IReadListObject;
import logisticspipes.network.IWriteListObject;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;

@Accessors(chain = true)
public class MostLikelyRecipeComponentsResponse extends ModernPacket {

    @Getter
    @Setter
    List<Integer> response;

    public MostLikelyRecipeComponentsResponse(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        response = data.readList(new IReadListObject<Integer>() {
            @Override
            public Integer readObject(LPDataInputStream data) throws IOException {
                return data.readInt();
            }
        });
    }

    @Override
    public void processPacket(EntityPlayer player) {
        MainProxy.proxy.processMostLikelyRecipeComponentsResponse(this);
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeList(response, new IWriteListObject<Integer>() {
            @Override
            public void writeObject(LPDataOutputStream data, Integer object) throws IOException {
                data.writeInt(object);
            }
        });
    }

    @Override
    public ModernPacket template() {
        return new MostLikelyRecipeComponentsResponse(getId());
    }
}
