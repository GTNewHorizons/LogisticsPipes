package logisticspipes.gui.fluids;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import com.cleanroommc.modularui.api.MCHelper;

import codechicken.nei.item.ItemFluidDisplay;
import cpw.mods.fml.common.Loader;

final class FluidTooltip {

    private FluidTooltip() {}

    static List<String> lines(FluidPicker.Entry entry) {
        FluidStack fluid = entry.fluid();
        List<String> lines = MCHelper.getFluidTooltip(fluid);
        String name = lines.get(0);
        if (name == null || name.isEmpty()) lines.set(0, entry.name());

        if (Loader.isModLoaded("NotEnoughItems") && ItemFluidDisplay.INSTANCE != null) {
            ItemStack display = ItemFluidDisplay.createStack(fluid.getFluid(), 0);
            if (display != null) {
                Minecraft client = Minecraft.getMinecraft();
                ItemFluidDisplay.INSTANCE
                        .addInformation(display, client.thePlayer, lines, client.gameSettings.advancedItemTooltips);
                return lines;
            }
        }
        lines.addAll(MCHelper.getAdditionalFluidTooltip(fluid));
        return lines;
    }
}
