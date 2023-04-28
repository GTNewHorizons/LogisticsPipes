package logisticspipes.modules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gregtech.api.enums.OrePrefixes;
import gregtech.api.objects.ItemData;
import gregtech.api.util.GT_OreDictUnificator;
import gregtech.common.tileentities.automation.GT_MetaTileEntity_TypeFilter;
import logisticspipes.gui.hud.modules.HUDStringBasedItemSink;
import logisticspipes.interfaces.*;
import logisticspipes.modules.abstractmodules.LogisticsGuiModule;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractguis.ModuleCoordinatesGuiProvider;
import logisticspipes.network.abstractguis.ModuleInHandGuiProvider;
import logisticspipes.network.guis.module.inhand.StringBasedItemSinkModuleGuiInHand;
import logisticspipes.network.guis.module.inpipe.StringBasedItemSinkModuleGuiSlot;
import logisticspipes.network.packets.hud.HUDStartModuleWatchingPacket;
import logisticspipes.network.packets.hud.HUDStopModuleWatchingPacket;
import logisticspipes.network.packets.module.ItemSinkListPacket;
import logisticspipes.pipes.PipeLogisticsChassi;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.PlayerCollectionList;
import logisticspipes.utils.SinkReply;
import logisticspipes.utils.item.ItemIdentifier;

public class ModuleTypeFilterItemSink extends LogisticsGuiModule
        implements IClientInformationProvider, IHUDModuleHandler, IModuleWatchReciver, IStringBasedModule {

    private final PlayerCollectionList localModeWatchers = new PlayerCollectionList();
    private final IHUDModuleRenderer HUD = new HUDStringBasedItemSink(this);

    private SinkReply sinkReply;
    private final EnumSet<OrePrefixes> prefixes = EnumSet.noneOf(OrePrefixes.class);
    private List<String> clientPrefixes = new ArrayList<>();

    @Override
    public void registerPosition(ModulePositionType slot, int positionInt) {
        super.registerPosition(slot, positionInt);
        sinkReply = new SinkReply(
                SinkReply.FixedPriority.GT_TypeFilterSink,
                0,
                true,
                false,
                5,
                0,
                new PipeLogisticsChassi.ChassiTargetInformation(getPositionInt()));
    }

    @Override
    public SinkReply sinksItem(ItemIdentifier item, int bestPriority, int bestCustomPriority, boolean allowDefault,
            boolean includeInTransit) {
        if (bestPriority > sinkReply.fixedPriority.ordinal() || (bestPriority == sinkReply.fixedPriority.ordinal()
                && bestCustomPriority >= sinkReply.customPriority)) {
            return null;
        }

        if (isStackAllowed(item)) {
            return sinkReply;
        }

        return null;
    }

    private boolean isStackAllowed(ItemIdentifier identifier) {
        ItemStack stack = identifier.makeNormalStackNoTag(1);
        ItemData data = GT_OreDictUnificator.getItemData(stack);

        for (OrePrefixes prefix : prefixes) {
            if (prefix == OrePrefixes.ore) {
                if (data != null && data.mPrefix != null
                        && GT_MetaTileEntity_TypeFilter.OREBLOCK_PREFIXES.contains(data.mPrefix)) {
                    return true;
                }
            }

            if (prefix.contains(stack)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public LogisticsModule getSubModule(int slot) {
        return null;
    }

    @Override
    public void tick() {}

    @Override
    public List<String> getClientInformation() {
        List<String> list = new ArrayList<>();
        if (prefixes.isEmpty()) {
            return Collections.singletonList("No prefixes");
        }

        list.add("Prefixes: ");
        for (OrePrefixes prefix : prefixes) {
            list.add(prefix.toString());
        }
        return list;
    }

    @Override
    public void startHUDWatching() {
        MainProxy.sendPacketToServer(PacketHandler.getPacket(HUDStartModuleWatchingPacket.class).setModulePos(this));
    }

    @Override
    public void stopHUDWatching() {
        MainProxy.sendPacketToServer(PacketHandler.getPacket(HUDStopModuleWatchingPacket.class).setModulePos(this));
    }

    @Override
    public void startWatching(EntityPlayer player) {
        localModeWatchers.add(player);
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        MainProxy.sendPacketToPlayer(
                PacketHandler.getPacket(ItemSinkListPacket.class).setNbt(nbt).setModulePos(this),
                player);
    }

    @Override
    public void stopWatching(EntityPlayer player) {
        localModeWatchers.remove(player);
    }

    @Override
    protected ModuleCoordinatesGuiProvider getPipeGuiProvider() {
        NBTTagCompound nbt = new NBTTagCompound();
        writeToNBT(nbt);
        return NewGuiHandler.getGui(StringBasedItemSinkModuleGuiSlot.class).setNbt(nbt);
    }

    @Override
    protected ModuleInHandGuiProvider getInHandGuiProvider() {
        return NewGuiHandler.getGui(StringBasedItemSinkModuleGuiInHand.class);
    }

    @Override
    public IHUDModuleRenderer getHUDRenderer() {
        return HUD;
    }

    /**
     * Only looking for items in filter
     */
    @Override
    public boolean hasGenericInterests() {
        return true;
    }

    @Override
    public List<ItemIdentifier> getSpecificInterests() {
        return null;
    }

    @Override
    public boolean interestedInAttachedInventory() {
        return false;
    }

    @Override
    public boolean interestedInUndamagedID() {
        return false;
    }

    @Override
    public boolean recievePassive() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconTexture(IIconRegister register) {
        return register.registerIcon("logisticspipes:itemModule/ModuleTypeFilterSink");
    }

    @Override
    public List<String> getStringList() {
        clientPrefixes = prefixes.stream().map(Enum::toString).collect(Collectors.toList());
        return clientPrefixes;
    }

    @Override
    public String getStringForItem(ItemIdentifier ident) {
        ItemData data = GT_OreDictUnificator.getAssociation(ident.makeNormalStackNoTag(1));
        if (data != null && data.hasValidPrefixData()) {
            return data.mPrefix.toString();
        }
        return "";
    }

    @Override
    public void listChanged() {
        prefixes.clear();
        for (String prefixName : clientPrefixes) {
            OrePrefixes prefix = OrePrefixes.getPrefix(prefixName);
            if (prefix != null) {
                prefixes.add(prefix);
            }
        }

        if (MainProxy.isServer(_world.getWorld())) {
            NBTTagCompound nbt = new NBTTagCompound();
            writeToNBT(nbt);
            MainProxy.sendToPlayerList(
                    PacketHandler.getPacket(ItemSinkListPacket.class).setNbt(nbt).setModulePos(this),
                    localModeWatchers);
        } else {
            NBTTagCompound nbt = new NBTTagCompound();
            writeToNBT(nbt);
            MainProxy.sendPacketToServer(
                    PacketHandler.getPacket(ItemSinkListPacket.class).setNbt(nbt).setModulePos(this));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        prefixes.clear();
        int limit = nbt.getInteger("size");
        for (int i = 0; i < limit; i++) {
            OrePrefixes prefix = OrePrefixes.getPrefix(nbt.getString("prefix_" + i));
            if (prefix != null) {
                prefixes.add(prefix);
            }
        }

        clientPrefixes.clear();
        for (OrePrefixes prefix : prefixes) {
            clientPrefixes.add(prefix.toString());
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        nbt.setInteger("size", prefixes.size());
        int i = 0;
        for (OrePrefixes prefix : prefixes) {
            nbt.setString("prefix_" + i, prefix.toString());
            i++;
        }
    }
}
