/*
 * Copyright (c) 2015 RS485 "LogisticsPipes" is distributed under the terms of the Minecraft Mod Public License 1.0, or
 * MMPL. Please check the contents of the license located in
 * https://github.com/RS485/LogisticsPipes/blob/mc16/LICENSE.md
 */

package logisticspipes.utils.item;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;

import org.lwjgl.opengl.GL11;

import logisticspipes.utils.Color;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.IItemSearch;
import logisticspipes.utils.gui.SimpleGraphics;
import logisticspipes.utils.string.StringUtils;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ItemStackRenderer {

    private RenderManager renderManager;
    private RenderBlocks renderBlocks;
    private RenderItem renderItem;
    private TextureManager texManager;
    private FontRenderer fontRenderer;

    private ItemIdentifierStack itemIdentifierStack;
    private int posX;
    private int posY;
    private float zLevel;
    private float scaleX;
    private float scaleY;
    private float scaleZ;
    private DisplayAmount displayAmount;
    private boolean renderEffects;
    private boolean ignoreDepth;
    private boolean renderInColor;
    private World worldObj;
    private float partialTickTime;

    public ItemStackRenderer(int posX, int posY, float zLevel, boolean renderEffects, boolean ignoreDepth,
            boolean renderInColor) {
        this.posX = posX;
        this.posY = posY;
        this.zLevel = zLevel;
        this.renderEffects = renderEffects;
        this.ignoreDepth = ignoreDepth;
        this.renderInColor = renderInColor;
        renderManager = RenderManager.instance;
        fontRenderer = renderManager.getFontRenderer();
        if (fontRenderer == null) {
            fontRenderer = Minecraft.getMinecraft().fontRenderer;
        }
        worldObj = renderManager.worldObj;
        texManager = renderManager.renderEngine;
        if (texManager == null) {
            texManager = Minecraft.getMinecraft().getTextureManager();
        }
        renderBlocks = RenderBlocks.getInstance();
        renderItem = RenderItem.getInstance();
        scaleX = 1.0F;
        scaleY = 1.0F;
        scaleZ = 1.0F;
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems,
            IItemSearch IItemSearch, int page, int left, int top, int columns, int items, int xSize, int ySize,
            float zLevel, DisplayAmount displayAmount) {
        ItemStackRenderer.renderItemIdentifierStackListIntoGui(
                _allItems,
                IItemSearch,
                page,
                left,
                top,
                columns,
                items,
                xSize,
                ySize,
                zLevel,
                displayAmount,
                true,
                true,
                false);
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems,
            IItemSearch IItemSearch, int page, int left, int top, int columns, int items, int xSize, int ySize,
            float zLevel, DisplayAmount displayAmount, boolean renderInColor, boolean renderEffect,
            boolean ignoreDepth) {
        ItemStackRenderer itemStackRenderer = new ItemStackRenderer(
                0,
                0,
                zLevel,
                renderEffect,
                ignoreDepth,
                renderInColor);
        itemStackRenderer.setDisplayAmount(displayAmount);
        ItemStackRenderer.renderItemIdentifierStackListIntoGui(
                _allItems,
                IItemSearch,
                page,
                left,
                top,
                columns,
                items,
                xSize,
                ySize,
                itemStackRenderer);
    }

    public static void renderItemIdentifierStackListIntoGui(List<ItemIdentifierStack> _allItems,
            IItemSearch IItemSearch, int page, int left, int top, int columns, int items, int xSize, int ySize,
            ItemStackRenderer itemStackRenderer) {
        int ppi = 0;
        int column = 0;
        int row = 0;

        for (ItemIdentifierStack itemIdentifierStack : _allItems) {
            if (itemIdentifierStack == null) {
                column++;
                if (column >= columns) {
                    row++;
                    column = 0;
                }
                ppi++;
                continue;
            }
            ItemIdentifier item = itemIdentifierStack.getItem();
            if (IItemSearch != null && !IItemSearch.itemSearched(item)) {
                continue;
            }
            ppi++;

            if (ppi <= items * page) {
                continue;
            }
            if (ppi > items * (page + 1)) {
                continue;
            }

            int x = left + xSize * column;
            int y = top + ySize * row + 1;

            itemStackRenderer.setItemIdentifierStack(itemIdentifierStack).setPosX(x).setPosY(y);
            itemStackRenderer.renderInGui();

            column++;
            if (column >= columns) {
                row++;
                column = 0;
            }
        }
    }

    public void renderInGui() {
        assert itemIdentifierStack != null;
        assert displayAmount != null;
        assert renderBlocks != null;
        assert renderItem != null;
        assert texManager != null;
        assert fontRenderer != null;
        assert scaleX != 0.0F;
        assert scaleY != 0.0F;
        assert scaleZ != 0.0F;

        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);

        // The only thing that ever sets NORMALIZE are slimes. It never gets disabled and it interferes with our
        // lightning in the HUD.
        GL11.glDisable(GL11.GL_NORMALIZE);

        // set up lightning
        GL11.glScalef(1.0F / scaleX, 1.0F / scaleY, 1.0F / scaleZ);
        RenderHelper.enableGUIStandardItemLighting();
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
        GL11.glScalef(scaleX, scaleY, scaleZ);

        if (ignoreDepth) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }

        ItemStack itemStack = itemIdentifierStack.makeNormalStack();
        if (!ForgeHooksClient
                .renderInventoryItem(renderBlocks, texManager, itemStack, renderInColor, zLevel, posX, posY)) {
            renderItem.zLevel += zLevel;
            renderItem.renderItemIntoGUI(fontRenderer, texManager, itemStack, posX, posY, renderEffects);
            renderItem.zLevel -= zLevel;
        }

        // disable lightning
        RenderHelper.disableStandardItemLighting();

        if (ignoreDepth) {
            GL11.glDisable(GL11.GL_DEPTH_TEST);
        } else {
            GL11.glEnable(GL11.GL_DEPTH_TEST);
        }
        // 20 should be about the size of a block
        GuiGraphics.drawDurabilityBar(itemStack, posX, posY, zLevel + 20.0F);

        // if we want to render the amount, do that
        if (displayAmount != DisplayAmount.NEVER) {
            if (ignoreDepth) {
                GL11.glDisable(GL11.GL_DEPTH_TEST);
            } else {
                GL11.glEnable(GL11.GL_DEPTH_TEST);
            }

            FontRenderer specialFontRenderer = itemIdentifierStack.getItem().item.getFontRenderer(itemStack);

            if (specialFontRenderer != null) {
                fontRenderer = specialFontRenderer;
            }

            GL11.glDisable(GL11.GL_LIGHTING);
            String amountString = StringUtils
                    .getFormatedStackSize(itemIdentifierStack.getStackSize(), displayAmount == DisplayAmount.ALWAYS);

            // 20 should be about the size of a block + 20 for the effect and overlay
            GL11.glTranslatef(0.0F, 0.0F, zLevel + 40.0F);

            // using a translated shadow does not hurt and works with the HUD
            SimpleGraphics.drawStringWithTranslatedShadow(
                    fontRenderer,
                    amountString,
                    posX + 17 - fontRenderer.getStringWidth(amountString),
                    posY + 9,
                    Color.getValue(Color.WHITE));

            GL11.glTranslatef(0.0F, 0.0F, -(zLevel + 40.0F));
        }

        GL11.glPopAttrib();
    }

    public enum DisplayAmount {
        HIDE_ONE,
        ALWAYS,
        NEVER,
    }
}
