package logisticspipes.utils.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class IItemTextureRenderSlot implements IRenderSlot {

    @SideOnly(Side.CLIENT)
    public abstract IIcon getTextureIcon();

    public abstract boolean drawSlotIcon();

    public abstract boolean customRender(Minecraft mc, float zLevel);

    @Override
    public int getSize() {
        return 18;
    }
}
