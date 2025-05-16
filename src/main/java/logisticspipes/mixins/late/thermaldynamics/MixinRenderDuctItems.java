package logisticspipes.mixins.late.thermaldynamics;

import org.spongepowered.asm.lib.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.sugar.Local;

import cofh.thermaldynamics.duct.item.TravelingItem;
import cofh.thermaldynamics.render.RenderDuctItems;
import logisticspipes.renderer.LogisticsRenderPipe;

@Mixin(RenderDuctItems.class)
public class MixinRenderDuctItems {

    @Inject(
            at = @At(
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    remap = false,
                    target = "Lcofh/thermaldynamics/duct/item/TravelingItem;stack:Lnet/minecraft/item/ItemStack;",
                    value = "FIELD"),
            method = "renderTravelingItems",
            remap = false)
    private void logisticspipes$renderItemTransportBox(CallbackInfo ci, @Local TravelingItem item) {
        if (!LogisticsRenderPipe.config.isUseNewRenderer()) {
            return;
        }
        if (item.stack.hasTagCompound()) {
            if (item.stack.getTagCompound().getString("LogsitcsPipes_ITEM_ON_TRANSPORTATION").equals("YES")) {
                LogisticsRenderPipe.boxRenderer.doRenderItem(null, 0.0, 0.0, 0.0, 0.65 / 0.6);
            }
        }
    }
}
