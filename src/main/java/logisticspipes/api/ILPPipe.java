package logisticspipes.api;

import com.gtnewhorizon.gtnhlib.blockpos.IBlockPos;

/**
 * Public interface implemented by LP's internal Pipe logic
 */
public interface ILPPipe {

    /**
     * @return true if the pipe can route items inside the network
     */
    boolean isRoutedPipe();

    IBlockPos getPos();
}
