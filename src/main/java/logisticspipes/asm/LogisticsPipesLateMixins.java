package logisticspipes.asm;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;

@LateMixin
public class LogisticsPipesLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.LogisticsPipes.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        List<String> mixins = new ArrayList<>();
        if (loadedMods.contains("ComputerCraft")) {
            mixins.add("computercraft.MixinLuaJLuaMachine");
        }
        if (loadedMods.contains("ThermalDynamics")) {
            if (FMLLaunchHandler.side() == Side.CLIENT) {
                mixins.add("thermaldynamics.MixinRenderDuctItems");
            }
            mixins.add("thermaldynamics.MixinTileTDBase");
            mixins.add("thermaldynamics.MixinTravelingItem");
        }
        return mixins;
    }
}
