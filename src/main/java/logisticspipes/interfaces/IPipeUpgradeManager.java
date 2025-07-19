package logisticspipes.interfaces;

import net.minecraftforge.common.util.ForgeDirection;

public interface IPipeUpgradeManager {

    boolean hasPowerPassUpgrade();

    boolean hasRFPowerSupplierUpgrade();

    int getIC2PowerLevel();

    /**
     * Returns the number of Upgrades the pipe has for this level of power (1 lv, 2 mv, ...)
     * @param level the power level
     * @return number of upgrades for this level
     */
    int getIC2MaxAmperage(long level);

    int getSpeedUpgradeCount();

    boolean isSideDisconnected(ForgeDirection side);

    boolean hasCCRemoteControlUpgrade();

    boolean hasCraftingMonitoringUpgrade();

    boolean isOpaque();

    boolean hasUpgradeModuleUpgrade();

    boolean hasCombinedSneakyUpgrade();

    ForgeDirection[] getCombinedSneakyOrientation();
}
