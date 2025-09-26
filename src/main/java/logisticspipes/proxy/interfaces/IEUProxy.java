package logisticspipes.proxy.interfaces;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public interface IEUProxy {

    /**
     * Tests if the given sink accepts energy from the given source over the given direction
     * @param sink the sink
     * @param source the source
     * @param sinkSide the side on the sink where the energy will be injected
     * @return true if energy can be injected
     */
    boolean acceptsEnergyFrom(TileEntity sink, TileEntity source, ForgeDirection sinkSide);

    /**
     * If the this tile entity is a GTEnergy sink
     * @param tileEntity the tile entity to test
     * @return true if this tileEntity is a GTEnergy sink
     */
    boolean isEnergySink(TileEntity tileEntity);

    /**
     * The energy Units that this sink needs until it is completely filled
     *
     * @param sink the sink
     * @return the amount of energy
     */
    double demandedEnergyUnits(TileEntity sink);

    /**
     * The maximum accepted Amperage of the sink
     *
     * @param sink the sink
     * @return the amperage
     */
    long maxInputAmperage(TileEntity sink);

    /**
     * Injects an amount of energy into the sink
     * @param sink the sink
     * @param sinkSide the side of the sink where the energy will be injected
     * @param amount the amount of energy to inject
     * @param amperage the amperage
     * @return actual received amount
     */
    long injectEnergyUnits(TileEntity sink, ForgeDirection sinkSide, long amount, long amperage);

}
