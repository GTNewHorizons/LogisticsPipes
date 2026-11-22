package logisticspipes.gui.fluids;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Mouse;

import logisticspipes.utils.gui.GuiGraphics;
import logisticspipes.utils.gui.SmallGuiButton;
import logisticspipes.utils.gui.SubGuiScreen;

public final class SelectFluidPopup extends SubGuiScreen {

    private static final int GRID_X = 16;
    private static final int GRID_Y = 44;
    private static final int CELL = 18;
    private final FluidPicker picker = new FluidPicker();
    private final RenderItem renderer = new RenderItem();
    private final Consumer<FluidPicker.Entry> onSelected;
    private GuiTextField search;

    public SelectFluidPopup(Consumer<FluidPicker.Entry> onSelected) {
        super(176, 194, 0, 0);
        this.onSelected = onSelected;
    }

    @Override
    public void initGui() {
        super.initGui();
        String query = search == null ? "" : search.getText();
        search = new GuiTextField(fontRendererObj, guiLeft + 9, guiTop + 19, 158, 15);
        search.setMaxStringLength(96);
        search.setText(query);
        search.setFocused(true);
        buttonList.clear();
        buttonList.add(new SmallGuiButton(0, guiLeft + 108, guiTop + 5, 10, 10, "<"));
        buttonList.add(new SmallGuiButton(1, guiLeft + 157, guiTop + 5, 10, 10, ">"));
    }

    @Override
    protected void renderGuiBackground(int mouseX, int mouseY) {
        GuiGraphics.drawGuiBackGround(mc, guiLeft, guiTop, right, bottom, zLevel, true);
        fontRendererObj
                .drawString(StatCollector.translateToLocal("gui.fluidpicker.title"), guiLeft + 9, guiTop + 6, 0x404040);
        String page = picker.pageLabel();
        fontRendererObj
                .drawString(page, guiLeft + 137 - fontRendererObj.getStringWidth(page) / 2, guiTop + 6, 0x404040);
        search.drawTextBox();
        if (search.getText().isEmpty()) {
            fontRendererObj.drawString(
                    StatCollector.translateToLocal("gui.fluidpicker.hint"),
                    guiLeft + 14,
                    guiTop + 23,
                    0xA0A0A0);
        }
        if (picker.pageCount() == 0) {
            String message = StatCollector.translateToLocal("gui.fluidpicker.noResults");
            fontRendererObj
                    .drawString(message, xCenter - fontRendererObj.getStringWidth(message) / 2, guiTop + 57, 0x404040);
        }
        for (int cell = 0; cell < FluidPicker.PAGE_SIZE && picker.get(cell) != null; cell++) {
            GuiGraphics.drawSlotBackground(mc, cellX(cell) - 1, cellY(cell) - 1);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        for (int cell = 0; cell < FluidPicker.PAGE_SIZE; cell++) {
            FluidPicker.Entry entry = picker.get(cell);
            if (entry == null) break;
            renderer.renderItemAndEffectIntoGUI(
                    fontRendererObj,
                    mc.renderEngine,
                    entry.icon(),
                    cellX(cell),
                    cellY(cell));
        }
    }

    @Override
    protected void renderToolTips(int mouseX, int mouseY, float partialTicks) {
        FluidPicker.Entry entry = entryAt(mouseX, mouseY);
        if (entry != null) drawHoveringText(FluidTooltip.lines(entry), mouseX, mouseY, fontRendererObj);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        picker.changePage(button.id == 0 ? -1 : 1);
    }

    @Override
    public void handleMouseInputSub() {
        super.handleMouseInputSub();
        int wheel = Mouse.getEventDWheel();
        int mouseX = Mouse.getEventX() * width / mc.displayWidth;
        int mouseY = height - Mouse.getEventY() * height / mc.displayHeight - 1;
        if (wheel != 0 && mouseX >= guiLeft && mouseX < right && mouseY >= guiTop && mouseY < bottom) {
            picker.changePage(wheel > 0 ? -1 : 1);
        }
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int button) {
        search.mouseClicked(mouseX, mouseY, button);
        FluidPicker.Entry entry = entryAt(mouseX, mouseY);
        if (button == 0 && entry != null) {
            onSelected.accept(entry);
            exitGui();
            return;
        }
        super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        if (search.textboxKeyTyped(typedChar, keyCode)) picker.search(search.getText());
        else super.keyTyped(typedChar, keyCode);
    }

    private int cellX(int cell) {
        return guiLeft + GRID_X + cell % FluidPicker.COLUMNS * CELL;
    }

    private int cellY(int cell) {
        return guiTop + GRID_Y + cell / FluidPicker.COLUMNS * CELL;
    }

    private FluidPicker.Entry entryAt(int mouseX, int mouseY) {
        int x = mouseX - cellX(0);
        int y = mouseY - cellY(0);
        if (x < 0 || x >= FluidPicker.COLUMNS * CELL
                || y < 0
                || y >= FluidPicker.ROWS * CELL
                || x % CELL >= 16
                || y % CELL >= 16)
            return null;
        return picker.get(y / CELL * FluidPicker.COLUMNS + x / CELL);
    }
}
