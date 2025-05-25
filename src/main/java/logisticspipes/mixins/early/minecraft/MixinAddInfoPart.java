package logisticspipes.mixins.early.minecraft;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import logisticspipes.asm.addinfo.IAddInfo;
import logisticspipes.asm.addinfo.IAddInfoProvider;

@Mixin(value = { ItemStack.class, Fluid.class, FluidStack.class })
public class MixinAddInfoPart implements IAddInfoProvider {

    @Unique
    private List<IAddInfo> logisticspipes$additionalInformation;

    @SuppressWarnings("unchecked")
    @Override
    public <T extends IAddInfo> T getLogisticsPipesAddInfo(Class<T> clazz) {
        if (this.logisticspipes$additionalInformation == null) {
            return null;
        }
        for (IAddInfo info : this.logisticspipes$additionalInformation) {
            if (info != null && info.getClass() == clazz) {
                return (T) info;
            }
        }
        return null;
    }

    @Override
    public void setLogisticsPipesAddInfo(IAddInfo info) {
        if (this.logisticspipes$additionalInformation == null) {
            this.logisticspipes$additionalInformation = new ArrayList<>();
        }
        for (int i = 0; i < this.logisticspipes$additionalInformation.size(); i++) {
            if (this.logisticspipes$additionalInformation.get(i) != null
                    && this.logisticspipes$additionalInformation.get(i).getClass() == info.getClass()) {
                this.logisticspipes$additionalInformation.set(i, info);
            }
        }
    }
}
