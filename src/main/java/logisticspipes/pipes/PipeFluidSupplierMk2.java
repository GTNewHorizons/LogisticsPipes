package logisticspipes.pipes;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.factory.PosGuiData;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.screen.Tooltip;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.value.sync.PanelSyncManager;
import com.cleanroommc.modularui.value.sync.SyncHandlers;
import com.cleanroommc.modularui.widgets.CycleButtonWidget;
import com.cleanroommc.modularui.widgets.FluidSlot;
import com.cleanroommc.modularui.widgets.layout.Column;
import com.cleanroommc.modularui.widgets.layout.Row;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

import gnu.trove.iterator.TObjectIntIterator;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import logisticspipes.api.IMUICompatiblePipe;
import logisticspipes.compat.ModularUIHelper;
import logisticspipes.interfaces.routing.IRequestFluid;
import logisticspipes.interfaces.routing.IRequireReliableFluidTransport;
import logisticspipes.migration.LegacyHelper;
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
import lombok.extern.log4j.Log4j2;

@Log4j2
public class PipeFluidSupplierMk2 extends FluidRoutedPipe
        implements IRequestFluid, IRequireReliableFluidTransport, IMUICompatiblePipe {

    private boolean _lastRequestFailed = false;

    private final FluidTank phantomTank = new FluidTank(1);
    private int amount = 0;
    private boolean requestPartials = false;
    private int refillThreshold = 0;

    @Override
    public void addUIWidgets(ModularPanel panel, PosGuiData data, PanelSyncManager syncManager) {
        panel.background(ModularUIHelper.BACKGROUND_TEXTURE).bindPlayerInventory().child(
                new Column().widthRel(1.0F).top(6).coverChildrenHeight().child(
                        new Row().mainAxisAlignment(Alignment.MainAxis.CENTER)
                                .crossAxisAlignment(Alignment.CrossAxis.CENTER).widthRel(1.0F).coverChildrenHeight()
                                .child(IKey.lang("gui.fluidsuppliermk2.TargetInv").asWidget()))
                        .child(
                                new Row().mainAxisAlignment(Alignment.MainAxis.CENTER)
                                        .crossAxisAlignment(Alignment.CrossAxis.CENTER).marginTop(5)
                                        .coverChildrenHeight()
                                        .child(
                                                IKey.comp(IKey.lang("gui.fluidsuppliermk2.Fluid"), IKey.str(":"))
                                                        .asWidget())
                                        .child(
                                                new FluidSlot()
                                                        .syncHandler(
                                                                new FluidSlotSyncHandler(phantomTank).phantom(true)
                                                                        .controlsAmount(false))
                                                        .marginLeft(6).width(16).height(16))
                                        .child(
                                                new TextFieldWidget().marginLeft(6).width(80)
                                                        .setNumbers(0, Integer.MAX_VALUE).value(
                                                                SyncHandlers.intNumber(
                                                                        () -> amount,
                                                                        value -> this.amount = value)))
                                        .child(IKey.str("mB").asWidget().marginLeft(3)))
                        .child(
                                new Row().mainAxisAlignment(Alignment.MainAxis.CENTER)
                                        .crossAxisAlignment(Alignment.CrossAxis.CENTER).marginTop(5)
                                        .coverChildrenHeight()
                                        .child(
                                                IKey.comp(IKey.lang("gui.fluidsuppliermk2.partial"), IKey.str(":"))
                                                        .asWidget())
                                        .child(
                                                new CycleButtonWidget().marginLeft(6).width(24)
                                                        .value(
                                                                SyncHandlers.bool(
                                                                        () -> this.requestPartials,
                                                                        value -> this.requestPartials = value))
                                                        .overlay(
                                                                ModularUIHelper.reallyDynamicKey(
                                                                        () -> this.requestPartials ? IKey.lang(
                                                                                "gui.fluidsuppliermk2.partial.yes")
                                                                                : IKey.lang(
                                                                                        "gui.fluidsuppliermk2.partial.no")))
                                                        .tooltipBuilder((tooltip -> {
                                                            tooltip.addLine(
                                                                    IKey.lang("gui.fluidsuppliermk2.partial.tip"));

                                                            if (requestPartials) {
                                                                tooltip.addLine(
                                                                        IKey.lang(
                                                                                "gui.fluidsuppliermk2.partial.yes.tip"));
                                                            } else {
                                                                tooltip.addLine(
                                                                        IKey.lang(
                                                                                "gui.fluidsuppliermk2.partial.no.tip"));
                                                            }
                                                        })).tooltipPos(Tooltip.Pos.ABOVE)))
                        .child(
                                new Row().mainAxisAlignment(Alignment.MainAxis.CENTER)
                                        .crossAxisAlignment(Alignment.CrossAxis.CENTER).marginTop(5)
                                        .coverChildrenHeight().child(
                                                IKey.comp(
                                                        IKey.lang("gui.fluidsuppliermk2.refill_if_depleted"),
                                                        IKey.str(":")).asWidget()))
                        .child(
                                new Row().marginTop(5).mainAxisAlignment(Alignment.MainAxis.CENTER)
                                        .coverChildrenHeight()
                                        .child(
                                                new TextFieldWidget().width(80).setNumbers(0, Integer.MAX_VALUE).value(
                                                        SyncHandlers.intNumber(
                                                                () -> refillThreshold,
                                                                value -> this.refillThreshold = value))

                                        ).child(IKey.str("mB").asWidget().marginLeft(3))
                                        .child(IKey.str("§9[?]").asWidget().marginLeft(6).tooltipBuilder(tooltip -> {
                                            tooltip.setAutoUpdate(true);
                                            tooltip.addLine(
                                                    IKey.lang(
                                                            "gui.fluidsuppliermk2.refill_if_depleted.tip",
                                                            this.refillThreshold != 0 ? this.refillThreshold : "n"));
                                            tooltip.addLine(
                                                    IKey.lang("gui.fluidsuppliermk2.refill_if_depleted.tip.zero"));
                                        }).tooltipPos(Tooltip.Pos.ABOVE))));
    }

    @Override
    public String getId() {
        return "fluid_supplier_mk2";
    }

    @Override
    public int getGuiWidth() {
        return 184;
    }

    @Override
    public int getGuiHeight() {
        return 186;
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

    private final TObjectIntMap<FluidIdentifier> _requestedItems = new TObjectIntHashMap<>(8, 0.5f, -1);

    @Override
    protected void fillSide(FluidStack toFill, ForgeDirection tankLocation, IFluidHandler tile) {
        if (phantomTank.getFluid() == null) {
            // shouldn't happen, but ok
            return;
        }
        // check if this tank need more fluid
        int have = 0;

        for (FluidTankInfo info : tile.getTankInfo(ForgeDirection.UNKNOWN)) {
            if (info.fluid != null && info.fluid.isFluidEqual(phantomTank.getFluid())) {
                have += info.fluid.amount;
            }
        }
        int vacant = amount - have;
        if (vacant <= 0)
            // too much fluid..
            return;
        // we can't actually be too strict with checking here. maybe the tank is being drained by
        // another party faster than LP can sustain.
        // the check to send fluid request does not run every tick after all...
        // so we just check if minimum is reached
        if (refillThreshold != 0 && vacant < refillThreshold) {
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
        if (phantomTank.getFluid() == null) {
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
            FluidIdentifier fIdent = FluidIdentifier.get(phantomTank.getFluid());
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

            // Reduce what have been requested already
            for (TObjectIntIterator<FluidIdentifier> iter = requestDiscount.iterator(); iter.hasNext();) {
                iter.advance();
                wantFluids.adjustValue(iter.key(), -iter.value());
            }

            setRequestFailed(false);

            // Make request

            for (TObjectIntIterator<FluidIdentifier> iter = wantFluids.iterator(); iter.hasNext();) {
                iter.advance();
                FluidIdentifier need = iter.key();
                int countToRequest = iter.value();

                if (countToRequest <= 0) {
                    // skip entries that already have enough in it
                    continue;
                }

                if (refillThreshold != 0 && countToRequest < refillThreshold) {
                    continue;
                }

                if (!useEnergy(11)) {
                    break;
                }

                boolean success = false;

                if (requestPartials) {
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
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        if (compound.hasKey("items")) {
            // legacy
            LegacyHelper.readItemIdentifierInventoryAndConvertToTank(phantomTank, compound, "");
        } else {
            phantomTank.readFromNBT(compound.getCompoundTag("tank"));
        }

        if (compound.hasKey("_bucketMinimum")) {
            // legacy
            byte bucketMinimum = compound.getByte("_bucketMinimum");
            if (bucketMinimum == 0) refillThreshold = 0;
            if (bucketMinimum == 1) refillThreshold = 1000;
            if (bucketMinimum == 2) refillThreshold = 2000;
            if (bucketMinimum == 3) refillThreshold = 5000;
        } else {
            refillThreshold = compound.getInteger("refillThreshold");
        }

        requestPartials = compound.getBoolean("requestpartials");
        amount = compound.getInteger("amount");
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setBoolean("requestpartials", requestPartials);
        compound.setInteger("amount", amount);
        compound.setInteger("refillThreshold", refillThreshold);

        compound.setTag("tank", phantomTank.writeToNBT(new NBTTagCompound()));
    }

    private void decreaseRequested(FluidIdentifier liquid, int remaining) {
        // see if we can get an exact match
        int count = _requestedItems.get(liquid);
        if (count <= 0) return;
        if (count <= remaining) _requestedItems.remove(liquid);
        else _requestedItems.put(liquid, count - remaining);
        if (remaining > count) {
            // we have no idea what this is, log it.
            debug.log("liquid supplier got unexpected item %s", liquid.toString());
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

    @Override
    public void onWrenchClicked(EntityPlayer player) {
        openGui(player, this);
    }

    @Override
    public boolean canReceiveFluid() {
        return false;
    }
}
