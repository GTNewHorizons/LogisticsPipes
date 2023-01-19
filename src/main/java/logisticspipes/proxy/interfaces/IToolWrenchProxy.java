package logisticspipes.proxy.interfaces;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;

public interface IToolWrenchProxy {

    boolean isWrenchEquipped(EntityPlayer entityplayer);

    boolean canWrench(EntityPlayer entityplayer, int x, int y, int z);

    void wrenchUsed(EntityPlayer entityplayer, int x, int y, int z);

    boolean isWrench(Item item);
}
