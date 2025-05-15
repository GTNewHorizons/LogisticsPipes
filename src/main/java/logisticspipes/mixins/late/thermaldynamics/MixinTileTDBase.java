package logisticspipes.mixins.late.thermaldynamics;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import cofh.thermaldynamics.block.TileTDBase;
import cofh.thermaldynamics.duct.Duct;
import cofh.thermaldynamics.duct.item.TileItemDuct;
import logisticspipes.asm.td.DuctAccessor;
import logisticspipes.pipes.basic.LogisticsTileGenericPipe;

@Mixin(TileTDBase.class)
public class MixinTileTDBase implements DuctAccessor {

    @Shadow(remap = false)
    Duct duct;

    @ModifyReturnValue(at = @At("RETURN"), method = "getAdjTileEntitySafe", remap = false)
    private TileEntity logisticspipes$checkGetTileEntity(TileEntity tile, int side) {
        if ((TileTDBase) (Object) this instanceof TileItemDuct) {
            if (tile instanceof LogisticsTileGenericPipe) {
                LogisticsTileGenericPipe pipe = (LogisticsTileGenericPipe) tile;
                return pipe.tdPart.getInternalDuctForSide(ForgeDirection.getOrientation(side).getOpposite());
            }
        }
        return tile;
    }

    @Override
    public Duct getDuct() {
        return this.duct;
    }

    @Override
    public void setDuct(Duct duct) {
        this.duct = duct;
    }
}
