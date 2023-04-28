/*
 * Copyright (c) Krapht, 2011 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0,
 * or MMPL. Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package logisticspipes.routing;

import java.util.BitSet;
import java.util.List;
import java.util.UUID;

import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.api.ILogisticsPowerProvider;
import logisticspipes.interfaces.ISubSystemPowerProvider;
import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.tuples.LPPosition;
import logisticspipes.utils.tuples.Pair;

public interface IRouter {

    interface IRAction {

        boolean isInteresting(IRouter that);

        void doTo(IRouter that);
    }

    void destroy();

    void update(boolean doFullRefresh, CoreRoutedPipe pipe);

    void updateInterests(); // calls getInterests on the attached pipe, and updates the global cache.

    boolean isRoutedExit(ForgeDirection connection);

    boolean isSubPoweredExit(ForgeDirection connection);

    int getDistanceToNextPowerPipe(ForgeDirection dir);

    boolean hasRoute(int id, boolean active, ItemIdentifier type);

    ExitRoute getExitFor(int id, boolean active, ItemIdentifier type);

    List<List<ExitRoute>> getRouteTable();

    List<ExitRoute> getIRoutersByCost();

    CoreRoutedPipe getPipe();

    CoreRoutedPipe getCachedPipe();

    boolean isInDim(int dimension);

    boolean isAt(int dimension, int xCoord, int yCoord, int zCoord);

    UUID getId();

    LogisticsModule getLogisticsModule();

    void clearPipeCache();

    int getSimpleID();

    LPPosition getLPPosition();

    /**
     * @param hasBeenProcessed a bitset flagging which nodes have already been acted on (the router should set the bit
     *                         for it's own id, then return true.
     * @param actor            the visitor
     */
    void act(BitSet hasBeenProcessed, IRAction actor);

    void flagForRoutingUpdate();

    boolean checkAdjacentUpdate();

    /* Automated Disconnection */
    boolean isSideDisconneceted(ForgeDirection dir);

    List<ExitRoute> getDistanceTo(IRouter r);

    void clearInterests();

    List<Pair<ILogisticsPowerProvider, List<IFilter>>> getPowerProvider();

    List<Pair<ISubSystemPowerProvider, List<IFilter>>> getSubSystemPowerProvider();

    boolean isValidCache();

    // force-update LSA version in the network
    void forceLsaUpdate();

    List<ExitRoute> getRoutersOnSide(ForgeDirection direction);

    int getDimension();

    void queueTask(int i, IRouterQueuedTask callable);
}
