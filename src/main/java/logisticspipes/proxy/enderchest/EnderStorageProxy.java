package logisticspipes.proxy.enderchest;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import codechicken.enderstorage.common.BlockEnderStorage;
import codechicken.enderstorage.common.TileFrequencyOwner;
import logisticspipes.proxy.interfaces.IEnderStorageProxy;

public class EnderStorageProxy implements IEnderStorageProxy {

    @Override
    public boolean isEnderChestBlock(Block block) {
        return block instanceof BlockEnderStorage;
    }

    @Override
    public void openEnderChest(World world, int x, int y, int z, EntityPlayer player) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        tile.activate(player, 0);
    }
}
