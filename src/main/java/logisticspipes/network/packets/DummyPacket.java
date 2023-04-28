package logisticspipes.network.packets;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;

public class DummyPacket extends ModernPacket {

    public DummyPacket(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) {
        throw new RuntimeException("This packet should never be used");
    }

    @Override
    public void processPacket(EntityPlayer player) {
        throw new RuntimeException("This packet should never be used");
    }

    @Override
    public void writeData(LPDataOutputStream data) {
        throw new RuntimeException("This packet should never be used");
    }

    @Override
    public ModernPacket template() {
        return new DummyPacket(getId());
    }
}
