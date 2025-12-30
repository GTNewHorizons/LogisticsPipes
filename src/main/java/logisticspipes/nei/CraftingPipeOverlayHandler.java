package logisticspipes.nei;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

import codechicken.nei.PositionedStack;
import codechicken.nei.api.IOverlayHandler;
import codechicken.nei.recipe.IRecipeHandler;
import logisticspipes.gui.GuiCraftingPipe;
import logisticspipes.modules.ModuleCrafter;
import logisticspipes.network.PacketHandler;
import logisticspipes.network.packets.NEISetAdvancedCraftingRecipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.item.ItemIdentifier;

public class CraftingPipeOverlayHandler implements IOverlayHandler {

    @Override
    public void overlayRecipe(GuiContainer firstGui, IRecipeHandler recipe, int recipeIndex, boolean maxTransfer) {

        if (!(firstGui instanceof GuiCraftingPipe)) {
            return;
        }

        GuiCraftingPipe gui = (GuiCraftingPipe) firstGui;
        ModuleCrafter module = gui.get_pipe();

        List<ItemStack> inputs = new ArrayList<>();
        List<ItemStack> outputs = new ArrayList<>();
        List<FluidStack> fluidInputs = new ArrayList<>();

        for (PositionedStack ps : recipe.getIngredientStacks(recipeIndex)) {
            if (ps.items != null && ps.items.length > 0) {
                // check of this is a fluid from gt
                if (ps.items[0].getTagCompound() != null) {
                    if (ps.items[0].getTagCompound().getTag("mFluidMaterialName") != null) {

                        String name = ps.items[0].getTagCompound().getString("mFluidMaterialName");
                        int amount = ps.items[0].getTagCompound().getInteger("mFluidDisplayAmount");

                        Fluid fluid = FluidRegistry.getFluid(name);
                        if (fluid == null) {
                            fluid = FluidRegistry.getFluid(name.toLowerCase());
                        }
                        if (fluid == null) {
                            fluid = FluidRegistry.getFluid("molten." + name.toLowerCase());
                        }
                        if (fluid == null) {
                            fluid = FluidRegistry.getFluid("fluid." + name.toLowerCase());
                        }
                        if (fluid == null) {
                            fluid = FluidRegistry.getFluid("gas." + name.toLowerCase());
                        }
                        if (fluid == null) {
                            fluid = FluidRegistry.getFluid("plasma." + name.toLowerCase());
                        }

                        if (fluid != null) {
                            fluidInputs.add(new FluidStack(fluid, amount));
                        }
                        continue;
                    }
                    continue;
                }

                inputs.add(ps.items[0]);
            } else {
                inputs.add(null);
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

        if (isCraftingRecipe(recipe)) {
            inputs = collapseStacks(inputs);
            outputs = collapseStacks(outputs);
        }
        fluidInputs = collapseFluids(fluidInputs);

        NEISetAdvancedCraftingRecipe packet = PacketHandler.getPacket(NEISetAdvancedCraftingRecipe.class);
        packet.setInputs(inputs).setOutputs(outputs).setFluidInputs(fluidInputs);
        packet.setModulePos(module);
        MainProxy.sendPacketToServer(packet);
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
        String name = recipe.getRecipeName();
        if (name == null) return false;
        if (name.equals("Crafting") || name.equals("Shapeless Crafting")) return true;
        String className = recipe.getClass().getName();
        return className.contains("ShapedRecipeHandler") || className.contains("ShapelessRecipeHandler");
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
