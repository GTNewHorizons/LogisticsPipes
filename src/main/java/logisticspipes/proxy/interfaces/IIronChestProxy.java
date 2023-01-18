package logisticspipes.proxy.interfaces;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.tileentity.TileEntity;

public interface IIronChestProxy {

    boolean isIronChest(TileEntity tile);

    @SideOnly(Side.CLIENT) boolean isChestGui(GuiScreen gui);
}
