package logisticspipes.items;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class ItemParts extends LogisticsItem {

    private IIcon[] _icons;

    public ItemParts() {
        setHasSubtypes(true);
    }

    @Override
    public void registerIcons(IIconRegister iconreg) {
        _icons = new IIcon[9];
        for (int i = 0; i < 9; i++) {
            _icons[i] = iconreg.registerIcon("logisticspipes:" + getUnlocalizedName().replace("item.", "") + "/" + i);
        }
    }

    @Override
    public IIcon getIconFromDamage(int par1) {
        return _icons[par1 % 9];
    }

    @Override
    public String getUnlocalizedName(ItemStack par1ItemStack) {
        switch (par1ItemStack.getItemDamage()) {
            case 0: // bow
                return "item.HUDbow";
            case 1: // glass
                return "item.HUDglass";
            case 2: // nose bridge
                return "item.HUDnosebridge";
            case 3:
                return "item.NanoHopper";
            case 4:
                return "item.blankupgrade";
            case 5:
                return "item.goldupgradechip";
            case 6:
                return "item.diamondupgradechip";
            case 7:
                return "item.goldcraftingupgradechip";
            case 8:
                return "item.diamondcraftingupgradechip";
        }
        return super.getUnlocalizedName(par1ItemStack);
    }

    @Override
    public CreativeTabs getCreativeTab() {
        return CreativeTabs.tabRedstone;
    }

    @SuppressWarnings({ "unchecked" })
    @Override
    public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
        par3List.add(new ItemStack(this, 1, 0));
        par3List.add(new ItemStack(this, 1, 1));
        par3List.add(new ItemStack(this, 1, 2));
        par3List.add(new ItemStack(this, 1, 3));
        par3List.add(new ItemStack(this, 1, 4));
        par3List.add(new ItemStack(this, 1, 5));
        par3List.add(new ItemStack(this, 1, 6));
        par3List.add(new ItemStack(this, 1, 7));
        par3List.add(new ItemStack(this, 1, 8));
    }
}
