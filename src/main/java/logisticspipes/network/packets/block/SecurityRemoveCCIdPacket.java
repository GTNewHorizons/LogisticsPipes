package logisticspipes.network.packets.block;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.blocks.LogisticsSecurityTileEntity;
import logisticspipes.network.abstractpackets.IntegerCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class SecurityRemoveCCIdPacket extends IntegerCoordinatesPacket {

    public SecurityRemoveCCIdPacket(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new SecurityRemoveCCIdPacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        LogisticsSecurityTileEntity tile = this.getTile(player.worldObj, LogisticsSecurityTileEntity.class);
        if (tile != null) {
            tile.removeCCFromList(getInteger());
            tile.requestList(player);
        }
    }
}
