package logisticspipes.modules;

import java.util.Collection;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.interfaces.IInventoryUtil;
import logisticspipes.interfaces.IPipeServiceProvider;
import logisticspipes.interfaces.IWorldProvider;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.utils.AdjacentTile;
import logisticspipes.utils.SidedInventoryMinecraftAdapter;
import logisticspipes.utils.SinkReply;
import logisticspipes.utils.SinkReply.FixedPriority;
import logisticspipes.utils.WorldUtil;
import logisticspipes.utils.item.ItemIdentifier;

// IHUDModuleHandler,
public class ModuleSatelite extends LogisticsModule {

    private final CoreRoutedPipe pipe;

    public ModuleSatelite(CoreRoutedPipe pipeItemsSatelliteLogistics) {
        pipe = pipeItemsSatelliteLogistics;
    }

    @Override
    public void registerHandler(IWorldProvider world, IPipeServiceProvider service) {}

    @Override
    public final int getX() {
        return pipe.getX();
    }

    @Override
    public final int getY() {
        return pipe.getY();
    }

    @Override
    public final int getZ() {
        return pipe.getZ();
    }

    private final SinkReply _sinkReply = new SinkReply(FixedPriority.ItemSink, 0, true, false, 1, 0, null);

    @Override
    public SinkReply sinksItem(ItemIdentifier item, int bestPriority, int bestCustomPriority, boolean allowDefault,
            boolean includeInTransit) {
        if (bestPriority > _sinkReply.fixedPriority.ordinal() || (bestPriority == _sinkReply.fixedPriority.ordinal()
                && bestCustomPriority >= _sinkReply.customPriority)) {
            return null;
        }
        return new SinkReply(_sinkReply, spaceFor(item, includeInTransit));
    }

    private int spaceFor(ItemIdentifier item, boolean includeInTransit) {
        int count = 0;
        WorldUtil wUtil = new WorldUtil(pipe.getWorld(), pipe.getX(), pipe.getY(), pipe.getZ());
        for (AdjacentTile tile : wUtil.getAdjacentTileEntities(true)) {
            if (!(tile.tile instanceof IInventory)) {
                continue;
            }
            IInventory base = (IInventory) tile.tile;
            if (base instanceof net.minecraft.inventory.ISidedInventory) {
                base = new SidedInventoryMinecraftAdapter(
                        (net.minecraft.inventory.ISidedInventory) base,
                        tile.orientation.getOpposite(),
                        false);
            }
            IInventoryUtil inv = SimpleServiceLocator.inventoryUtilFactory.getInventoryUtil(base, tile.orientation);
            count += inv.roomForItem(item, 9999);
        }
        if (includeInTransit) {
            count -= pipe.countOnRoute(item);
        }
        return count;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {}

    @Override
    public void tick() {}

    @Override
    public boolean hasGenericInterests() {
        return false;
    }

    @Override
    public Collection<ItemIdentifier> getSpecificInterests() {
        return pipe.getSpecificInterests();
    }

    @Override
    public boolean interestedInAttachedInventory() {
        return false;
        // when we are default we are interested in everything anyway, otherwise we're only interested in our filter.
    }

    @Override
    public boolean interestedInUndamagedID() {
        return false;
    }

    @Override
    public boolean recievePassive() {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconTexture(IIconRegister register) {
        return null;
    }
}
