package logisticspipes.nei;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
import logisticspipes.gui.popup.SelectItemOutOfList;
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
                    List<ItemStack> options = new ArrayList<>();
                    for (ItemStack stack : ps.items) {
                        options.add(stack);
                    }
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
        final List<ItemStack> finalOutputs = isCraftingRecipe(recipe) ? collapseStacks(outputs) : outputs;
        final boolean isCrafting = isCraftingRecipe(recipe);

        handleOptions(gui, inputOptions, new ArrayList<>(), module, isCrafting, finalOutputs, finalFluidInputs);
    }

    private void handleOptions(GuiCraftingPipe gui, List<List<ItemStack>> inputOptions, List<ItemStack> selectedInputs, ModuleCrafter module, boolean isCrafting, List<ItemStack> outputs, List<FluidStack> fluidInputs) {
        if (selectedInputs.size() == inputOptions.size()) {
            List<ItemStack> finalInputs = isCrafting ? collapseStacks(selectedInputs) : selectedInputs;
            NEISetAdvancedCraftingRecipe packet = PacketHandler.getPacket(NEISetAdvancedCraftingRecipe.class);
            packet.setInputs(finalInputs).setOutputs(outputs).setFluidInputs(fluidInputs);
            packet.setModulePos(module);
            MainProxy.sendPacketToServer(packet);
            return;
        }

        List<ItemStack> nextOptions = inputOptions.get(selectedInputs.size());
        if (nextOptions == null || nextOptions.size() <= 1) {
            selectedInputs.add(nextOptions == null ? null : nextOptions.get(0));
            handleOptions(gui, inputOptions, selectedInputs, module, isCrafting, outputs, fluidInputs);
        } else {
            List<ItemIdentifierStack> candidates = new ArrayList<>();
            for (ItemStack stack : nextOptions) {
                candidates.add(ItemIdentifierStack.getFromStack(stack));
            }
            SelectItemOutOfList subGui = new SelectItemOutOfList(candidates, slot -> {
                selectedInputs.add(nextOptions.get(slot));
                handleOptions(gui, inputOptions, selectedInputs, module, isCrafting, outputs, fluidInputs);
            });
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

    private boolean isCraftingRecipe(IRecipeHandler recipe) {
        if (recipe instanceof TemplateRecipeHandler templateRecipeHandler) {
            String overlayIdentifier = templateRecipeHandler.getOverlayIdentifier();
            return "crafting".equals(overlayIdentifier) || "crafting2x2".equals(overlayIdentifier);
        } else {
            return false;
        }
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
