package logisticspipes.interfaces;

import net.minecraft.world.World;

public interface IHeadUpDisplayRendererProvider {

    IHeadUpDisplayRenderer getRenderer();

    int getX();

    int getY();

    int getZ();

    World getWorld();

    void startWatching();

    void stopWatching();
}
