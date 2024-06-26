package logisticspipes.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;

import logisticspipes.LogisticsPipes;
import logisticspipes.api.IHUDArmor;
import logisticspipes.network.GuiIDs;
import logisticspipes.proxy.MainProxy;
import logisticspipes.utils.string.StringUtils;

public class ItemHUDArmor extends ItemArmor implements ISpecialArmor, IHUDArmor {

    public ItemHUDArmor(int renderIndex) {
        super(ArmorMaterial.CHAIN, renderIndex, 0);
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage,
            int slot) {
        return new ArmorProperties(0, 0, 0);
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) {
        return 0;
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        // Does not get dammaged
    }

    @Override
    public boolean getShareTag() {
        return true;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (MainProxy.isClient(world)) {
            return stack;
        }
        useItem(player, world);
        return stack.copy();
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side,
            float hitX, float hitY, float hitZ) {
        useItem(player, world);
        return !MainProxy.isClient(world);
    }

    private void useItem(EntityPlayer player, World world) {
        player.openGui(LogisticsPipes.instance, GuiIDs.GUI_HUD_Settings, world, player.inventory.currentItem, -1, 0);
    }

    @Override
    public CreativeTabs[] getCreativeTabs() {
        return new CreativeTabs[] { getCreativeTab(), LogisticsPipes.LPCreativeTab };
    }

    @Override
    public void registerIcons(IIconRegister par1IIconRegister) {
        itemIcon = par1IIconRegister.registerIcon("logisticspipes:" + getUnlocalizedName().replace("item.", ""));
    }

    @Override
    public boolean isEnabled(ItemStack item) {
        return true;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type) {
        return "logisticspipes:textures/armor/LogisticsHUD_1.png";
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        return StringUtils.translate(getUnlocalizedName(itemstack));
    }
}
