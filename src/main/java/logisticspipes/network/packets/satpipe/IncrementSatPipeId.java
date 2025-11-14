package logisticspipes.network.packets.satpipe;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.pipes.PipeFluidSatellite;
import logisticspipes.pipes.PipeItemsSatelliteLogistics;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class IncrementSatPipeId extends CoordinatesPacket {

    private int increment;

    public IncrementSatPipeId(int packetId) {
        super(packetId);
    }

    public IncrementSatPipeId(int packetId, int increment) {
        super(packetId);
        this.increment = increment;
    }

    @Override
    public ModernPacket template() {
        return new IncrementSatPipeId(getId(), 1);
    }

    @Override
    public void processPacket(EntityPlayer player) {
        final LogisticsTileGenericPipe pipe = getPipe(player.worldObj);
        if (pipe == null) {
            return;
        }

        if (pipe.pipe instanceof PipeItemsSatelliteLogistics) {
            ((PipeItemsSatelliteLogistics) pipe.pipe).incrementId(player, increment);
        }
        if (pipe.pipe instanceof PipeFluidSatellite) {
            ((PipeFluidSatellite) pipe.pipe).incrementId(player, increment);
        }
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeInt(increment);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        super.readData(data);
        increment = data.readInt();
    }
}
