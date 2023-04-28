package logisticspipes.routing.pathfinder;

import java.util.List;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.transport.LPTravelingItem;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.tuples.LPPosition;

public interface IPipeInformationProvider {

    boolean isCorrect();

    int getX();

    int getY();

    int getZ();

    World getWorld();

    boolean isRouterInitialized();

    boolean isRoutingPipe();

    CoreRoutedPipe getRoutingPipe();

    TileEntity getTile(ForgeDirection direction);

    boolean isFirewallPipe();

    IFilter getFirewallFilter();

    TileEntity getTile();

    boolean divideNetwork();

    boolean powerOnly();

    boolean isOnewayPipe();

    boolean isOutputOpen(ForgeDirection direction);

    boolean canConnect(TileEntity to, ForgeDirection direction, boolean flag);

    double getDistance();

    boolean isItemPipe();

    boolean isFluidPipe();

    boolean isPowerPipe();

    double getDistanceTo(int destinationint, ForgeDirection ignore, ItemIdentifier ident, boolean isActive,
            double travled, double max, List<LPPosition> visited);

    boolean acceptItem(LPTravelingItem item, TileEntity from);

    void refreshTileCacheOnSide(ForgeDirection side);
}
