package logisticspipes.nei;

import java.util.*;

import codechicken.nei.recipe.StackInfo;
import codechicken.nei.recipe.TemplateRecipeHandler;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import logisticspipes.gui.GuiCraftingPipe;
import logisticspipes.gui.popup.GuiRecipeImport;
import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.NEISetAdvancedCraftingRecipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.gui.SubGuiScreen;
import logisticspipes.utils.item.ItemIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;

public class CraftingPipeOverlayHandler implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {

        if (!(firstGui instanceof GuiCraftingPipe)) {
            return;
        }

        GuiCraftingPipe gui = (GuiCraftingPipe) firstGui;
        ModuleCrafter module = gui.get_pipe();

        List<List<ItemStack>> inputOptions = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        List<FluidStack> fluidInputs = new ArrayList<>();

        for (PositionedStack ps : recipe.getIngredientStacks(recipeIndex)) {
            if (ps.items != null && ps.items.length > 0) {
                FluidStack fluid = StackInfo.getFluid(ps.items[0]);

                if (fluid != null) {
                    fluidInputs.add(fluid);
                } else {
                    List<ItemStack> options = new ArrayList<>(Arrays.asList(ps.items));
                    inputOptions.add(options);
                }

            } else {
                inputOptions.add(null);
            }
        }

        PositionedStack result = recipe.getResultStack(recipeIndex);
        if (result != null && result.items != null && result.items.length > 0) {
            outputs.add(result.items[0]);
        }

        for (PositionedStack ps : recipe.getOtherStacks(recipeIndex)) {
            if (ps.items != null && ps.items.length > 0) {
                outputs.add(ps.items[0]);
            }
        }

        final List<FluidStack> finalFluidInputs = collapseFluids(fluidInputs);
        final List<ItemStack> finalOutputs = collapseStacks(outputs);

        List<List<ItemStack>> collapsedInputOptions = collapseInputOptions(inputOptions);

        ItemStack[][] stacks = new ItemStack[9][];
        int i = 0;
        for (List<ItemStack> options : collapsedInputOptions) {
            if (options != null) {
                stacks[i] = options.toArray(new ItemStack[0]);
            }
            i++;
            if (i >= 9) break;
        }

        NEISetAdvancedCraftingRecipe packet = PacketHandler.getPacket(NEISetAdvancedCraftingRecipe.class);
        packet.setModulePos(module);

        GuiRecipeImport subGui = new GuiRecipeImport(null, stacks, packet, finalOutputs, finalFluidInputs);
        if (!gui.hasSubGui()) {
            gui.setSubGui(subGui);
        } else {
            SubGuiScreen next = gui.getSubGui();
            while (next.hasSubGui()) {
                next = next.getSubGui();
            }
            next.setSubGui(subGui);
        }
    }

    private List<FluidStack> collapseFluids(List<FluidStack> fluids) {
        List<FluidStack> result = new ArrayList<>();
        for (FluidStack fluid : fluids) {
            if (fluid == null || fluid.getFluid() == null) continue;
            boolean found = false;
            for (FluidStack existing : result) {
                if (existing.isFluidEqual(fluid)) {
                    existing.amount += fluid.amount;
                    found = true;
                    break;
                }
            }
            if (!found) {
                result.add(fluid.copy());
            }
        }
        return result;
    }

    private List<List<ItemStack>> collapseInputOptions(List<List<ItemStack>> inputOptions) {
        List<List<ItemStack>> result = new ArrayList<>();
        for (List<ItemStack> options : inputOptions) {
            if (options == null || options.isEmpty()) continue;
            boolean found = false;
            for (List<ItemStack> existingOptions : result) {
                if (optionsAreEqual(options, existingOptions)) {
                    for (int i = 0; i < existingOptions.size(); i++) {
                        existingOptions.get(i).stackSize += options.get(i).stackSize;
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                List<ItemStack> copy = new ArrayList<>();
                for (ItemStack stack : options) {
                    copy.add(stack.copy());
                }
                result.add(copy);
            }
        }
        return result;
    }

    private boolean optionsAreEqual(List<ItemStack> options1, List<ItemStack> options2) {
        if (options1.size() != options2.size()) return false;
        for (int i = 0; i < options1.size(); i++) {
            ItemStack stack1 = options1.get(i);
            ItemStack stack2 = options2.get(i);
            if (!ItemIdentifier.get(stack1).equals(ItemIdentifier.get(stack2))) return false;
        }
        return true;
    }

    private List<ItemStack> collapseStacks(List<ItemStack> stacks) {
        Map<ItemIdentifier, Integer> collapsed = new LinkedHashMap<>();
        for (ItemStack stack : stacks) {
            if (stack == null) continue;
            ItemIdentifier id = ItemIdentifier.get(stack);
            collapsed.put(id, collapsed.getOrDefault(id, 0) + stack.stackSize);
        }
        List<ItemStack> result = new ArrayList<>();
        for (Map.Entry<ItemIdentifier, Integer> entry : collapsed.entrySet()) {
            result.add(entry.getKey().makeNormalStack(entry.getValue()));
        }
        return result;
    }
}
