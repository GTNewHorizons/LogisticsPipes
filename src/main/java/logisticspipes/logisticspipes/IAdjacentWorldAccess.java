package logisticspipes.logisticspipes;

import java.util.List;

import logisticspipes.utils.AdjacentTile;

/**
 * This interface gives access to the surrounding world
 *
 * @author Krapht
 */
public interface IAdjacentWorldAccess {

    List<AdjacentTile> getConnectedEntities();

    int getRandomInt(int maxSize);
}
