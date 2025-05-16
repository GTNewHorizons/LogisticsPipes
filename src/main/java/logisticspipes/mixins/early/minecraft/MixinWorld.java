package logisticspipes.mixins.early.minecraft;

import net.minecraft.block.Block;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import logisticspipes.LPConstants;
import logisticspipes.routing.pathfinder.changedetection.TEControl;

@Mixin(World.class)
public class MixinWorld {

    @Inject(at = @At("HEAD"), method = "notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V")
    private void LogisticsPipes$notifyBlocksOfNeighborChange_Start(int p_147459_1_, int p_147459_2_, int p_147459_3_,
            Block p_147459_4_, CallbackInfo ci) {
        try {
            TEControl.notifyBlocksOfNeighborChange_Start((World) (Object) this, p_147459_1_, p_147459_2_, p_147459_3_);
        } catch (Exception e) {
            if (LPConstants.DEBUG) {
                throw e;
            }
            e.printStackTrace();
        }
    }

    @Inject(at = @At("TAIL"), method = "notifyBlocksOfNeighborChange(IIILnet/minecraft/block/Block;)V")
    private void LogisticsPipes$notifyBlocksOfNeighborChange_Stop(int p_147459_1_, int p_147459_2_, int p_147459_3_,
            Block p_147459_4_, CallbackInfo ci) {
        try {
            TEControl.notifyBlocksOfNeighborChange_Stop((World) (Object) this, p_147459_1_, p_147459_2_, p_147459_3_);
        } catch (Exception e) {
            if (LPConstants.DEBUG) {
                throw e;
            }
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "notifyBlockOfNeighborChange")
    private void LogisticsPipes$notifyBlockOfNeighborChange(int p_147460_1_, int p_147460_2_, int p_147460_3_,
            final Block p_147460_4_, CallbackInfo ci) {
        try {
            TEControl.notifyBlockOfNeighborChange((World) (Object) this, p_147460_1_, p_147460_2_, p_147460_3_);
        } catch (Exception e) {
            if (LPConstants.DEBUG) {
                throw e;
            }
            e.printStackTrace();
        }
    }
}
