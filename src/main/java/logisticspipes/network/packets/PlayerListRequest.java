package logisticspipes.network.packets;

import java.util.LinkedList;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class PlayerListRequest extends ModernPacket {

    public PlayerListRequest(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new PlayerListRequest(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        List<String> list = new LinkedList<>();
        for (WorldServer world : DimensionManager.getWorlds()) {
            for (Object o : world.playerEntities) {
                if (o instanceof EntityPlayer) {
                    list.add(((EntityPlayer) o).getGameProfile().getName());
                }
            }
        }
        MainProxy.sendPacketToPlayer(PacketHandler.getPacket(PlayerList.class).setStringList(list), player);
    }

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public void writeData(LPDataOutputStream data) {}
}
