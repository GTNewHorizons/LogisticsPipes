package logisticspipes.network.packets.cpipe;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.abstractpackets.ModuleCoordinatesPacket;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CPipeIncrementSatellite extends ModuleCoordinatesPacket {

    int increment;

    public CPipeIncrementSatellite(int id) {
        super(id);
        increment = 1;
    }

    @Override
    public ModernPacket template() {
        return new CPipeIncrementSatellite(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        ModuleCrafter module = this.getLogisticsModule(player, ModuleCrafter.class);
        if (module == null) {
            return;
        }
        module.incrementId(player, increment);
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
