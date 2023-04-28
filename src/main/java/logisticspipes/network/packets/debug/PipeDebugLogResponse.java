package logisticspipes.network.packets.debug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;

import logisticspipes.network.abstractpackets.CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;

public class PipeDebugLogResponse extends CoordinatesPacket {

    public PipeDebugLogResponse(int id) {
        super(id);
    }

    @Override
    public void processPacket(EntityPlayer player) {
        LogisticsTileGenericPipe tile = this.getPipe(player.getEntityWorld());
        if (tile != null) {
            tile.pipe.debug.openForPlayer(player);
            player.addChatComponentMessage(new ChatComponentText("Debug log enabled."));
        }
    }

    @Override
    public ModernPacket template() {
        return new PipeDebugLogResponse(getId());
    }

    @Override
    public boolean isCompressable() {
        return true;
    }
}
