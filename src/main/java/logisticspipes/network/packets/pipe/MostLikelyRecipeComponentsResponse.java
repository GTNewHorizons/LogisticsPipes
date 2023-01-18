package logisticspipes.network.packets.pipe;

import java.io.IOException;
import java.util.List;
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
        response = data.readList(data1 -> data1.readInt());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        MainProxy.proxy.processMostLikelyRecipeComponentsResponse(this);
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeList(response, (data1, object) -> data1.writeInt(object));
    }

    @Override
    public ModernPacket template() {
        return new MostLikelyRecipeComponentsResponse(getId());
    }
}
