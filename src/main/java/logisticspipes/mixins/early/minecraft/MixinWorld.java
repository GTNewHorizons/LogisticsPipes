package logisticspipes.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import logisticspipes.asm.LogisticsASMHookClass;

@Mixin(World.class)
public class MixinWorld {

    @Inject(at = @At("HEAD"), method = "notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V")
    private void logisticspipes$notifyBlocksOfNeighborChange_Start(int p_147459_1_, int p_147459_2_, int p_147459_3_,
            Block p_147459_4_, CallbackInfo ci) {
        LogisticsASMHookClass
                .notifyBlocksOfNeighborChange_Start((World) (Object) this, p_147459_1_, p_147459_2_, p_147459_3_);
    }

    @Inject(at = @At("TAIL"), method = "notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V")
    private void logisticspipes$notifyBlocksOfNeighborChange_Stop(int p_147459_1_, int p_147459_2_, int p_147459_3_,
            Block p_147459_4_, CallbackInfo ci) {
        LogisticsASMHookClass
                .notifyBlocksOfNeighborChange_Stop((World) (Object) this, p_147459_1_, p_147459_2_, p_147459_3_);
    }

    @Inject(at = @At("HEAD"), method = "notifyBlockOfNeighborChange")
    private void logisticspipes$notifyBlockOfNeighborChange(int p_147460_1_, int p_147460_2_, int p_147460_3_,
            final Block p_147460_4_, CallbackInfo ci) {
        LogisticsASMHookClass.notifyBlockOfNeighborChange((World) (Object) this, p_147460_1_, p_147460_2_, p_147460_3_);
    }
}
