package logisticspipes.network.packets.module;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.interfaces.IModuleBufferInventoryReceive;
import logisticspipes.network.abstractpackets.InventoryModuleCoordinatesPacket;
import logisticspipes.network.abstractpackets.ModernPacket;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class ModuleBufferInventory extends InventoryModuleCoordinatesPacket {

    public ModuleBufferInventory(int id) {
        super(id);
    }

    @Override
    public ModernPacket template() {
        return new ModuleBufferInventory(getId());
    }

    @Override
    public void processPacket(EntityPlayer player) {
        IModuleBufferInventoryReceive module = this.getLogisticsModule(player, IModuleBufferInventoryReceive.class);
        if (module == null) {
            return;
        }
        module.handleBufferInvContent(getIdentList());
    }
}
