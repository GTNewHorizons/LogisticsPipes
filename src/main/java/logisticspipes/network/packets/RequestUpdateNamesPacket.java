package logisticspipes.network.packets;

import net.minecraft.entity.player.EntityPlayer;

import cpw.mods.fml.client.FMLClientHandler;
import logisticspipes.network.LPDataInputStream;
import logisticspipes.network.LPDataOutputStream;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.proxy.SimpleServiceLocator;

public class RequestUpdateNamesPacket extends ModernPacket {

    public RequestUpdateNamesPacket(int id) {
        super(id);
    }

    @Override
    public void readData(LPDataInputStream data) {}

    @Override
    public void processPacket(EntityPlayer player) {
        // XXX stubbed out. How do you enumerate every item now?
        // Item[] itemList = Item.itemsList;
        // Why is this looping on empty list ?
        // Item[] itemList = new Item[0];
        // List<ItemIdentifier> identList = new LinkedList<>();
        // for (Item item : itemList) {
        // if (item != null) {
        // for (CreativeTabs tab : item.getCreativeTabs()) {
        // List<ItemStack> list = new ArrayList<>();
        // item.getSubItems(item, tab, list);
        // if (list.size() > 0) {
        // for (ItemStack stack : list) {
        // identList.add(ItemIdentifier.get(stack));
        // }
        // } else {
        // identList.add(ItemIdentifier.get(item, 0, null));
        // }
        // }
        // }
        // }
        SimpleServiceLocator.clientBufferHandler.setPause(true);
        // for (ItemIdentifier item : identList) {
        // MainProxy.sendPacketToServer(
        // PacketHandler.getPacket(UpdateName.class).setIdent(item).setName(item.getFriendlyName()));
        // }
        SimpleServiceLocator.clientBufferHandler.setPause(false);
        FMLClientHandler.instance().getClient().thePlayer.sendChatMessage("Names in send Queue");
    }

    @Override
    public void writeData(LPDataOutputStream data) {}

    @Override
    public ModernPacket template() {
        return new RequestUpdateNamesPacket(getId());
    }
}
