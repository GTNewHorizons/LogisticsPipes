package logisticspipes.proxy.specialconnection;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.interfaces.routing.ISpecialPipedConnection;
import logisticspipes.routing.PipeRoutingConnectionType;
import logisticspipes.routing.pathfinder.IPipeInformationProvider;
import lombok.AllArgsConstructor;
import lombok.Data;

public class SpecialPipeConnection {

    private final List<ISpecialPipedConnection> handler = new ArrayList<>();

    public void registerHandler(ISpecialPipedConnection connectionHandler) {
        if (connectionHandler.init()) {
            handler.add(connectionHandler);
        }
    }

    public List<ConnectionInformation> getConnectedPipes(IPipeInformationProvider startPipe,
            EnumSet<PipeRoutingConnectionType> connection, ForgeDirection side) {
        for (ISpecialPipedConnection connectionHandler : handler) {
            if (connectionHandler.isType(startPipe)) {
                return connectionHandler.getConnections(startPipe, connection, side);
            }
        }
        return new ArrayList<>();
    }

    @Data
    @AllArgsConstructor
    public static class ConnectionInformation {

        private IPipeInformationProvider connectedPipe;
        private EnumSet<PipeRoutingConnectionType> connectionFlags;
        private ForgeDirection insertOrientation;
        private ForgeDirection exitOrientation;
        private double distance;
    }
}
