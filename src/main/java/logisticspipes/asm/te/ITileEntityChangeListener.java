package logisticspipes.asm.te;

import net.minecraftforge.common.util.ForgeDirection;

import logisticspipes.utils.tuples.LPPosition;

public interface ITileEntityChangeListener {

    void pipeRemoved(LPPosition pos);

    void pipeAdded(LPPosition pos, ForgeDirection side);

    void pipeModified(LPPosition pos);
}
