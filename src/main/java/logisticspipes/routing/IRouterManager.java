/*
 * Copyright (c) Krapht, 2011 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0,
 * or MMPL. Please check the contents of the license located in http://www.mod-buildcraft.com/MMPL-1.0.txt
 */
package logisticspipes.routing;

import java.util.List;
import java.util.UUID;

public interface IRouterManager {

    int getIDforUUID(UUID id);

    IRouter getOrCreateRouter(UUID routerUUid, int dimension, int xCoord, int yCoord, int zCoord,
            boolean forceCreateDuplicateAtCoordinate);

    IRouter getRouter(int id);

    boolean isRouter(int id);

    void removeRouter(int id);

    List<IRouter> getRouters();

    void serverStopClean();

    void clearClientRouters();

    void dimensionUnloaded(int dim);

    boolean isRouterUnsafe(int id, boolean isClientSide);

    IRouter getRouterUnsafe(Integer value1, boolean isClientSide);

    void printAllRouters();
}
