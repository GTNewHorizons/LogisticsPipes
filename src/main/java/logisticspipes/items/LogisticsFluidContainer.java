package logisticspipes.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.input.Keyboard;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import logisticspipes.interfaces.IItemAdvancedExistance;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.utils.item.ItemIdentifierStack;

public class LogisticsFluidContainer extends LogisticsItem implements IItemAdvancedExistance {

    static int capacity = 8000;

    @Override
    public boolean canExistInNormalInventory(ItemStack stack) {
        return false;
    }

    @Override
    public boolean canExistInWorld(ItemStack stack) {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconReg) {
        itemIcon = iconReg.registerIcon("logisticspipes:liquids/empty");
    }

    @Override
    public int getItemStackLimit() {
        return 1;
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        FluidStack stack = getFluid(par1ItemStack);
        if (stack != null) {
            String s = stack.getFluid().getUnlocalizedName();
            if (s != null) {
                return s;
            }
        }
        return super.getUnlocalizedName(par1ItemStack);
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        FluidStack fluid = getFluid(stack);
        return fluid == null ? super.getItemStackDisplayName(stack) : fluid.getLocalizedName();
    }

    private FluidStack getFluid(ItemStack stack) {
        return SimpleServiceLocator.logisticsFluidManager
                .getFluidFromContainer(ItemIdentifierStack.getFromStack(stack));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List<String> par3List,
            boolean par4) {
        super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            FluidStack stack = getFluid(par1ItemStack);
            if (stack != null) {
                par3List.add("Type:  " + stack.getFluid().getLocalizedName(stack));
                par3List.add("Value: " + stack.amount + "mB");
            }
        }
    }

    @Override
    public void getSubItems(Item par1, CreativeTabs ct, List<ItemStack> list) {
        // Don't add to creative in any way
    }
}
