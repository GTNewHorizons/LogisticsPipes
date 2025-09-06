package logisticspipes.network.packets.block;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.abstractpackets.IntegerModuleCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

import java.io.IOException;

@Getter
@Setter
public class CraftingPipeIncrementAdvancedSatellitePacket extends IntegerModuleCoordinatesPacket {

    private int increment;
    public CraftingPipeIncrementAdvancedSatellitePacket(int id) {
        super(id);
        this.increment = 1;
    }

    @Override
    public ModernPacket template() {
        return new CraftingPipeIncrementAdvancedSatellitePacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        ModuleCrafter module = this.getLogisticsModule(player, ModuleCrafter.class);
        if (module == null) {
            return;
        }
        module.incrementId(player, increment, getInteger());
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        super.readData(data);
        increment = data.readInt();
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeInt(increment);
    }
}
