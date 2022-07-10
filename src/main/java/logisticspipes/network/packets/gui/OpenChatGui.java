package logisticspipes.network.packets.gui;

import java.io.IOException;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.MainProxy;
import net.minecraft.entity.player.EntityPlayer;

public class OpenChatGui extends ModernPacket {

    public OpenChatGui(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) throws IOException {}

    @Override
    public void processPacket(EntityPlayer player) {
        if (!player.isClientWorld()) return;
        MainProxy.proxy.openChatGui();
    }

    @Override
    public void writeData(LPDataOutputStream data) throws IOException {}

    @Override
    public ModernPacket template() {
        return new OpenChatGui(getId());
    }
}
