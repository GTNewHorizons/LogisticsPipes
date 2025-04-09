package logisticspipes.renderer;

import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import logisticspipes.items.ItemModule;
import logisticspipes.modules.ModuleCrafter;
import logisticspipes.utils.item.EzNBT;

public class ItemModuleRenderer implements IItemRenderer {

    private final RenderItem ri = new RenderItem();

    @Override
    public boolean handleRenderType(final ItemStack itemStack, final ItemRenderType rendererType) {
        final boolean isShiftPressed = Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)
                || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT);

        if (isShiftPressed && rendererType == IItemRenderer.ItemRenderType.INVENTORY
                && ItemModule.isCrafter(itemStack)) {
            return this.stackToModule(itemStack).map(m -> m.getConfiguredCraftResult()).isPresent();
        } else {
            return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(final ItemRenderType type, final ItemStack item,
            final ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(final ItemRenderType renderType, final ItemStack itemStack, final Object... data) {
        final ItemStack is = stackToModule(itemStack).get().getConfiguredCraftResult().makeNormalStack();
        final Minecraft mc = Minecraft.getMinecraft();

        GL11.glPushAttrib(GL11.GL_ENABLE_BIT | GL11.GL_COLOR_BUFFER_BIT | GL11.GL_LIGHTING_BIT);
        RenderHelper.enableGUIStandardItemLighting();
        this.ri.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), is, 0, 0);
        RenderHelper.disableStandardItemLighting();
        GL11.glPopAttrib();
    }

    protected Optional<ModuleCrafter> stackToModule(final ItemStack itemStack) {
        return new EzNBT(itemStack).get("moduleInformation").asCompound().map(modInfoTag -> {
            final ModuleCrafter module = new ModuleCrafter();
            module.readFromNBT(modInfoTag);
            return module;
        });
    }
}
