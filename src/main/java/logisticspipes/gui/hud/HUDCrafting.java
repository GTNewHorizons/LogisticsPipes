package logisticspipes.gui.hud;

import java.util.List;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import logisticspipes.interfaces.IHUDConfig;
import logisticspipes.pipes.PipeItemsCraftingLogistics;
import logisticspipes.pipes.PipeItemsCraftingLogisticsMk3;
import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.item.ItemIdentifierStack;
import logisticspipes.utils.item.ItemStackRenderer;
import logisticspipes.utils.item.ItemStackRenderer.DisplayAmount;

public class HUDCrafting extends BasicHUDGui {

    private final PipeItemsCraftingLogistics pipe;

    public HUDCrafting(PipeItemsCraftingLogistics pipe) {
        this.pipe = pipe;
    }

    @Override
    public void renderHeadUpDisplay(double d, boolean day, boolean shifted, Minecraft mc, IHUDConfig config) {
        boolean hasResults = pipe.getConfiguredCraftResults() != null;
        boolean hasDisplay = !pipe.displayList.isEmpty();
        boolean hasBuffer = false;

        int bufferSize = calculateBufferSize();

        if (pipe instanceof PipeItemsCraftingLogisticsMk3 pipeMk3) {
            hasBuffer = !pipeMk3.getMk3Module().bufferList.isEmpty();
        }

        setBackgroundColor(day);
        drawBackground(mc, hasDisplay, hasBuffer, bufferSize);
        setForegroundColor(day);

        GL11.glTranslatef(0.0F, 0.0F, -0.005F);
        GL11.glScalef(1.5F, 1.5F, 0.0001F);

        drawLabels(mc, hasDisplay, hasBuffer);

        GL11.glScalef(0.8F, 0.8F, -1F);

        if (!hasResults) {
            return;
        }

        renderItems(shifted, hasDisplay, hasBuffer);
    }

    public int calculateBufferSize() {
        int size = 0;
        if (pipe instanceof PipeItemsCraftingLogisticsMk3 pipeMk3) {
            size = pipeMk3.getMk3Module().bufferList.size();
        }
        return size == 0 ? 0 : ((size - 1) / 4) + 1;
    }

    public void setBackgroundColor(boolean day) {
        if (day) {
            GL11.glColor4b((byte) 64, (byte) 64, (byte) 64, (byte) 64);
        } else {
            GL11.glColor4b((byte) 127, (byte) 127, (byte) 127, (byte) 64);
        }
    }

    public void setForegroundColor(boolean day) {
        if (day) {
            GL11.glColor4b((byte) 64, (byte) 64, (byte) 64, (byte) 127);
        } else {
            GL11.glColor4b((byte) 127, (byte) 127, (byte) 127, (byte) 127);
        }
    }

    public void drawBackground(Minecraft mc, boolean hasDisplay, boolean hasBuffer, int bufferSize) {
        if (hasDisplay && !hasBuffer) {
            GuiGraphics.drawGuiBackGround(mc, -50, -28, 50, 30, 0, false);
        } else if (hasBuffer) {
            GuiGraphics.drawGuiBackGround(mc, -50, -50, 50, bufferSize * 20 + 10, 0, false);
        } else {
            GuiGraphics.drawGuiBackGround(mc, -30, -22, 30, 25, 0, false);
        }
    }

    public void drawLabels(Minecraft mc, boolean hasDisplay, boolean hasBuffer) {
        if (hasDisplay && !hasBuffer) {
            drawLabel(mc, "Result:", -28, -10);
            drawLabel(mc, "Todo:", -28, 5);
        } else if (hasBuffer) {
            drawLabel(mc, "Result:", -28, -28);
            drawLabel(mc, "Todo:", -28, -15);
        } else {
            drawLabel(mc, "Result:", -16, -10);
        }
    }

    public void drawLabel(Minecraft mc, String text, int x, int y) {
        mc.fontRenderer.drawString(text, x, y, 0);
    }

    public void renderItems(boolean shifted, boolean hasDisplay, boolean hasBuffer) {
        if (hasDisplay && !hasBuffer) {
            renderResults(13, -17, shifted);
            renderDisplay(13, 3, shifted);
            return;
        }

        if (hasBuffer) {
            renderResults(13, -37, shifted);
            renderDisplay(13, -17, shifted);
            renderBuffer(-35, 0, shifted);
            return;
        }

        renderResults(-9, 0, shifted);
    }

    public void renderResults(int x, int y, boolean shifted) {
        var results = pipe instanceof PipeItemsCraftingLogisticsMk3 pipemk3
                ? pipemk3.getMk3Module().getConfiguredBufferCraftResults()
                : pipe.getConfiguredCraftResults();
        renderList(results, x, y, 1, 1, shifted);
    }

    public void renderDisplay(int x, int y, boolean shifted) {
        renderList(pipe.displayList, x, y, 1, 1, shifted);
    }

    public void renderBuffer(int x, int y, boolean shifted) {
        if (pipe instanceof PipeItemsCraftingLogisticsMk3 pipemk3) {
            renderList(pipemk3.getMk3Module().bufferList, x, y, 4, 16, shifted);
        }
    }

    public void renderList(List<?> list, int x, int y, int columns, int rows, boolean shifted) {
        ItemStackRenderer.renderItemIdentifierStackListIntoGui(
                (List<ItemIdentifierStack>) list,
                null,
                0,
                x,
                y,
                columns,
                rows,
                18,
                18,
                100.0F,
                DisplayAmount.ALWAYS,
                true,
                false,
                shifted);
    }

    @Override
    public boolean display(IHUDConfig config) {
        if (pipe instanceof PipeItemsCraftingLogisticsMk3 pipeMk3) {
            return config.isHUDCrafting()
                    && ((!pipeMk3.hasCraftingSign() && pipeMk3.getConfiguredCraftResults() != null)
                            || !pipeMk3.getMk3Module().bufferList.isEmpty()
                            || !pipeMk3.displayList.isEmpty());
        } else {
            return config.isHUDCrafting() && ((!pipe.hasCraftingSign() && pipe.getConfiguredCraftResults() != null)
                    || !pipe.displayList.isEmpty());
        }
    }

    @Override
    public boolean cursorOnWindow(int x, int y) {
        if (pipe instanceof PipeItemsCraftingLogisticsMk3 pipeMk3) {
            int bufferSize = (pipeMk3.getMk3Module().bufferList.size() / 4) + 1;
            if (pipeMk3.getMk3Module().bufferList.size() % 4 == 0) {
                bufferSize--;
            }

            if (!pipeMk3.getMk3Module().bufferList.isEmpty()) {
                return -50 < x && x < 50 && -50 < y && y < bufferSize * 20 + 10;
            }
        }

        if (!pipe.displayList.isEmpty()) {
            return -50 < x && x < 50 && -28 < y && y < 30;
        } else {
            return -30 < x && x < 30 && -22 < y && y < 25;
        }
    }
}
