package logisticspipes.mixins.early.minecraft;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import logisticspipes.LPConstants;
import logisticspipes.asm.te.ILPTEInformation;
import logisticspipes.asm.te.LPTileEntityObject;
import logisticspipes.routing.pathfinder.changedetection.TEControl;

@Mixin(TileEntity.class)
public class MixinTileEntity implements ILPTEInformation {

    @Unique
    private LPTileEntityObject logisticspipes$informationObject;

    @Inject(at = @At("HEAD"), method = "invalidate")
    private void logisticspipes$invalidate(CallbackInfo ci) {
        try {
            TEControl.invalidate((TileEntity) (Object) this);
        } catch (Exception e) {
            if (LPConstants.DEBUG) {
                throw e;
            }
            e.printStackTrace();
        }
    }

    @Inject(at = @At("HEAD"), method = "validate")
    private void logisticspipes$validate(CallbackInfo ci) {
        try {
            TEControl.validate((TileEntity) (Object) this);
        } catch (Exception e) {
            if (LPConstants.DEBUG) {
                throw e;
            }
            e.printStackTrace();
        }
    }

    @Override
    public LPTileEntityObject getObject() {
        return this.logisticspipes$informationObject;
    }

    @Override
    public void setObject(LPTileEntityObject object) {
        this.logisticspipes$informationObject = object;
    }
}
