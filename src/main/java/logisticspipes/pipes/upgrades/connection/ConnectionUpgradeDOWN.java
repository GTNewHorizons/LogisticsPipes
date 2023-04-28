package logisticspipes.pipes.upgrades.connection;

import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.pipes.upgrades.ConnectionUpgrade;

public class ConnectionUpgradeDOWN extends ConnectionUpgrade {

    @Override
    public ForgeDirection getSide() {
        return ForgeDirection.DOWN;
    }
}
