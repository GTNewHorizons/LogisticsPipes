package logisticspipes.pipes.basic;

import logisticspipes.blocks.powertile.LogisticsPowerProviderTileEntity;
import logisticspipes.interfaces.ISubSystemPowerProvider;
import logisticspipes.interfaces.routing.IFilter;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.cofh.subproxies.ICoFHEnergyReceiver;
import logisticspipes.proxy.interfaces.IEUProxy;
import logisticspipes.utils.AdjacentTile;
import logisticspipes.utils.WorldUtil;
import logisticspipes.utils.tuples.Pair;
import net.minecraft.nbt.NBTTagCompound;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class PowerSupplierHandler {

    private static final long INTERNAL_RF_BUFFER_MAX = 10000;
    private static long INTERNAL_IC2_BUFFER_MAX = 0;

    private final CoreRoutedPipe pipe;

    private long internal_RF_Buffer = 0;
    private long internal_IC2_Buffer = 0;

    public PowerSupplierHandler(CoreRoutedPipe pipe) {
        this.pipe = pipe;
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        if (internal_RF_Buffer > 0) {
            nbttagcompound.setFloat("bufferRF", internal_RF_Buffer);
        }
        if (internal_IC2_Buffer > 0) {
            nbttagcompound.setFloat("bufferEU", internal_IC2_Buffer);
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        internal_RF_Buffer = nbttagcompound.getLong("bufferRF");
        internal_IC2_Buffer = nbttagcompound.getLong("bufferEU");
    }

    public void renderLaser(AdjacentTile adjacentTile, int laserType) {
        pipe.container.addLaser(adjacentTile.orientation, 0.5F, laserType, false, true);
    }

    /**
     * Provides Power to the given adjacent tile, if all prerequisites are fulfilled.
     *
     * @param adjacentTile the adjacent tile to provide power to
     */
    private void providerRFPower(AdjacentTile adjacentTile) {

        if (!pipe.getUpgradeManager().hasRFPowerSupplierUpgrade()) return;
        if (internal_RF_Buffer == 0) return;
        if (!SimpleServiceLocator.cofhPowerProxy.isEnergyReceiver(adjacentTile.tile)) return;

        ICoFHEnergyReceiver receiver = SimpleServiceLocator.cofhPowerProxy.getEnergyReceiver(adjacentTile.tile);
        var opposingDir = adjacentTile.orientation.getOpposite();

        if (!receiver.canConnectEnergy(opposingDir)) return;

        int energyToProvide = (int) Math.min(receiver.getMaxEnergyStored(opposingDir) - receiver.getEnergyStored(opposingDir), internal_RF_Buffer);
        int used = receiver.receiveEnergy(opposingDir, energyToProvide, false);


        if (used > 0) {
            renderLaser(adjacentTile, LogisticsPowerProviderTileEntity.RF_COLOR);
            internal_RF_Buffer -= used;
        }

        if (internal_RF_Buffer < 0) internal_RF_Buffer = 0;

    }

    public void receiveRFPower(List<ISubSystemPowerProvider> powerProvider) {
        for (ISubSystemPowerProvider provider : powerProvider) {
            long needed = INTERNAL_RF_BUFFER_MAX - internal_RF_Buffer;
            if (needed <= 0F) return;
            provider.requestPower(pipe.getRouterId(), needed);
        }
    }


    public void provideEUPower(AdjacentTile adjacentTile) {
        if (pipe.upgradeManager.getIC2PowerLevel() == 0) return;

        IEUProxy proxy;

        if (SimpleServiceLocator.IC2Proxy.isEnergySink(adjacentTile.tile)) proxy = SimpleServiceLocator.IC2Proxy;
        else if (SimpleServiceLocator.gtProxy.isEnergySink(adjacentTile.tile)) proxy = SimpleServiceLocator.gtProxy;
        else return;

        var opposingDir = adjacentTile.orientation.getOpposite();

        if (!proxy.acceptsEnergyFrom(adjacentTile.tile, pipe.container, opposingDir)) return;

        long pipePowerLevel = pipe.upgradeManager.getIC2PowerLevel();
        long energyToProvide = (long) Math.min(Math.min(proxy.demandedEnergyUnits(adjacentTile.tile), pipePowerLevel), internal_IC2_Buffer);
        if (energyToProvide <= 0) return;

        double maxAmps = Math.min(internal_IC2_Buffer / (double) pipePowerLevel, Math.min(proxy.maxInputAmperage(adjacentTile.tile), pipe.upgradeManager.getIC2MaxAmperage(pipePowerLevel)));
        long used = 0;
        long multiAmps = (long) Math.floor(maxAmps);
        if (multiAmps > 0) used += proxy.injectEnergyUnits(adjacentTile.tile, opposingDir, energyToProvide, multiAmps);
        if (maxAmps - multiAmps != 0) used += proxy.injectEnergyUnits(adjacentTile.tile, opposingDir, energyToProvide - used, 1);

        if (used > 0) {
            renderLaser(adjacentTile, LogisticsPowerProviderTileEntity.IC2_COLOR);
            internal_IC2_Buffer -= used;
        }

        if (internal_IC2_Buffer < 0) internal_IC2_Buffer = 0;
    }

    public void receiveEUPower(List<ISubSystemPowerProvider> powerProvider) {
        Supplier<Long> needSupplier = () -> INTERNAL_IC2_BUFFER_MAX - internal_IC2_Buffer;

        if (needSupplier.get() <= 0) return;

        for (ISubSystemPowerProvider provider : powerProvider) {
            var need = needSupplier.get();
            if (need <= 0F) return;
            provider.requestPower(pipe.getRouterId(), need);
        }
    }

    public void update() {
        WorldUtil wu = new WorldUtil(pipe.getWorld(), pipe.getX(), pipe.getY(), pipe.getZ());
        updateICBuffer();

        var powerProvider = pipe.getRouter().getSubSystemPowerProvider().stream().filter(it -> it.getValue2().stream().noneMatch(IFilter::blockPower)).map(Pair::getValue1).collect(Collectors.toList());

        for (AdjacentTile adjacentTile : wu.getAdjacentTileEntities()) {
            if (!pipe.canPipeConnect(adjacentTile.tile, adjacentTile.orientation)) continue;
            if (adjacentTile.tile instanceof LogisticsPowerProviderTileEntity) {
                continue;
            }
            providerRFPower(adjacentTile);
            receiveRFPower(powerProvider.stream().filter(it -> it.getBrand().equals("RF")).collect(Collectors.toList()));

            provideEUPower(adjacentTile);
            receiveEUPower(powerProvider.stream().filter(it -> it.getBrand().equals("EU")).collect(Collectors.toList()));
        }
    }

    public void updateICBuffer() {
        var upgradeManager = pipe.getUpgradeManager();
        INTERNAL_IC2_BUFFER_MAX = upgradeManager.getIC2PowerLevel() * upgradeManager.getIC2MaxAmperage(upgradeManager.getIC2PowerLevel()) * 8L;
    }

    public void addRFPower(long toSend) {
        internal_RF_Buffer += toSend;
    }

    public void addIC2Power(long toSend) {
        internal_IC2_Buffer += toSend;
    }
}
