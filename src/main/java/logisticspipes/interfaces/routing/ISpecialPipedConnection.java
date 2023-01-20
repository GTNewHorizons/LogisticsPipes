package logisticspipes.interfaces.routing;

import java.util.EnumSet;
import java.util.List;
import logisticspipes.proxy.specialconnection.SpecialPipeConnection.ConnectionInformation;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import net.minecraftforge.common.util.ForgeDirection;

public interface ISpecialPipedConnection {

    boolean init();

    boolean isType(IPipeInformationProvider startPipe);

    List<ConnectionInformation> getConnections(
            IPipeInformationProvider startPipe, EnumSet<PipeRoutingConnectionType> connection, ForgeDirection side);
}
