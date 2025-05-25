package logisticspipes.network.packets.modules;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.network.abstractpackets.Integer2CoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;

public class CrafterDefault extends Integer2CoordinatesPacket {

    public CrafterDefault(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new CrafterDefault(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {}
}
