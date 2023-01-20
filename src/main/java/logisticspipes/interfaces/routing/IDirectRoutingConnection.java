package logisticspipes.interfaces.routing;

import logisticspipes.routing.ItemRoutingInformation;

public interface IDirectRoutingConnection {

    int getConnectionResistance();

    void addItem(ItemRoutingInformation info);
}
