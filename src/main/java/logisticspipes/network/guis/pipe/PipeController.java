package logisticspipes.network.guis.pipe;

import java.util.UUID;

import net.minecraft.entity.player.EntityPlayer;

import logisticspipes.LogisticsPipes;
import logisticspipes.gui.GuiPipeController;
import logisticspipes.interfaces.IGuiOpenControler;
import logisticspipes.items.LogisticsItemCard;
import logisticspipes.network.abstractguis.CoordinatesGuiProvider;
import logisticspipes.network.abstractguis.GuiProvider;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.pipes.upgrades.IPipeUpgrade;
import logisticspipes.pipes.upgrades.SneakyUpgrade;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.utils.gui.DummyContainer;

public class PipeController extends CoordinatesGuiProvider {

    public PipeController(int id) {
        super(id);
    }

    @Override
    public Object getClientGui(EntityPlayer player) {
        LogisticsTileGenericPipe pipe = getPipe(player.getEntityWorld());
        if (pipe == null || !(pipe.pipe instanceof CoreRoutedPipe)) {
            return null;
        }
        return new GuiPipeController(player, (CoreRoutedPipe) pipe.pipe);
    }

    @Override
    public DummyContainer getContainer(EntityPlayer player) {
        LogisticsTileGenericPipe tile = getPipe(player.getEntityWorld());
        if (tile == null || !(tile.pipe instanceof CoreRoutedPipe)) {
            return null;
        }
        final CoreRoutedPipe pipe = (CoreRoutedPipe) tile.pipe;
        DummyContainer dummy = new DummyContainer(
                player,
                null,
                pipe.getOriginalUpgradeManager().getGuiController(),
                new IGuiOpenControler() {

                    // Network Statistics
                    @Override
                    public void guiOpenedByPlayer(EntityPlayer player) {
                        pipe.playerStartWatching(player, 0);
                    }

                    @Override
                    public void guiClosedByPlayer(EntityPlayer player) {
                        pipe.playerStopWatching(player, 0);
                    }
                });
        dummy.addNormalSlotsForPlayerInventory(0, 0);
        // TAB_1 SLOTS
        for (int pipeSlot = 0; pipeSlot < 9; pipeSlot++) {
            dummy.addRestrictedSlot(
                    pipeSlot,
                    pipe.getOriginalUpgradeManager().getInv(),
                    8 + pipeSlot * 18,
                    18,
                    itemStack -> {
                        if (itemStack == null) {
                            return false;
                        }
                        if (itemStack.getItem() == LogisticsPipes.UpgradeItem) {
                            return LogisticsPipes.UpgradeItem.getUpgradeForItem(itemStack, null).isAllowedForPipe(pipe);
                        } else {
                            return false;
                        }
                    });
        }
        for (int pipeSlot = 0; pipeSlot < 9; pipeSlot++) {
            dummy.addRestrictedSlot(
                    pipeSlot,
                    pipe.getOriginalUpgradeManager().getSneakyInv(),
                    8 + pipeSlot * 18,
                    48,
                    itemStack -> {
                        if (itemStack == null) {
                            return false;
                        }
                        if (itemStack.getItem() == LogisticsPipes.UpgradeItem) {
                            IPipeUpgrade upgrade = LogisticsPipes.UpgradeItem.getUpgradeForItem(itemStack, null);
                            if (!(upgrade instanceof SneakyUpgrade)) {
                                return false;
                            }
                            return upgrade.isAllowedForPipe(pipe);
                        } else {
                            return false;
                        }
                    });
        }
        // TAB_2 SLOTS
        dummy.addStaticRestrictedSlot(0, pipe.getOriginalUpgradeManager().getSecInv(), 8 + 8 * 18, 18, itemStack -> {
            if (itemStack == null) {
                return false;
            }
            if (itemStack.getItem() != LogisticsPipes.LogisticsItemCard) {
                return false;
            }
            if (itemStack.getItemDamage() != LogisticsItemCard.SEC_CARD) {
                return false;
            }
            return SimpleServiceLocator.securityStationManager
                    .isAuthorized(UUID.fromString(itemStack.getTagCompound().getString("UUID")));
        }, 1);
        dummy.addRestrictedSlot(0, tile.logicController.diskInv, 14, 36, LogisticsPipes.LogisticsItemDisk);
        return dummy;
    }

    @Override
    public GuiProvider template() {
        return new PipeController(getId());
    }
}
