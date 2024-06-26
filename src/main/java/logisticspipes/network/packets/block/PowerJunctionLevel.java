package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.powertile.LogisticsPowerJunctionTileEntity;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class PowerJunctionLevel extends IntegerCoordinatesPacket {

    public PowerJunctionLevel(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new PowerJunctionLevel(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        LogisticsPowerJunctionTileEntity tile = this.getTile(player.worldObj, LogisticsPowerJunctionTileEntity.class);
        if (tile != null) {
            tile.handlePowerPacket(getInteger());
        }
    }
}
