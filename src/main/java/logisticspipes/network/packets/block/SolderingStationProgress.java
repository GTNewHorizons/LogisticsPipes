package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.LogisticsSolderingTileEntity;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class SolderingStationProgress extends IntegerCoordinatesPacket {

    public SolderingStationProgress(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new SolderingStationProgress(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        final LogisticsSolderingTileEntity tile = this.getTile(player.worldObj, LogisticsSolderingTileEntity.class);
        if (tile != null) {
            tile.progress = getInteger();
        }
    }
}
