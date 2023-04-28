package logisticspipes.logisticspipes;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.interfaces.IInventoryUtil;
import logisticspipes.interfaces.ISendRoutedItem;
import logisticspipes.interfaces.ISlotUpgradeManager;
import logisticspipes.modules.abstractmodules.LogisticsModule.ModulePositionType;
import logisticspipes.routing.order.LogisticsItemOrderManager;
import logisticspipes.utils.item.ItemIdentifier;

public interface IInventoryProvider extends ISendRoutedItem {

    IInventoryUtil getPointedInventory(boolean forExtraction);

    IInventoryUtil getPointedInventory(ExtractionMode mode, boolean forExtraction);

    IInventoryUtil getSneakyInventory(boolean forExtraction, ModulePositionType slot, int positionInt);

    IInventoryUtil getSneakyInventory(ForgeDirection _sneakyOrientation, boolean forExtraction);

    IInventoryUtil getUnsidedInventory();

    IInventory getRealInventory();

    ForgeDirection inventoryOrientation();

    // to interact and send items you need to know about orders, upgrades, and have the ability to send
    LogisticsItemOrderManager getItemOrderManager();

    void queueRoutedItem(IRoutedItem routedItem, ForgeDirection from);

    ISlotUpgradeManager getUpgradeManager(ModulePositionType slot, int positionInt);

    int countOnRoute(ItemIdentifier item);
}
