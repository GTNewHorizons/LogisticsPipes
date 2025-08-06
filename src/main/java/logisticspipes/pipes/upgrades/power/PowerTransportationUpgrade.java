package logisticspipes.pipes.upgrades.power;

import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.pipes.upgrades.IPipeUpgrade;

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
     * @return the ic2 voltage that this power transportation upgrade can transport
     */
     public long getPowerLevel() { return 0; }
}
