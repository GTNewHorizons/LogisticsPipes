package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.LogisticsSecurityTileEntity;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class SecurityCardPacket extends IntegerCoordinatesPacket {

    public SecurityCardPacket(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new SecurityCardPacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        LogisticsSecurityTileEntity tile = this.getTile(player.worldObj, LogisticsSecurityTileEntity.class);
        if (tile != null) {
            tile.buttonFreqCard(getInteger(), player);
        }
    }
}
