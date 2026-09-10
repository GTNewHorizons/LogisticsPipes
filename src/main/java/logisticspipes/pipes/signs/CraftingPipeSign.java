package logisticspipes.pipes.signs;

import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.modules.ModuleCrafter;
import logisticspipes.modules.abstractmodules.LogisticsModule.ModulePositionType;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.abstractpackets.ModernPacket;
import logisticspipes.network.packets.cpipe.CPipeSatelliteImportBack;
import logisticspipes.pipes.PipeItemsCraftingLogistics;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.renderer.LogisticsRenderPipe;
import logisticspipes.utils.item.ItemIdentifierStack;
import lombok.Data;

public class CraftingPipeSign implements IPipeSign {

    /** Labels are laid out in 1/90 block units. */
    private static final float TEXT_SCALE = 1.0F / 90.0F;
    private static final int LINE_HEIGHT = 10;
    private static final int NAME_X_OFFSET = -15;

    private static final CraftingPipeSignData EMPTY_DATA = new CraftingPipeSignData(null, -1);

    @Data
    private static class CraftingPipeSignData implements IPipeSignData {

        private final ItemIdentifierStack item;
        private final int satID;

        @Override
        @SideOnly(Side.CLIENT)
        public boolean isListCompatible(LogisticsRenderPipe render) {
            return false;
        }
    }

    public CoreRoutedPipe pipe;
    public ForgeDirection dir;

    @Override
    public boolean isAllowedFor(CoreRoutedPipe pipe) {
        return pipe instanceof PipeItemsCraftingLogistics;
    }

    @Override
    public void addSignTo(CoreRoutedPipe pipe, ForgeDirection dir, EntityPlayer player) {
        pipe.addPipeSign(dir, new CraftingPipeSign(), player);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {}

    @Override
    public void writeToNBT(NBTTagCompound tag) {}

    @Override
    public ModernPacket getPacket() {
        PipeItemsCraftingLogistics cpipe = (PipeItemsCraftingLogistics) pipe;
        return PacketHandler.getPacket(CPipeSatelliteImportBack.class).setInventory(cpipe.getDummyInventory())
                .setType(ModulePositionType.IN_PIPE).setPosX(cpipe.getX()).setPosY(cpipe.getY()).setPosZ(cpipe.getZ());
    }

    @Override
    public void updateServerSide() {}

    @Override
    public void init(CoreRoutedPipe pipe, ForgeDirection dir) {
        this.pipe = pipe;
        this.dir = dir;
    }

    @Override
    public void activate(EntityPlayer player) {}

    @Override
    @SideOnly(Side.CLIENT)
    public void render(CoreRoutedPipe pipe, LogisticsRenderPipe renderer) {
        if (!(pipe instanceof PipeItemsCraftingLogistics)) {
            return;
        }
        final PipeItemsCraftingLogistics cpipe = (PipeItemsCraftingLogistics) pipe;
        final FontRenderer fontRenderer = renderer.func_147498_b();
        if (fontRenderer == null) {
            return;
        }

        final ItemStack stack = getDisplayedStack(cpipe);

        GL11.glPushMatrix();
        try {
            if (stack != null) {
                renderer.renderItemStackOnSign(stack);
            }

            GL11.glDepthMask(false);
            GL11.glRotatef(-180.0F, 1.0F, 0.0F, 0.0F);
            GL11.glTranslatef(0.5F, 0.08F, 0.0F);
            GL11.glScalef(TEXT_SCALE, TEXT_SCALE, TEXT_SCALE);

            final String name;
            if (stack != null) {
                name = getDisplayName(stack);

                drawCentered(fontRenderer, "ID: " + Item.getIdFromItem(stack.getItem()), 0, -2 * LINE_HEIGHT);

                final ModuleCrafter module = cpipe.getLogisticsModule();
                if (module != null && module.satelliteId != 0) {
                    drawCentered(fontRenderer, "Sat ID: " + module.satelliteId, 0, -LINE_HEIGHT);
                }
            } else {
                name = "Empty";
            }

            drawCentered(fontRenderer, renderer.cut(name, fontRenderer), NAME_X_OFFSET, LINE_HEIGHT);
        } finally {
            GL11.glDepthMask(true);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glPopMatrix();
        }
    }

    @Override
    public IPipeSignData getRenderData(CoreRoutedPipe pipe) {
        if (!(pipe instanceof PipeItemsCraftingLogistics)) {
            return EMPTY_DATA;
        }
        final PipeItemsCraftingLogistics cpipe = (PipeItemsCraftingLogistics) pipe;
        final ItemIdentifierStack craftable = getFirstCraftable(cpipe);
        if (craftable == null) {
            return EMPTY_DATA;
        }
        final ModuleCrafter module = cpipe.getLogisticsModule();
        return new CraftingPipeSignData(craftable, module == null ? -1 : module.satelliteId);
    }

    private static ItemIdentifierStack getFirstCraftable(PipeItemsCraftingLogistics cpipe) {
        final List<ItemIdentifierStack> craftables = cpipe.getConfiguredCraftResults();
        if (craftables == null || craftables.isEmpty()) {
            return null;
        }
        return craftables.get(0);
    }

    @SideOnly(Side.CLIENT)
    private static ItemStack getDisplayedStack(PipeItemsCraftingLogistics cpipe) {
        final ItemIdentifierStack craftable = getFirstCraftable(cpipe);
        if (craftable == null) {
            return null;
        }
        final ItemStack stack = craftable.unsafeMakeNormalStack();
        if (stack == null || stack.getItem() == null) {
            return null;
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    private static String getDisplayName(ItemStack stack) {
        try {
            return stack.getItem().getItemStackDisplayName(stack);
        } catch (Exception e) {
            try {
                return stack.getItem().getUnlocalizedName();
            } catch (Exception ignored) {
                return "";
            }
        }
    }

    @SideOnly(Side.CLIENT)
    private static void drawCentered(FontRenderer fontRenderer, String text, int xOffset, int y) {
        if (text == null || text.isEmpty()) {
            return;
        }
        fontRenderer.drawString(text, xOffset - fontRenderer.getStringWidth(text) / 2, y, 0);
    }
}
