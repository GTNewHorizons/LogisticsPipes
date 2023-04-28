package logisticspipes.proxy;

import cpw.mods.fml.common.Loader;
import logisticspipes.proxy.specialinventoryhandler.*;

public class SpecialInventoryHandlerManager {

    public static void load() {
        if (Loader.isModLoaded("factorization")) {
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new BarrelInventoryHandler());
        }

        if (Loader.isModLoaded("betterstorage")) {
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new CrateInventoryHandler());
        }

        if (Loader.isModLoaded("AppliedEnergistics2-Core") || Loader.isModLoaded("appliedenergistics2-core")) {
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new AEInterfaceInventoryHandler());
        }

        if (Loader.isModLoaded("JABBA")) {
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new JABBAInventoryHandler());
        }

        if (Loader.isModLoaded("StorageDrawers")) {
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new StorageDrawersInventoryHandler());
        }

        try {
            Class.forName("powercrystals.minefactoryreloaded.api.IDeepStorageUnit");
            SimpleServiceLocator.inventoryUtilFactory.registerHandler(new DSUInventoryHandler());
        } catch (ClassNotFoundException ignored) {}
    }
}
