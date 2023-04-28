package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.LPConstants;
import logisticspipes.blocks.powertile.LogisticsPowerJunctionTileEntity;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class PowerJunctionCheatPacket extends CoordinatesPacket {

    public PowerJunctionCheatPacket(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new PowerJunctionCheatPacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        if (!LPConstants.DEBUG) {
            return;
        }
        final LogisticsPowerJunctionTileEntity tile = this
                .getTile(player.worldObj, LogisticsPowerJunctionTileEntity.class);
        if (tile != null) {
            tile.addEnergy(100000);
        }
    }
}
