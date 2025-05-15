package logisticspipes.logic.gui;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import logisticspipes.logic.LogicController;
import logisticspipes.utils.Color;
import logisticspipes.utils.gui.DummyContainer;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.LogisticsBaseGuiScreen;
import logisticspipes.utils.gui.SimpleGraphics;

public class LogicLayoutGui extends LogisticsBaseGuiScreen {

    private enum ZOOM_LEVEL {

        NORMAL(1, 165, 224, 1, 0),
        LEVEL_1(0.5F, 330, 465, 1, 50),
        LEVEL_2(0.25F, 660, 950, 2, 100);

        ZOOM_LEVEL(float zoom, int bottom, int right, int line, int moveY) {
            this.zoom = zoom;
            bottomRenderBorder = bottom;
            rightRenderBorder = right;
            this.line = line;
            this.moveY = moveY;
        }

        final float zoom;
        final int bottomRenderBorder;
        final int rightRenderBorder;
        final int line;
        final int moveY;

        ZOOM_LEVEL next() {
            int id = ordinal();
            if (id + 1 >= ZOOM_LEVEL.values().length) {
                return this;
            } else {
                return ZOOM_LEVEL.values()[id + 1];
            }
        }

        ZOOM_LEVEL prev() {
            int id = ordinal();
            if (id - 1 < 0) {
                return this;
            } else {
                return ZOOM_LEVEL.values()[id - 1];
            }
        }
    }

    private static final ResourceLocation achievementTextures = new ResourceLocation(
            "textures/gui/achievement/achievement_background.png");

    private int isMouseButtonDown;
    private int mouseX;
    private int mouseY;
    private double guiMapX;
    private double guiMapY;
    private ZOOM_LEVEL zoom = ZOOM_LEVEL.NORMAL;

    private Object[] tooltip = null;

    public LogicLayoutGui(LogicController controller, EntityPlayer player) {
        super(256, 202 + 90, 0, 0);
        guiMapY = -200;
        Mouse.getDWheel(); // Reset DWheel on GUI open
        DummyContainer dummy = new DummyContainer(player.inventory, null);
        dummy.addNormalSlotsForPlayerInventory(50, 205);
        inventorySlots = dummy;
    }

    @Override
    public void initGui() {
        super.initGui();
        /*
         * buttonList.clear(); this.buttonList.add(new GuiButton(0, this.width / 2 + 45, this.height / 2 + 74, 80, 20,
         * "Close"));
         */
    }

    @Override
    protected void actionPerformed(GuiButton button) {}

    @Override
    public void drawScreen(int par1, int par2, float par3) {
        super.drawScreen(par1, par2, par3);
        if (Mouse.isButtonDown(0)) {
            int k = (width - xSize) / 2;
            int l = (height - ySize) / 2;
            int i1 = k + 8;
            int j1 = l + 17;

            if ((isMouseButtonDown == 0 || isMouseButtonDown == 1) && par1 >= i1
                    && par1 < i1 + 224
                    && par2 >= j1
                    && par2 < j1 + 155) {
                if (isMouseButtonDown == 0) {
                    isMouseButtonDown = 1;
                } else {
                    guiMapX -= (double) (par1 - mouseX) * 1 / zoom.zoom;
                    guiMapY -= (double) (par2 - mouseY) * 1 / zoom.zoom;
                }

                mouseX = par1;
                mouseY = par2;
            }

        } else {
            isMouseButtonDown = 0;
        }

        int dWheel = Mouse.getDWheel();
        if (dWheel < 0) {
            zoom = zoom.next();
        } else if (dWheel > 0) {
            zoom = zoom.prev();
        }
        GL11.glTranslatef(0.0F, 0.0F, 100.0F);
        if (tooltip != null) {
            GuiGraphics.displayItemToolTip(tooltip, zLevel, guiLeft, guiTop, true);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        super.drawGuiContainerBackgroundLayer(f, i, j);
        drawTransparentBack();
        drawMap(i, j);
        GuiGraphics.drawGuiBackGround(
                getMC(),
                guiLeft,
                guiTop + 180,
                right,
                bottom,
                zLevel,
                true,
                false,
                true,
                true,
                true);
        GuiGraphics.drawPlayerInventoryBackground(getMC(), guiLeft + 50, guiTop + 205);
    }

    private void drawTransparentBack() {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        SimpleGraphics.drawGradientRect(0, 0, width, height, Color.BLANK, Color.BLANK, 0.0);
    }

    private void drawMap(int par1, int par2) {
        tooltip = null;
        int mapX = MathHelper.floor_double(guiMapX);
        int mapY = MathHelper.floor_double(guiMapY - zoom.moveY);
        int leftSide = ((width - xSize) / 2);
        int topSide = ((height - ySize) / 2);

        GL11.glTranslatef(0.0F, 0.0F, 100.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(LogicLayoutGui.achievementTextures);
        drawTexturedModalRect(leftSide, topSide, 0, 0, 256, 202);
        GL11.glTranslatef(0.0F, 0.0F, -100.0F);

        guiTop *= 1 / zoom.zoom;
        guiLeft *= 1 / zoom.zoom;
        xSize *= 1 / zoom.zoom;
        ySize *= 1 / zoom.zoom;
        leftSide *= 1 / zoom.zoom;
        topSide *= 1 / zoom.zoom;
        par1 *= 1 / zoom.zoom;
        par2 *= 1 / zoom.zoom;

        int innerLeftSide = leftSide + 16;
        int innerTopSide = topSide + 17;
        zLevel = 0.0F;

        GL11.glDepthFunc(GL11.GL_GEQUAL);
        GL11.glPushMatrix();
        GL11.glScalef(zoom.zoom, zoom.zoom, 1);
        GL11.glTranslatef(0.0F, 0.0F, -100.0F);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);

        int moveBackgroundX = (mapX) % 16 + (mapX < 0 ? 16 : 0);
        int moveBackgroundY = (mapY) % 16 + (mapY < 0 ? 16 : 0);
        GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);
        for (int yVar = 0; yVar * 16 - moveBackgroundY < zoom.bottomRenderBorder; yVar++) {
            for (int xVar = 0; xVar * 16 - moveBackgroundX < zoom.rightRenderBorder; xVar++) {
                IIcon icon = Blocks.stone.getIcon(0, 0);
                mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
                drawTexturedModelRectFromIcon(
                        innerLeftSide + xVar * 16 - moveBackgroundX,
                        innerTopSide + yVar * 16 - moveBackgroundY,
                        icon,
                        16,
                        16);
            }
        }

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_TEXTURE_2D);

        GL11.glPushMatrix();
        GL11.glLoadIdentity();
        // Draw Content
        // Lines

        // Draw Background

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);

        RenderHelper.enableGUIStandardItemLighting();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColor4f(0.7F, 0.7F, 0.7F, 1.0F);

        mc.getTextureManager().bindTexture(LogicLayoutGui.achievementTextures);

        // Draw Content
        // Items

        GL11.glPopMatrix();

        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        guiTop *= zoom.zoom;
        guiLeft *= zoom.zoom;
        xSize *= zoom.zoom;
        ySize *= zoom.zoom;
        leftSide *= zoom.zoom;
        topSide *= zoom.zoom;

        GL11.glScalef(1 / zoom.zoom, 1 / zoom.zoom, 1);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.getTextureManager().bindTexture(LogicLayoutGui.achievementTextures);
        drawTexturedModalRect(leftSide, topSide, 0, 0, 256, 202);

        GL11.glPopMatrix();
        zLevel = 0.0F;
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        // GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_LIGHTING);
        RenderHelper.disableStandardItemLighting();
    }

    protected void drawProgressPoint(int x, int y, int color) {
        int line = zoom.line + 1;
        Gui.drawRect(x - line + 1, y - line + 1, x + line, y + line, color);
    }
}
