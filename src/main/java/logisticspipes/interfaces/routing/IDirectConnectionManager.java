package logisticspipes.interfaces.routing;

import java.util.UUID;

import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.routing.IRouter;

public interface IDirectConnectionManager {

    boolean hasDirectConnection(IRouter router);

    boolean addDirectConnection(UUID ident, IRouter router);

    CoreRoutedPipe getConnectedPipe(IRouter router);

    void removeDirectConnection(IRouter router);
}
