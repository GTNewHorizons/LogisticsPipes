package logisticspipes.network.packets.chassis;

import java.lang.ref.WeakReference;
import java.util.List;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.LogisticsEventListener;
import logisticspipes.modules.ModuleQuickSort;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;

public class ChestGuiClosed extends ModernPacket {

    public ChestGuiClosed(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public void processPacket(EntityPlayer player) {
        List<WeakReference<ModuleQuickSort>> list = LogisticsEventListener.chestQuickSortConnection.get(player);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (WeakReference<ModuleQuickSort> sorter : list) {
            ModuleQuickSort module = sorter.get();
            if (module == null) {
                continue;
            }
            module.removeWatchingPlayer(player);
        }
        LogisticsEventListener.chestQuickSortConnection.remove(player);
    }

    @Override
    public void writeData(LPDataOutputStream data) {}

    @Override
    public ModernPacket template() {
        return new ChestGuiClosed(getId());
    }
}
