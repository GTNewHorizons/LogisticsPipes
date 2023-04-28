package logisticspipes.network.packets.pipe;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

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
        response = data.readList(DataInputStream::readInt);
    }

    @Override
    public void processPacket(EntityPlayer player) {
        MainProxy.proxy.processMostLikelyRecipeComponentsResponse(this);
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        data.writeList(response, DataOutputStream::writeInt);
    }

    @Override
    public ModernPacket template() {
        return new MostLikelyRecipeComponentsResponse(getId());
    }
}
