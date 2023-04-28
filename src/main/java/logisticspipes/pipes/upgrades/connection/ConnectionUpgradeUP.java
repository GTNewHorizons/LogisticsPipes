package logisticspipes.pipes.upgrades.connection;

import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.pipes.upgrades.ConnectionUpgrade;

public class ConnectionUpgradeUP extends ConnectionUpgrade {

    @Override
    public ForgeDirection getSide() {
        return ForgeDirection.UP;
    }
}
