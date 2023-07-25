package logisticspipes.network.packets.routingdebug;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;

import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.packets.routingdebug.RoutingUpdateTargetResponse.TargetMode;
import logisticspipes.proxy.MainProxy;

public class RoutingUpdateAskForTarget extends ModernPacket {

    public RoutingUpdateAskForTarget(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public void processPacket(EntityPlayer player) {
        if (!player.isClientWorld()) return;
        MovingObjectPosition box = MainProxy.proxy.getMousedOverObject();
        if (box == null) {
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(RoutingUpdateTargetResponse.class).setMode(TargetMode.None));
        } else if (box.typeOfHit == MovingObjectType.BLOCK) {
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(RoutingUpdateTargetResponse.class).setMode(TargetMode.Block)
                            .setAdditions(new int[] { box.blockX, box.blockY, box.blockZ }));
        } else if (box.typeOfHit == MovingObjectType.ENTITY) {
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(RoutingUpdateTargetResponse.class).setMode(TargetMode.Entity)
                            .setAdditions(new int[] { box.entityHit.getEntityId() }));
        }
    }

    @Override
    public void writeData(LPDataOutputStream data) {}

    @Override
    public ModernPacket template() {
        return new RoutingUpdateAskForTarget(getId());
    }

    @Override
    public boolean isCompressable() {
        return true;
    }
}
