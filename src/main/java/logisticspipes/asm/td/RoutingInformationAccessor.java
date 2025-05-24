package logisticspipes.asm.td;

import logisticspipes.routing.ItemRoutingInformation;

public interface RoutingInformationAccessor {

    ItemRoutingInformation getRoutingInformation();

    void setRoutingInformation(ItemRoutingInformation routingInformation);

}
