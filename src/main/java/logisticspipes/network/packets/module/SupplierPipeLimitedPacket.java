package logisticspipes.network.packets.module;

import java.io.IOException;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.modules.ModuleActiveSupplier;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.abstractpackets.ModuleCoordinatesPacket;
import logisticspipes.proxy.MainProxy;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class SupplierPipeLimitedPacket extends ModuleCoordinatesPacket {

    @Getter
    @Setter
    private boolean isLimited;

    public SupplierPipeLimitedPacket(int id) {
        super(id);
    }

    @Override
    public void processPacket(EntityPlayer player) {
        ModuleActiveSupplier module = this.getLogisticsModule(player, ModuleActiveSupplier.class);
        if (module == null) {
            return;
        }
        module.setLimited(isLimited());
        if (MainProxy.isClient(player.worldObj)) {
            refresh();
        } else {
            MainProxy.sendPacketToPlayer(
                    PacketHandler.getPacket(SupplierPipeLimitedPacket.class).setLimited(isLimited()).setPacketPos(this),
                    player);
        }
    }

    private void refresh() {
        MainProxy.proxy.refreshGuiSupplierPipeMode();
    }

    @Override
    public ModernPacket template() {
        return new SupplierPipeLimitedPacket(getId());
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {
        super.writeData(data);
        data.writeBoolean(isLimited);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {
        super.readData(data);
        isLimited = data.readBoolean();
    }
}
