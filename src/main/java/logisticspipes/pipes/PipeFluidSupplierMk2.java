package logisticspipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import logisticspipes.LogisticsPipes;
import logisticspipes.interfaces.routing.IRequestFluid;
import logisticspipes.interfaces.routing.IRequireReliableFluidTransport;
import logisticspipes.network.GuiIDs;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.pipe.FluidSupplierAmount;
import logisticspipes.pipes.basic.fluid.FluidRoutedPipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.request.RequestTree;
import logisticspipes.textures.Textures;
import logisticspipes.textures.Textures.TextureType;
import logisticspipes.transport.PipeFluidTransportLogistics;
import logisticspipes.utils.AdjacentTile;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.WorldUtil;
import logisticspipes.utils.item.ItemIdentifierInventory;
import lombok.Getter;

public class PipeFluidSupplierMk2 extends FluidRoutedPipe implements IRequestFluid, IRequireReliableFluidTransport {

    private boolean _lastRequestFailed = false;

    public enum MinMode {

        NONE(0),
        ONEBUCKET(1000),
        TWOBUCKET(2000),
        FIVEBUCKET(5000);

        @Getter
        private final int amount;

        MinMode(int amount) {
            this.amount = amount;
        }
    }

    public PipeFluidSupplierMk2(Item item) {
        super(item);
        throttleTime = 100;
    }

    @Override
    public void sendFailed(FluidIdentifier value1, Integer value2) {
        liquidLost(value1, value2);
    }

    @Override
    public ItemSendMode getItemSendMode() {
        return ItemSendMode.Fast;
    }

    @Override
    public boolean canInsertFromSideToTanks() {
        return true;
    }

    @Override
    public boolean canInsertToTanks() {
        return true;
    }

    /* TRIGGER INTERFACE */
    public boolean isRequestFailed() {
        return _lastRequestFailed;
    }

    public void setRequestFailed(boolean value) {
        _lastRequestFailed = value;
    }

    @Override
    public TextureType getCenterTexture() {
        return Textures.LOGISTICSPIPE_LIQUIDSUPPLIER_MK2_TEXTURE;
    }

    @Override
    public boolean hasGenericInterests() {
        return true;
    }

    // from PipeFluidSupplierMk2
    private final ItemIdentifierInventory dummyInventory = new ItemIdentifierInventory(
            1,
            "Fluid to keep stocked",
            127,
            true);
    private int amount = 0;

    private final TObjectIntMap<FluidIdentifier> _requestedItems = new TObjectIntHashMap<>(8, 0.5f, -1);

    private boolean _requestPartials = false;
    private MinMode _bucketMinimum = MinMode.ONEBUCKET;

    @Override
    protected void fillSide(FluidStack toFill, ForgeDirection tankLocation, IFluidHandler tile) {
        if (dummyInventory.getStackInSlot(0) == null)
            // shouldn't happen, but ok
            return;
        // check if this tank need more fluid
        int have = 0;
        FluidIdentifier fIdent = FluidIdentifier.get(dummyInventory.getIDStackInSlot(0).getItem());
        for (FluidTankInfo info : tile.getTankInfo(ForgeDirection.UNKNOWN)) {
            if (info.fluid != null && fIdent.equals(FluidIdentifier.get(info.fluid))) have += info.fluid.amount;
        }
        int vacant = amount - have;
        if (vacant <= 0)
            // too much fluid..
            return;
        // we can't actually be too strict with checking here. maybe the tank is being drained by
        // another party faster than LP can sustain.
        // the check to send fluid request does not run every tick after all...
        // so we just check if minimum is reached
        if (_bucketMinimum.getAmount() != 0 && vacant < _bucketMinimum.getAmount()) {
            // not enough spare space - this packet must be for tank on different side
            return;
        }
        // attempt to fill now... don't fill too much though
        // if insertion on all sides failed, surplus will end up in one of the internal pipe tanks
        // it won't be wasted
        FluidStack filled = toFill.copy();
        filled.amount = Math.min(filled.amount, vacant);
        toFill.amount -= tile.fill(tankLocation.getOpposite(), filled, true);
    }

    @Override
    public void throttledUpdateEntity() {
        if (!isEnabled()) {
            return;
        }
        if (MainProxy.isClient(container.getWorld())) {
            return;
        }
        super.throttledUpdateEntity();
        if (dummyInventory.getStackInSlot(0) == null) {
            return;
        }

        TObjectIntMap<FluidIdentifier> requestDiscount = new TObjectIntHashMap<>(_requestedItems);
        FluidTank centerTank = ((PipeFluidTransportLogistics) transport).internalTank;
        if (centerTank != null && centerTank.getFluid() != null) {
            requestDiscount.adjustOrPutValue(
                    FluidIdentifier.get(centerTank.getFluid()),
                    centerTank.getFluid().amount,
                    centerTank.getFluid().amount);
        }

        WorldUtil worldUtil = new WorldUtil(getWorld(), getX(), getY(), getZ());
        for (AdjacentTile tile : worldUtil.getAdjacentTileEntities(true)) {
            if (!(tile.tile instanceof IFluidHandler)
                    || SimpleServiceLocator.pipeInformationManager.isItemPipe(tile.tile)) {
                continue;
            }
            IFluidHandler container = (IFluidHandler) tile.tile;
            if (container.getTankInfo(ForgeDirection.UNKNOWN) == null
                    || container.getTankInfo(ForgeDirection.UNKNOWN).length == 0) {
                continue;
            }

            // How much should I request?
            TObjectIntMap<FluidIdentifier> wantFluids = new TObjectIntHashMap<>(8);
            FluidIdentifier fIdent = FluidIdentifier.get(dummyInventory.getIDStackInSlot(0).getItem());
            wantFluids.put(fIdent, amount);

            FluidTankInfo[] result = container.getTankInfo(ForgeDirection.UNKNOWN);
            for (FluidTankInfo slot : result) {
                if (slot == null || slot.fluid == null || slot.fluid.getFluidID() == 0) {
                    continue;
                }
                wantFluids.adjustValue(FluidIdentifier.get(slot.fluid), -slot.fluid.amount);
            }

            // What does our sided internal tank have
            if (tile.orientation.ordinal() < ((PipeFluidTransportLogistics) transport).sideTanks.length) {
                FluidTank sideTank = ((PipeFluidTransportLogistics) transport).sideTanks[tile.orientation.ordinal()];
                if (sideTank != null && sideTank.getFluid() != null) {
                    wantFluids.adjustValue(FluidIdentifier.get(sideTank.getFluid()), -sideTank.getFluid().amount);
                }
            }

            // drop entries that already have enough in it
            wantFluids.retainEntries((k, v) -> v >= 0);

            // Reduce what have been requested already
            for (TObjectIntIterator<FluidIdentifier> iter = requestDiscount.iterator(); iter.hasNext();) {
                iter.advance();
                wantFluids.adjustValue(iter.key(), -iter.value());
                iter.remove();
            }

            setRequestFailed(false);

            // Make request

            for (TObjectIntIterator<FluidIdentifier> iter = wantFluids.iterator(); iter.hasNext();) {
                iter.advance();
                FluidIdentifier need = iter.key();
                int countToRequest = iter.value();
                if (countToRequest == 0) {
                    continue;
                } else if (countToRequest < 0) {
                    // at this point a negative value can only come from requestDiscount, so add it back
                    requestDiscount.adjustOrPutValue(need, -countToRequest, -countToRequest);
                    continue;
                }
                if (_bucketMinimum.getAmount() != 0 && countToRequest < _bucketMinimum.getAmount()) {
                    continue;
                }

                if (!useEnergy(11)) {
                    break;
                }

                boolean success = false;

                if (_requestPartials) {
                    countToRequest = RequestTree.requestFluidPartial(need, countToRequest, this, null);
                    if (countToRequest > 0) {
                        success = true;
                    }
                } else {
                    success = RequestTree.requestFluid(need, countToRequest, this, null);
                }

                if (success) {
                    _requestedItems.adjustOrPutValue(need, countToRequest, countToRequest);
                } else {
                    setRequestFailed(true);
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        dummyInventory.readFromNBT(nbttagcompound, "");
        _requestPartials = nbttagcompound.getBoolean("requestpartials");
        amount = nbttagcompound.getInteger("amount");
        _bucketMinimum = MinMode.values()[nbttagcompound.getByte("_bucketMinimum")];
    }

    @Override
    public void writeToNBT(NBTTagCompound nbttagcompound) {
        super.writeToNBT(nbttagcompound);
        dummyInventory.writeToNBT(nbttagcompound, "");
        nbttagcompound.setBoolean("requestpartials", _requestPartials);
        nbttagcompound.setInteger("amount", amount);
        nbttagcompound.setByte("_bucketMinimum", (byte) _bucketMinimum.ordinal());
    }

    private void decreaseRequested(FluidIdentifier liquid, int remaining) {
        // see if we can get an exact match
        int count = _requestedItems.get(liquid);
        if (count <= 0) return;
        if (count <= remaining) _requestedItems.remove(liquid);
        else _requestedItems.put(liquid, count - remaining);
        if (remaining > count) {
            // we have no idea what this is, log it.
            debug.log("liquid supplier got unexpected item " + liquid.toString());
        }
    }

    @Override
    public void liquidLost(FluidIdentifier item, int amount) {
        decreaseRequested(item, amount);
    }

    @Override
    public void liquidArrived(FluidIdentifier item, int amount) {
        decreaseRequested(item, amount);
        delayThrottle();
    }

    @Override
    public void liquidNotInserted(FluidIdentifier item, int amount) {}

    public boolean isRequestingPartials() {
        return _requestPartials;
    }

    public void setRequestingPartials(boolean value) {
        _requestPartials = value;
    }

    public MinMode getMinMode() {
        return _bucketMinimum;
    }

    public void setMinMode(MinMode value) {
        _bucketMinimum = value;
    }

    @Override
    public void onWrenchClicked(EntityPlayer entityplayer) {
        entityplayer
                .openGui(LogisticsPipes.instance, GuiIDs.GUI_FluidSupplier_MK2_ID, getWorld(), getX(), getY(), getZ());
    }

    public IInventory getDummyInventory() {
        return dummyInventory;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        if (MainProxy.isClient(container.getWorld())) {
            this.amount = amount;
        }
    }

    public void changeFluidAmount(int change, EntityPlayer player) {
        amount += change;
        if (amount <= 0) {
            amount = 0;
        }
        MainProxy.sendPacketToPlayer(
                PacketHandler.getPacket(FluidSupplierAmount.class).setInteger(amount).setPosX(getX()).setPosY(getY())
                        .setPosZ(getZ()),
                player);
    }

    @Override
    public boolean canReceiveFluid() {
        return false;
    }
}
