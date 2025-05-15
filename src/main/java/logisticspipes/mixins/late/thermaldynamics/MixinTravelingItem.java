package logisticspipes.mixins.late.thermaldynamics;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;

import cofh.thermaldynamics.duct.item.TravelingItem;
import logisticspipes.asm.td.RoutingInformationAccessor;
import logisticspipes.routing.ItemRoutingInformation;

@Mixin(TravelingItem.class)
public class MixinTravelingItem implements RoutingInformationAccessor {

    @Unique
    private ItemRoutingInformation logisticspipes$routingInformation;

    @Override
    public ItemRoutingInformation getRoutingInformation() {
        return this.logisticspipes$routingInformation;
    }

    @Override
    public void setRoutingInformation(ItemRoutingInformation routingInformation) {
        this.logisticspipes$routingInformation = routingInformation;
    }

    @Inject(at = @At("HEAD"), method = "toNBT", remap = false)
    private void logisticspipes$travelingItemToNBT(NBTTagCompound nbt, CallbackInfo ci) {
        if (this.logisticspipes$routingInformation != null) {
            NBTTagCompound save = new NBTTagCompound();
            this.logisticspipes$routingInformation.writeToNBT(save);
            nbt.setTag("LPRoutingInformation", save);
        }
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/nbt/NBTTagCompound;)V", remap = false)
    private void logisticspipes$travelingItemNBTContructor(NBTTagCompound nbt, CallbackInfo ci) {
        if (!nbt.hasKey("LPRoutingInformation")) {
            return;
        }
        this.logisticspipes$routingInformation = new ItemRoutingInformation();
        this.logisticspipes$routingInformation.readFromNBT(nbt.getCompoundTag("LPRoutingInformation"));
    }

    @ModifyExpressionValue(
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    remap = false,
                    target = "Lcofh/thermaldynamics/duct/item/TravelingItem;stack:Lnet/minecraft/item/ItemStack;",
                    value = "FIELD"),
            method = "writePacket",
            remap = false)
    private ItemStack logisticspipes$handleItemSendPacket(ItemStack original) {
        if (original == null) {
            return null;
        }
        if (this.logisticspipes$routingInformation != null) {
            original = original.copy();
            if (!original.hasTagCompound()) {
                original.setTagCompound(new NBTTagCompound());
            }
            original.getTagCompound().setString("LogsitcsPipes_ITEM_ON_TRANSPORTATION", "YES");
        }
        return original;
    }
}
