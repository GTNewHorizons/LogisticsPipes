package logisticspipes.network.packets.hud;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.interfaces.IBlockWatchingHandler;
import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class HUDStartBlockWatchingPacket extends CoordinatesPacket {

    public HUDStartBlockWatchingPacket(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new HUDStartBlockWatchingPacket(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        IBlockWatchingHandler tile = this.getTile(player.worldObj, IBlockWatchingHandler.class);
        if (tile != null) {
            tile.playerStartWatching(player);
        }
    }
}
