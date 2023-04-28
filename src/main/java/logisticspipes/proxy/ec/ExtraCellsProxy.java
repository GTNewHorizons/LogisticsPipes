package logisticspipes.proxy.ec;

import net.minecraftforge.fluids.Fluid;

import extracells.api.ECApi;
import logisticspipes.proxy.interfaces.IExtraCellsProxy;

public class ExtraCellsProxy implements IExtraCellsProxy {

    @Override
    public boolean canSeeFluidInNetwork(Fluid fluid) {
        if (fluid == null) {
            return true;
        }
        return ECApi.instance().canFluidSeeInTerminal(fluid);
    }
}
