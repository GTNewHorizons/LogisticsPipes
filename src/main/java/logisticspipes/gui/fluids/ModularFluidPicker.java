package logisticspipes.gui.fluids;

import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.IPanelHandler;
import com.cleanroommc.modularui.api.drawable.IKey;
import com.cleanroommc.modularui.screen.ModularPanel;
import com.cleanroommc.modularui.utils.Alignment;
import com.cleanroommc.modularui.value.ObjectValue;
import com.cleanroommc.modularui.value.StringValue;
import com.cleanroommc.modularui.value.sync.FluidSlotSyncHandler;
import com.cleanroommc.modularui.widgets.ButtonWidget;
import com.cleanroommc.modularui.widgets.ItemDisplayWidget;
import com.cleanroommc.modularui.widgets.slot.FluidSlot;
import com.cleanroommc.modularui.widgets.textfield.TextFieldWidget;

public final class ModularFluidPicker {

    private ModularFluidPicker() {}

    public static ButtonWidget<?> searchButton(ModularPanel parent, FluidSlotSyncHandler slot, boolean clientSide) {
        IPanelHandler picker = clientSide ? IPanelHandler.simple(parent, (panel, player) -> createPanel(slot), true)
                : null;
        return new ButtonWidget<>().size(42, 14).overlay(IKey.lang("gui.fluidpicker.search")).onMousePressed(button -> {
            if (button != 0 || picker == null || picker.isPanelOpen()) return false;
            picker.deleteCachedPanel();
            picker.openPanel();
            return true;
        });
    }

    private static ModularPanel createPanel(FluidSlotSyncHandler slot) {
        FluidPicker picker = new FluidPicker();
        ModularPanel panel = new ModularPanel("lp_fluid_picker") {

            @Override
            public boolean disablePanelsBelow() {
                return true;
            }
        }.size(176, 194);

        panel.child(IKey.lang("gui.fluidpicker.title").asWidget().left(9).top(6)).child(ButtonWidget.panelCloseButton())
                .child(pageButton(picker, -1, "<", 94))
                .child(
                        IKey.dynamic(picker::pageLabel).alignment(Alignment.Center).asWidget().size(38, 12).left(106)
                                .top(4))
                .child(pageButton(picker, 1, ">", 144))
                .child(
                        new TextFieldWidget().setMaxLength(96).setFocusOnGuiOpen(true).autoUpdateOnChange(true)
                                .hintText(StatCollector.translateToLocal("gui.fluidpicker.hint"))
                                .value(new StringValue("") {

                                    @Override
                                    public void setStringValue(String value) {
                                        super.setStringValue(value);
                                        picker.search(value);
                                    }
                                }).size(158, 15).left(9).top(19))
                .child(
                        IKey.lang("gui.fluidpicker.noResults").alignment(Alignment.Center).asWidget().width(176).top(57)
                                .setEnabledIf(widget -> picker.pageCount() == 0));

        for (int cell = 0; cell < FluidPicker.PAGE_SIZE; cell++) {
            final int position = cell;
            panel.child(
                    new ButtonWidget<>().size(18).left(16 + cell % FluidPicker.COLUMNS * 18)
                            .top(43 + cell / FluidPicker.COLUMNS * 18)
                            // The item display owns hover events inside the button.
                            .child(new ItemDisplayWidget().item(new ObjectValue.Dynamic<>(ItemStack.class, () -> {
                                FluidPicker.Entry entry = picker.get(position);
                                return entry == null ? null : entry.icon();
                            }, null)).tooltipAutoUpdate(true).tooltipBuilder(tooltip -> {
                                FluidPicker.Entry entry = picker.get(position);
                                if (entry != null) {
                                    for (String line : FluidTooltip.lines(entry)) tooltip.addLine(IKey.str(line));
                                }
                            })).setEnabledIf(widget -> picker.get(position) != null).onMousePressed(button -> {
                                FluidPicker.Entry entry = picker.get(position);
                                if (button != 0 || entry == null) return false;
                                slot.setValue(entry.fluid());
                                panel.closeIfOpen();
                                return true;
                            }));
        }
        return panel;
    }

    private static ButtonWidget<?> pageButton(FluidPicker picker, int direction, String label, int left) {
        return new ButtonWidget<>().size(12).left(left).top(4).overlay(IKey.str(label)).onMousePressed(button -> {
            if (button != 0) return false;
            picker.changePage(direction);
            return true;
        });
    }

    public static final class FilterSlot extends FluidSlot {

        @Override
        public boolean handleDragAndDrop(ItemStack draggedStack, int button) {
            FluidStack fluid = FluidPicker.getDraggedFluid(draggedStack);
            if (fluid == null) return false;
            FluidStack marker = fluid.copy();
            marker.amount = 1;
            getSyncHandler().setValue(marker);
            draggedStack.stackSize = 0;
            return true;
        }
    }
}
