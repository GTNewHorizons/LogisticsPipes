package logisticspipes.pipes.upgrades;

import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.pipes.basic.CoreRoutedPipe;

public class PowerTransportationUpgrade implements IPipeUpgrade {

    @Override
    public boolean needsUpdate() {
        return false;
    }

    @Override
    public boolean isAllowedForPipe(CoreRoutedPipe pipe) {
        return true;
    }

    @Override
    public boolean isAllowedForModule(LogisticsModule pipe) {
        return false;
    }

    @Override
    public String[] getAllowedPipes() {
        return new String[]{"all"};
    }

    @Override
    public String[] getAllowedModules() {
        return new String[]{};
    }

    /**
     * @return the ic2 voltage that this power transportation upgrade can superconduct (with 0 loss)
     */
    public int getSuperconductorLevel() {
        return 0;
    }
}
