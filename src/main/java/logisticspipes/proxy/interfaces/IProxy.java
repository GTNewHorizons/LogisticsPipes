package logisticspipes.proxy.interfaces;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;

import logisticspipes.items.ItemLogisticsPipe;
import logisticspipes.modules.abstractmodules.LogisticsModule;
import logisticspipes.network.packets.gui.GUIPacket;
import logisticspipes.network.packets.orderer.ComponentList;
import logisticspipes.network.packets.orderer.MissingItems;
import logisticspipes.network.packets.pipe.MostLikelyRecipeComponentsResponse;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;
import logisticspipes.utils.item.ItemIdentifier;

public interface IProxy {

    String getSide();

    World getWorld();

    void registerTileEntities();

    EntityPlayer getClientPlayer();

    void addLogisticsPipesOverride(IIconRegister par1IIconRegister, int index, String override1, String override2,
            boolean flag);

    void registerParticles();

    String getName(ItemIdentifier item);

    void updateNames(ItemIdentifier item, String name);

    void tick();

    void sendNameUpdateRequest(EntityPlayer player);

    int getDimensionForWorld(World world);

    LogisticsTileGenericPipe getPipeInDimensionAt(int dimension, int x, int y, int z, EntityPlayer player);

    void sendBroadCast(String message);

    void tickServer();

    void tickClient();

    EntityPlayer getEntityPlayerFromNetHandler(INetHandler handler);

    void setIconProviderFromPipe(ItemLogisticsPipe item, CoreUnroutedPipe dummyPipe);

    LogisticsModule getModuleFromGui();

    IItemRenderer getPipeItemRenderer();

    boolean checkSinglePlayerOwner(String commandSenderName);

    void openFluidSelectGui(int slotId);

    default void processGuiPacket(GUIPacket packet, EntityPlayer player) {}

    default void clearChat() {}

    default void storeSendMessages(List<String> sendChatMessages) {}

    default void restoreSendMessages(List<String> sendChatMessages) {}

    default void addSendMessages(List<String> sendChatMessages, String substring) {}

    default void onGuiCraftingPipeCleanupModeChange() {}

    default void openChatGui() {}

    default void refreshGuiSupplierPipeMode() {}

    default void processComponentListPacket(ComponentList packet, EntityPlayer player) {}

    default void processMissingItemsPacket(MissingItems packet, EntityPlayer player) {}

    default void processMostLikelyRecipeComponentsResponse(MostLikelyRecipeComponentsResponse packet) {}

    default MovingObjectPosition getMousedOverObject() {
        return null;
    }
}
