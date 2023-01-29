package logisticspipes.asm.te;

import logisticspipes.utils.tuples.LPPosition;

import net.minecraftforge.common.util.ForgeDirection;

public interface ITileEntityChangeListener {

    void pipeRemoved(LPPosition pos);

    void pipeAdded(LPPosition pos, ForgeDirection side);

    void pipeModified(LPPosition pos);
}
