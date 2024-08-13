package logisticspipes.api;

import net.minecraft.entity.player.EntityPlayer;

import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;

import logisticspipes.compat.ModularUIHelper;
import logisticspipes.pipes.basic.CoreUnroutedPipe;

public interface IMUICompatiblePipe {

    String getId();

    int getGuiWidth();

    int getGuiHeight();

    default void openGui(EntityPlayer player, CoreUnroutedPipe pipe) {
        ModularUIHelper.openPipeUI(player, pipe);
    }

    default void addUIWidgets(ModularPanel panel, PosGuiData data, PanelSyncManager syncManager) {}
}
