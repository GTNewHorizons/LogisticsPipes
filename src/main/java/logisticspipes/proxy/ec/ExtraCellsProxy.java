package logisticspipes.proxy.ec;

import extracells.api.ECApi;
import logisticspipes.proxy.interfaces.IExtraCellsProxy;
import net.minecraftforge.fluids.Fluid;

public class ExtraCellsProxy implements IExtraCellsProxy {

    @Override
    public boolean canSeeFluidInNetwork(Fluid fluid) {
        if (fluid == null) {
            return true;
        }
        return ECApi.instance().canFluidSeeInTerminal(fluid);
    }
}
