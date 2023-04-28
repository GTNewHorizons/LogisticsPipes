package logisticspipes.network.packets.routingdebug;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.routing.debug.ClientViewController;

public class RoutingUpdateInitDebug extends ModernPacket {

    public RoutingUpdateInitDebug(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public void processPacket(EntityPlayer player) {
        ClientViewController.instance().init(this);
    }

    @Override
    public void writeData(LPDataOutputStream data) {}

    @Override
    public ModernPacket template() {
        return new RoutingUpdateInitDebug(getId());
    }

    @Override
    public boolean isCompressable() {
        return true;
    }
}
