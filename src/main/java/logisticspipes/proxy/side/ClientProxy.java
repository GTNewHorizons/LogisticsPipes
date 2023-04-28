package logisticspipes.proxy.side;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.DimensionManager;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.GameRegistry;
import logisticspipes.LPConstants;
import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsSecurityTileEntity;
import logisticspipes.blocks.LogisticsSolderingTileEntity;
import logisticspipes.blocks.crafting.LogisticsCraftingTableTileEntity;
import logisticspipes.blocks.powertile.LogisticsIC2PowerProviderTileEntity;
import logisticspipes.blocks.powertile.LogisticsPowerJunctionTileEntity;
import logisticspipes.blocks.powertile.LogisticsRFPowerProviderTileEntity;
import logisticspipes.blocks.stats.LogisticsStatisticsTileEntity;
import logisticspipes.config.Configs;
import logisticspipes.gui.GuiCraftingPipe;
import logisticspipes.gui.GuiLogisticsCraftingTable;
import logisticspipes.gui.GuiSupplierPipe;
import logisticspipes.gui.modules.ModuleBaseGui;
import logisticspipes.gui.orderer.GuiOrderer;
import logisticspipes.gui.orderer.GuiRequestTable;
import logisticspipes.gui.popup.GuiRecipeImport;
import logisticspipes.gui.popup.SelectItemOutOfList;
import logisticspipes.items.ItemLogisticsPipe;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.network.NewGuiHandler;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.gui.DummyContainerSlotClick;
import logisticspipes.network.packets.gui.GUIPacket;
import logisticspipes.network.packets.orderer.ComponentList;
import logisticspipes.network.packets.orderer.MissingItems;
import logisticspipes.network.packets.pipe.MostLikelyRecipeComponentsResponse;
import logisticspipes.pipefxhandlers.Particles;
import logisticspipes.pipefxhandlers.PipeFXRenderHandler;
import logisticspipes.pipefxhandlers.providers.*;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.interfaces.IProxy;
import logisticspipes.renderer.LogisticsPipeItemRenderer;
import logisticspipes.renderer.LogisticsPipeWorldRenderer;
import logisticspipes.renderer.LogisticsRenderPipe;
import logisticspipes.renderer.LogisticsSolidBlockWorldRenderer;
import logisticspipes.renderer.newpipe.GLRenderListHandler;
import logisticspipes.request.resources.IResource;
import logisticspipes.textures.Textures;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.gui.LogisticsBaseGuiScreen;
import logisticspipes.utils.gui.SubGuiScreen;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;
import logisticspipes.utils.string.ChatColor;

public class ClientProxy implements IProxy {

    private IItemRenderer pipeRenderer;

    @Override
    public String getSide() {
        return "Client";
    }

    @Override
    public World getWorld() {
        return FMLClientHandler.instance().getClient().theWorld;
    }

    @Override
    public void registerTileEntities() {
        GameRegistry.registerTileEntity(
                LogisticsSolderingTileEntity.class,
                "logisticspipes.blocks.LogisticsSolderingTileEntity");
        GameRegistry.registerTileEntity(
                LogisticsPowerJunctionTileEntity.class,
                "logisticspipes.blocks.powertile.LogisticsPowerJuntionTileEntity");
        GameRegistry.registerTileEntity(
                LogisticsRFPowerProviderTileEntity.class,
                "logisticspipes.blocks.powertile.LogisticsRFPowerProviderTileEntity");
        GameRegistry.registerTileEntity(
                LogisticsIC2PowerProviderTileEntity.class,
                "logisticspipes.blocks.powertile.LogisticsIC2PowerProviderTileEntity");
        GameRegistry.registerTileEntity(
                LogisticsSecurityTileEntity.class,
                "logisticspipes.blocks.LogisticsSecurityTileEntity");
        GameRegistry.registerTileEntity(
                LogisticsCraftingTableTileEntity.class,
                "logisticspipes.blocks.crafting.LogisticsCraftingTableTileEntity");
        GameRegistry.registerTileEntity(LogisticsTileGenericPipe.class, LogisticsPipes.logisticsTileGenericPipeMapping);
        GameRegistry.registerTileEntity(
                LogisticsStatisticsTileEntity.class,
                "logisticspipes.blocks.stats.LogisticsStatisticsTileEntity");

        LPConstants.pipeModel = RenderingRegistry.getNextAvailableRenderId();
        LPConstants.solidBlockModel = RenderingRegistry.getNextAvailableRenderId();

        LogisticsRenderPipe lrp = new LogisticsRenderPipe();
        ClientRegistry.bindTileEntitySpecialRenderer(LogisticsTileGenericPipe.class, lrp);

        RenderingRegistry.registerBlockHandler(new LogisticsPipeWorldRenderer());

        RenderingRegistry.registerBlockHandler(new LogisticsSolidBlockWorldRenderer());

        SimpleServiceLocator.buildCraftProxy.resetItemRotation();

        SimpleServiceLocator.setRenderListHandler(new GLRenderListHandler());
    }

    @Override
    public EntityPlayer getClientPlayer() {
        return FMLClientHandler.instance().getClient().thePlayer;
    }

    @Override
    public void registerParticles() {
        PipeFXRenderHandler.registerParticleHandler(Particles.WhiteParticle, new EntityWhiteSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.RedParticle, new EntityRedSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.BlueParticle, new EntityBlueSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.GreenParticle, new EntityGreenSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.GoldParticle, new EntityGoldSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.VioletParticle, new EntityVioletSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.OrangeParticle, new EntityOrangeSparkleFXProvider());
        PipeFXRenderHandler
                .registerParticleHandler(Particles.LightGreenParticle, new EntityLightGreenSparkleFXProvider());
        PipeFXRenderHandler.registerParticleHandler(Particles.LightRedParticle, new EntityLightRedSparkleFXProvider());
    }

    @Override
    public String getName(ItemIdentifier item) {
        return item.getFriendlyName();
    }

    @Override
    public void updateNames(ItemIdentifier item, String name) {
        // Not Client Side
    }

    @Override
    public void tick() {
        // Not Client Side
    }

    @Override
    public void sendNameUpdateRequest(EntityPlayer player) {
        // Not Client Side
    }

    @Override
    public int getDimensionForWorld(World world) {
        if (world instanceof WorldServer) {
            return world.provider.dimensionId;
        }
        if (world instanceof WorldClient) {
            return world.provider.dimensionId;
        }
        return world.getWorldInfo().getVanillaDimension();
    }

    @Override
    public LogisticsTileGenericPipe getPipeInDimensionAt(int dimension, int x, int y, int z, EntityPlayer player) {
        return ClientProxy.getPipe(DimensionManager.getWorld(dimension), x, y, z);
    }

    // BuildCraft method

    /**
     * Retrieves pipe at specified coordinates if any.
     */
    private static LogisticsTileGenericPipe getPipe(World world, int x, int y, int z) {
        if (world == null || !world.blockExists(x, y, z)) {
            return null;
        }

        final TileEntity tile = world.getTileEntity(x, y, z);
        if (!(tile instanceof LogisticsTileGenericPipe)) {
            return null;
        }

        return (LogisticsTileGenericPipe) tile;
    }

    // BuildCraft method end

    @Override
    public void addLogisticsPipesOverride(IIconRegister par1IIconRegister, int index, String override1,
            String override2, boolean flag) {
        if (par1IIconRegister != null) {
            if ("NewPipeTexture".equals(override2) && !override1.contains("status_overlay")) {
                Textures.LPnewPipeIconProvider.setIcon(
                        index,
                        par1IIconRegister
                                .registerIcon("logisticspipes:" + override1.replace("pipes/", "pipes/new_texture/")));
            } else if (flag) {
                Textures.LPpipeIconProvider
                        .setIcon(index, par1IIconRegister.registerIcon("logisticspipes:" + override1));
            } else {
                Textures.LPpipeIconProvider.setIcon(
                        index,
                        par1IIconRegister.registerIcon(
                                "logisticspipes:" + override1.replace("pipes/", "pipes/overlay_gen/")
                                        + "/"
                                        + override2.replace("pipes/status_overlay/", "")));
            }
        }
    }

    @Override
    public void sendBroadCast(String message) {
        if (Minecraft.getMinecraft().thePlayer != null) {
            Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("[LP] Client: " + message));
        }
    }

    @Override
    public void tickServer() {}

    @Override
    public void tickClient() {
        MainProxy.addTick();
        SimpleServiceLocator.renderListHandler.tick();
    }

    @Override
    public EntityPlayer getEntityPlayerFromNetHandler(INetHandler handler) {
        if (handler instanceof NetHandlerPlayServer) {
            return ((NetHandlerPlayServer) handler).playerEntity;
        } else {
            return Minecraft.getMinecraft().thePlayer;
        }
    }

    @Override
    public void setIconProviderFromPipe(ItemLogisticsPipe item, CoreUnroutedPipe dummyPipe) {
        item.setPipesIcons(dummyPipe.getIconProvider());
    }

    @Override
    public LogisticsModule getModuleFromGui() {
        if (FMLClientHandler.instance().getClient().currentScreen instanceof ModuleBaseGui) {
            return ((ModuleBaseGui) FMLClientHandler.instance().getClient().currentScreen).getModule();
        }
        if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCraftingPipe) {
            return ((GuiCraftingPipe) FMLClientHandler.instance().getClient().currentScreen).get_pipe();
        }
        return null;
    }

    @Override
    public IItemRenderer getPipeItemRenderer() {
        if (pipeRenderer == null) {
            pipeRenderer = new LogisticsPipeItemRenderer(false);
        }
        return pipeRenderer;
    }

    @Override
    public boolean checkSinglePlayerOwner(String commandSenderName) {
        return FMLCommonHandler.instance().getMinecraftServerInstance().isSinglePlayer()
                && FMLCommonHandler.instance().getMinecraftServerInstance() instanceof IntegratedServer
                && !((IntegratedServer) FMLCommonHandler.instance().getMinecraftServerInstance()).getPublic();
    }

    @Override
    public void openFluidSelectGui(final int slotId) {
        if (Minecraft.getMinecraft().currentScreen instanceof LogisticsBaseGuiScreen) {
            final List<ItemIdentifierStack> list = new ArrayList<>();
            for (FluidIdentifier fluid : FluidIdentifier.all()) {
                if (fluid == null) {
                    continue;
                }
                list.add(fluid.getItemIdentifier().makeStack(1));
            }
            SelectItemOutOfList subGui = new SelectItemOutOfList(
                    list,
                    slot -> MainProxy.sendPacketToServer(
                            PacketHandler.getPacket(DummyContainerSlotClick.class).setSlotId(slotId)
                                    .setStack(list.get(slot).makeNormalStack()).setButton(0)));
            LogisticsBaseGuiScreen gui = (LogisticsBaseGuiScreen) Minecraft.getMinecraft().currentScreen;
            if (!gui.hasSubGui()) {
                gui.setSubGui(subGui);
            } else {
                SubGuiScreen nextGui = gui.getSubGui();
                while (nextGui.hasSubGui()) {
                    nextGui = nextGui.getSubGui();
                }
                nextGui.setSubGui(subGui);
            }
        } else {
            throw new UnsupportedOperationException(String.valueOf(Minecraft.getMinecraft().currentScreen));
        }
    }

    @Override
    public void processGuiPacket(GUIPacket packet, EntityPlayer player) {
        NewGuiHandler.openGui(packet, player);
    }

    @Override
    public void clearChat() {
        FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().clearChatMessages();
    }

    @SuppressWarnings("unchecked")
    @Override
    public void storeSendMessages(List<String> sendChatMessages) {
        sendChatMessages.clear();
        sendChatMessages.addAll(FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().getSentMessages());
    }

    @Override
    public void restoreSendMessages(List<String> sendChatMessages) {
        if (sendChatMessages != null) {
            for (String o : sendChatMessages) {
                FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().addToSentMessages(o);
            }
            sendChatMessages.clear();
        }
    }

    @Override
    public void addSendMessages(List<String> sendChatMessages, String substring) {
        if (sendChatMessages != null) {
            sendChatMessages.add(substring);
        } else {
            FMLClientHandler.instance().getClient().ingameGUI.getChatGUI().addToSentMessages(substring);
        }
    }

    @Override
    public void onGuiCraftingPipeCleanupModeChange() {
        if (Minecraft.getMinecraft().currentScreen instanceof GuiCraftingPipe) {
            ((GuiCraftingPipe) Minecraft.getMinecraft().currentScreen).onCleanupModeChange();
        }
    }

    @Override
    public void openChatGui() {
        FMLClientHandler.instance().getClient().displayGuiScreen(new GuiChat());
    }

    @Override
    public void refreshGuiSupplierPipeMode() {
        if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiSupplierPipe) {
            ((GuiSupplierPipe) FMLClientHandler.instance().getClient().currentScreen).refreshMode();
        }
    }

    @Override
    public void processComponentListPacket(ComponentList packet, EntityPlayer player) {
        if (Configs.DISPLAY_POPUP && FMLClientHandler.instance().getClient().currentScreen instanceof GuiOrderer) {
            ((GuiOrderer) FMLClientHandler.instance().getClient().currentScreen).handleSimulateAnswer(
                    packet.getUsed(),
                    packet.getMissing(),
                    (GuiOrderer) FMLClientHandler.instance().getClient().currentScreen,
                    player);
        } else if (Configs.DISPLAY_POPUP
                && FMLClientHandler.instance().getClient().currentScreen instanceof GuiRequestTable) {
                    ((GuiRequestTable) FMLClientHandler.instance().getClient().currentScreen).handleSimulateAnswer(
                            packet.getUsed(),
                            packet.getMissing(),
                            (GuiRequestTable) FMLClientHandler.instance().getClient().currentScreen,
                            player);
                } else {
                    for (IResource item : packet.getUsed()) {
                        player.addChatComponentMessage(
                                new ChatComponentText(
                                        "Component: " + item.getDisplayText(IResource.ColorCode.SUCCESS)));
                    }
                    for (IResource item : packet.getMissing()) {
                        player.addChatComponentMessage(
                                new ChatComponentText("Missing: " + item.getDisplayText(IResource.ColorCode.MISSING)));
                    }
                }
    }

    @Override
    public void processMissingItemsPacket(MissingItems packet, EntityPlayer player) {
        if (Configs.DISPLAY_POPUP && FMLClientHandler.instance().getClient().currentScreen instanceof GuiOrderer) {
            ((GuiOrderer) FMLClientHandler.instance().getClient().currentScreen).handleRequestAnswer(
                    packet.getItems(),
                    packet.isFlag(),
                    (GuiOrderer) FMLClientHandler.instance().getClient().currentScreen,
                    player);
        } else if (Configs.DISPLAY_POPUP
                && FMLClientHandler.instance().getClient().currentScreen instanceof GuiRequestTable) {
                    ((GuiRequestTable) FMLClientHandler.instance().getClient().currentScreen).handleRequestAnswer(
                            packet.getItems(),
                            packet.isFlag(),
                            (GuiRequestTable) FMLClientHandler.instance().getClient().currentScreen,
                            player);
                } else
            if (packet.isFlag()) {
                for (IResource item : packet.getItems()) {
                    player.addChatComponentMessage(
                            new ChatComponentText(
                                    ChatColor.RED + "Missing: " + item.getDisplayText(IResource.ColorCode.MISSING)));
                }
            } else {
                for (IResource item : packet.getItems()) {
                    player.addChatComponentMessage(
                            new ChatComponentText(
                                    ChatColor.GREEN + "Requested: "
                                            + item.getDisplayText(IResource.ColorCode.SUCCESS)));
                }
                player.addChatComponentMessage(new ChatComponentText(ChatColor.GREEN + "Request successful!"));
            }
    }

    @Override
    public void processMostLikelyRecipeComponentsResponse(MostLikelyRecipeComponentsResponse packet) {
        GuiScreen firstGui = Minecraft.getMinecraft().currentScreen;
        LogisticsBaseGuiScreen gui;
        if (firstGui instanceof GuiLogisticsCraftingTable) {
            gui = (GuiLogisticsCraftingTable) firstGui;
        } else if (firstGui instanceof GuiRequestTable) {
            gui = (GuiRequestTable) firstGui;
        } else {
            return;
        }
        GuiRecipeImport importGui = null;
        SubGuiScreen sub = gui.getSubGui();
        while (sub != null) {
            if (sub instanceof GuiRecipeImport) {
                importGui = (GuiRecipeImport) sub;
                break;
            }
            sub = sub.getSubGui();
        }
        if (importGui == null) return;
        importGui.handleProposePacket(packet.getResponse());
    }

    @Override
    public MovingObjectPosition getMousedOverObject() {
        return FMLClientHandler.instance().getClient().objectMouseOver;
    }
}
