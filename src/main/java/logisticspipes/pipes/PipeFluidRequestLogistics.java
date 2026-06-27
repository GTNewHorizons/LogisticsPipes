package logisticspipes.pipes;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;

import logisticspipes.LogisticsPipes;
import logisticspipes.interfaces.routing.IRequestFluid;
import logisticspipes.network.GuiIDs;
import logisticspipes.pipes.basic.fluid.FluidRoutedPipe;
import logisticspipes.proxy.MainProxy;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.computers.interfaces.CCCommand;
import logisticspipes.proxy.computers.interfaces.CCQueued;
import logisticspipes.proxy.computers.interfaces.CCType;
import logisticspipes.request.RequestHandler;
import logisticspipes.security.SecuritySettings;
import logisticspipes.textures.Textures;
import logisticspipes.textures.Textures.TextureType;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.item.ItemIdentifierStack;

@CCType(name = "LogisticsPipes:FluidRequest")
public class PipeFluidRequestLogistics extends FluidRoutedPipe implements IRequestFluid {

    public PipeFluidRequestLogistics(Item item) {
        super(item);
    }

    public void openGui(EntityPlayer entityplayer) {
        entityplayer.openGui(LogisticsPipes.instance, GuiIDs.GUI_Fluid_Orderer_ID, getWorld(), getX(), getY(), getZ());
    }

    @Override
    public boolean handleClick(EntityPlayer entityplayer, SecuritySettings settings) {
        if (SimpleServiceLocator.toolWrenchHandler.isWrenchEquipped(entityplayer)
                && SimpleServiceLocator.toolWrenchHandler.canWrench(entityplayer, getX(), getY(), getZ())) {
            if (MainProxy.isServer(getWorld())) {
                if (settings == null || settings.openRequest) {
                    openGui(entityplayer);
                } else {
                    entityplayer.addChatMessage(new ChatComponentText("Permission denied"));
                }
            }
            SimpleServiceLocator.toolWrenchHandler.wrenchUsed(entityplayer, getX(), getY(), getZ());
            return true;
        }
        return false;
    }

    @Override
    public TextureType getCenterTexture() {
        return Textures.LOGISTICSPIPE_LIQUID_REQUEST;
    }

    @Override
    public void sendFailed(FluidIdentifier value1, Integer value2) {
        // Request Pipe doesn't handle this.
    }

    @Override
    public boolean canInsertToTanks() {
        return true;
    }

    @Override
    public boolean canInsertFromSideToTanks() {
        return true;
    }

    @Override
    public boolean canReceiveFluid() {
        return false;
    }

    /*
     * CC/OC: makeRequest / getFluidAmount take a FluidIdentifier OBJECT (like the item pipe's ItemIdentifier).
     * Construct one computer-side with LP.getFluidIdentifierBuilder() (mirrors getItemIdentifierBuilder()):
     * setFluidName("water") then build(). makeRequest's amount is a whole-number Long (mB) matching makeStack(Long
     * stackSize) - pass a Lua integer like 1, not 1.0. getAvailableFluids() deliberately returns a plain registry-name
     * -> mB table (NOT FluidIdentifier objects) so it is always safe to serialize/print on the computer: returning
     * fluid objects here would let a naive recursive dumper hit the FluidIdentifier <-> container-ItemIdentifier getter
     * cycle (getItemIdentifier() <-> getFluidContainer()) and StackOverflow. Trade-off: NBT-tagged fluid variants
     * collapse to their base name in the listing (rebuild from name addresses only the untagged variant).
     */
    @CCCommand(description = "Requests the given amount (mB) of the given FluidIdentifier")
    @CCQueued
    public Object[] makeRequest(FluidIdentifier fluid, Long amount) throws Exception {
        if (fluid == null) {
            throw new Exception("Invalid FluidIdentifier");
        }
        if (amount == null) {
            throw new Exception("Invalid amount");
        }
        return RequestHandler.computerFluidRequest(fluid, amount.intValue(), this, this);
    }

    @CCCommand(description = "Returns a table of available fluid registry-name -> amount (mB) in the network")
    @CCQueued
    public Map<String, Integer> getAvailableFluids() {
        TreeSet<ItemIdentifierStack> fluids = SimpleServiceLocator.logisticsFluidManager
                .getAvailableFluid(getRouter().getIRoutersByCost());
        Map<String, Integer> result = new HashMap<>();
        for (ItemIdentifierStack stack : fluids) {
            FluidIdentifier fluid = FluidIdentifier.get(stack.getItem());
            if (fluid == null) {
                continue;
            }
            result.merge(fluid.getName(), stack.getStackSize(), Integer::sum);
        }
        return result;
    }

    @CCCommand(description = "Asks for the available amount (mB) of the given FluidIdentifier")
    @CCQueued
    public int getFluidAmount(FluidIdentifier fluid) throws Exception {
        if (fluid == null) {
            throw new Exception("Invalid FluidIdentifier");
        }
        TreeSet<ItemIdentifierStack> fluids = SimpleServiceLocator.logisticsFluidManager
                .getAvailableFluid(getRouter().getIRoutersByCost());
        int total = 0;
        for (ItemIdentifierStack stack : fluids) {
            if (fluid.equals(FluidIdentifier.get(stack.getItem()))) {
                total += stack.getStackSize();
            }
        }
        return total;
    }
}
