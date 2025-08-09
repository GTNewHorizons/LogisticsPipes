package logisticspipes.network.packets.block;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.powertile.LogisticsPowerProviderTileEntity;
import logisticspipes.network.abstractpackets.ModernPacket;

import java.io.IOException;

@Accessors(chain = true)
public class PowerProviderLevel extends CoordinatesPacket {

    @Getter
    @Setter
    private double storedEnergy;

    @Getter
    @Setter
    private double maxEnergy;

    @Getter
    @Setter
    private double averageIO;

    public PowerProviderLevel(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new PowerProviderLevel(getId());
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeDouble(storedEnergy);
        data.writeDouble(maxEnergy);
        data.writeDouble(averageIO);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        super.readData(data);
        storedEnergy = data.readDouble();
        maxEnergy = data.readDouble();
        averageIO = data.readDouble();
    }

    @Override
    public void processPacket(EntityPlayer player) {
        System.out.println("Processing " + this);
        LogisticsPowerProviderTileEntity tile = this.getTile(player.worldObj, LogisticsPowerProviderTileEntity.class);
        if (tile != null) {
            tile.updateClientReceive(this);
        }
    }

    @Override
    public String toString() {
        return "PowerProviderLevel [id=" + getId() + ", storedEnergy=" + storedEnergy + ", maxEnergy=" + maxEnergy + ", averageIO=" + averageIO + "]";
    }
}
