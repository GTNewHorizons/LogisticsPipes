package logisticspipes.interfaces.routing;

import logisticspipes.routing.IRouter;

public interface IRequest extends Comparable<IRequest> {

    IRouter getRouter();

    int getID();
}
