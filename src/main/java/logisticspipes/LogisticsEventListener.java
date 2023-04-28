package logisticspipes;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.Action;
import net.minecraftforge.event.world.ChunkWatchEvent.UnWatch;
import net.minecraftforge.event.world.ChunkWatchEvent.Watch;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.config.PlayerConfig;
import logisticspipes.interfaces.IItemAdvancedExistance;
import logisticspipes.modules.ModuleQuickSort;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.PlayerConfigToClientPacket;
import logisticspipes.network.packets.chassis.ChestGuiClosed;
import logisticspipes.network.packets.chassis.ChestGuiOpened;
import logisticspipes.network.packets.gui.GuiReopenPacket;
import logisticspipes.pipes.PipeLogisticsChassi;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.renderer.LogisticsGuiOverrenderer;
import logisticspipes.renderer.LogisticsHUDRenderer;
import logisticspipes.utils.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

public class LogisticsEventListener {

    public static final WeakHashMap<EntityPlayer, List<WeakReference<ModuleQuickSort>>> chestQuickSortConnection = new WeakHashMap<>();
    public static Map<ChunkCoordIntPair, PlayerCollectionList> watcherList = new ConcurrentHashMap<>();
    int taskCount = 0;
    public static Map<PlayerIdentifier, PlayerConfig> playerConfigs = new HashMap<>();

    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event != null && event.entity instanceof EntityItem
                && event.entity.worldObj != null
                && !event.entity.worldObj.isRemote) {
            ItemStack stack = ((EntityItem) event.entity).getEntityItem(); // Get ItemStack
            if (stack != null && stack.getItem() instanceof IItemAdvancedExistance
                    && !((IItemAdvancedExistance) stack.getItem()).canExistInWorld(stack)) {
                event.setCanceled(true);
            }
        }
    }

    /*
     * subscribe forge pre stich event to register common texture
     */
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void textureHook(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 1) {
            LogisticsPipes.textures.registerItemIcons(event.map);
        }
        if (event.map.getTextureType() == 0) {
            LogisticsPipes.textures.registerBlockIcons(event.map);
        }
    }

    @SubscribeEvent
    public void onPlayerInteract(final PlayerInteractEvent event) {
        if (MainProxy.isServer(event.entityPlayer.worldObj)) {
            if (event.action == Action.LEFT_CLICK_BLOCK) {
                final TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
                if (tile instanceof LogisticsTileGenericPipe) {
                    if (((LogisticsTileGenericPipe) tile).pipe instanceof CoreRoutedPipe) {
                        if (!((CoreRoutedPipe) ((LogisticsTileGenericPipe) tile).pipe)
                                .canBeDestroyedByPlayer(event.entityPlayer)) {
                            event.setCanceled(true);
                            event.entityPlayer
                                    .addChatComponentMessage(new ChatComponentTranslation("lp.chat.permissiondenied"));
                            ((LogisticsTileGenericPipe) tile).scheduleNeighborChange();
                            event.entityPlayer.worldObj.markBlockForUpdate(tile.xCoord, tile.yCoord, tile.zCoord);
                            ((CoreRoutedPipe) ((LogisticsTileGenericPipe) tile).pipe).delayTo = System
                                    .currentTimeMillis() + 200;
                            ((CoreRoutedPipe) ((LogisticsTileGenericPipe) tile).pipe).repeatFor = 10;
                        } else {
                            ((CoreRoutedPipe) ((LogisticsTileGenericPipe) tile).pipe).setDestroyByPlayer();
                        }
                    }
                }
            }
            if (event.action == Action.RIGHT_CLICK_BLOCK) {
                final TileEntity tile = event.entityPlayer.worldObj.getTileEntity(event.x, event.y, event.z);
                if (tile instanceof TileEntityChest || SimpleServiceLocator.ironChestProxy.isIronChest(tile)) {
                    List<WeakReference<ModuleQuickSort>> list = new ArrayList<>();
                    for (AdjacentTile adj : new WorldUtil(tile).getAdjacentTileEntities()) {
                        if (adj.tile instanceof LogisticsTileGenericPipe) {
                            if (((LogisticsTileGenericPipe) adj.tile).pipe instanceof PipeLogisticsChassi) {
                                if (((PipeLogisticsChassi) ((LogisticsTileGenericPipe) adj.tile).pipe)
                                        .getPointedOrientation() == adj.orientation.getOpposite()) {
                                    PipeLogisticsChassi chassi = (PipeLogisticsChassi) ((LogisticsTileGenericPipe) adj.tile).pipe;
                                    for (int i = 0; i < chassi.getChassiSize(); i++) {
                                        if (chassi.getLogisticsModule().getSubModule(i) instanceof ModuleQuickSort) {
                                            list.add(
                                                    new WeakReference<>(
                                                            (ModuleQuickSort) chassi.getLogisticsModule()
                                                                    .getSubModule(i)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (!list.isEmpty()) {
                        LogisticsEventListener.chestQuickSortConnection.put(event.entityPlayer, list);
                    }
                }
            }
        }
    }

    public static HashMap<Integer, Long> WorldLoadTime = new HashMap<>();

    @SubscribeEvent
    public void WorldLoad(WorldEvent.Load event) {
        if (MainProxy.isServer(event.world)) {
            int dim = MainProxy.getDimensionForWorld(event.world);
            if (!LogisticsEventListener.WorldLoadTime.containsKey(dim)) {
                LogisticsEventListener.WorldLoadTime.put(dim, System.currentTimeMillis());
            }
        }
        if (MainProxy.isClient(event.world)) {
            SimpleServiceLocator.routerManager.clearClientRouters();
            LogisticsHUDRenderer.instance().clear();
        }
    }

    @SubscribeEvent
    public void WorldUnload(WorldEvent.Unload event) {
        if (MainProxy.isServer(event.world)) {
            int dim = MainProxy.getDimensionForWorld(event.world);
            SimpleServiceLocator.routerManager.dimensionUnloaded(dim);
        }
    }

    @SubscribeEvent
    public void watchChunk(Watch event) {
        if (!LogisticsEventListener.watcherList.containsKey(event.chunk)) {
            LogisticsEventListener.watcherList.put(event.chunk, new PlayerCollectionList());
        }
        LogisticsEventListener.watcherList.get(event.chunk).add(event.player);
    }

    @SubscribeEvent
    public void unWatchChunk(UnWatch event) {
        if (LogisticsEventListener.watcherList.containsKey(event.chunk)) {
            LogisticsEventListener.watcherList.get(event.chunk).remove(event.player);
        }
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event) {
        if (MainProxy.isServer(event.player.worldObj)) {
            SimpleServiceLocator.securityStationManager.sendClientAuthorizationList(event.player);
            SimpleServiceLocator.craftingPermissionManager.sendCraftingPermissionsToPlayer(event.player);
        }

        SimpleServiceLocator.serverBufferHandler.clear(event.player);
        PlayerConfig config = LogisticsEventListener.getPlayerConfig(PlayerIdentifier.get(event.player));
        MainProxy.sendPacketToPlayer(
                PacketHandler.getPacket(PlayerConfigToClientPacket.class).setConfig(config),
                event.player);
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event) {
        SimpleServiceLocator.serverBufferHandler.clear(event.player);
        PlayerIdentifier ident = PlayerIdentifier.get(event.player);
        PlayerConfig config = LogisticsEventListener.playerConfigs.get(ident);
        if (config != null) {
            config.writeToFile();
        }
        LogisticsEventListener.playerConfigs.remove(ident);
    }

    @AllArgsConstructor
    private static class GuiEntry {

        @Getter
        private final int xCoord;

        @Getter
        private final int yCoord;

        @Getter
        private final int zCoord;

        @Getter
        private final int guiID;

        @Getter
        @Setter
        private boolean isActive;
    }

    @Getter(lazy = true)
    private static final Queue<GuiEntry> guiPos = new LinkedList<>();

    // Handle GuiRepoen
    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onGuiOpen(GuiOpenEvent event) {
        if (!LogisticsEventListener.getGuiPos().isEmpty()) {
            GuiEntry part = LogisticsEventListener.getGuiPos().peek();
            if (event.gui == null) {
                if (part.isActive()) {
                    part = LogisticsEventListener.getGuiPos().poll();
                    MainProxy.sendPacketToServer(
                            PacketHandler.getPacket(GuiReopenPacket.class).setGuiID(part.getGuiID())
                                    .setPosX(part.getXCoord()).setPosY(part.getYCoord()).setPosZ(part.getZCoord()));
                    LogisticsGuiOverrenderer.getInstance().setOverlaySlotActive(false);
                }
            } else {
                part.setActive(true);
            }
        }
        if (event.gui == null) {
            LogisticsGuiOverrenderer.getInstance().setOverlaySlotActive(false);
        }
        if (event.gui instanceof GuiChest || (SimpleServiceLocator.ironChestProxy != null
                && SimpleServiceLocator.ironChestProxy.isChestGui(event.gui))) {
            MainProxy.sendPacketToServer(PacketHandler.getPacket(ChestGuiOpened.class));
        } else {
            QuickSortChestMarkerStorage.getInstance().disable();
            MainProxy.sendPacketToServer(PacketHandler.getPacket(ChestGuiClosed.class));
        }
    }

    @SideOnly(Side.CLIENT)
    public static void addGuiToReopen(int xCoord, int yCoord, int zCoord, int guiID) {
        LogisticsEventListener.getGuiPos().add(new GuiEntry(xCoord, yCoord, zCoord, guiID, false));
    }

    @SubscribeEvent
    public void clientLoggedIn(ClientConnectedToServerEvent event) {
        SimpleServiceLocator.clientBufferHandler.clear();
    }

    public static void serverShutdown() {
        for (PlayerConfig config : LogisticsEventListener.playerConfigs.values()) {
            config.writeToFile();
        }
        LogisticsEventListener.playerConfigs.clear();
    }

    public static PlayerConfig getPlayerConfig(PlayerIdentifier ident) {
        PlayerConfig config = LogisticsEventListener.playerConfigs.get(ident);
        if (config == null) {
            config = new PlayerConfig(ident);
            config.readFromFile();
            LogisticsEventListener.playerConfigs.put(ident, config);
        }
        return config;
    }
}
