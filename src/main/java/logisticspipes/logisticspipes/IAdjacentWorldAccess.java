package logisticspipes.logisticspipes;

import java.util.List;
import logisticspipes.utils.AdjacentTile;

/**
 * This interface gives access to the surrounding world
 *
 * @author Krapht
 */
public interface IAdjacentWorldAccess {

    public List<AdjacentTile> getConnectedEntities();

    public int getRandomInt(int maxSize);
}
