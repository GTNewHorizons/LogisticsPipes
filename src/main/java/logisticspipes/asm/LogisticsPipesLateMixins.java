package logisticspipes.asm;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.ILateMixinLoader;
import com.gtnewhorizon.gtnhmixins.LateMixin;

@LateMixin
public class LogisticsPipesLateMixins implements ILateMixinLoader {

    @Override
    public String getMixinConfig() {
        return "mixins.LogisticsPipes.late.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedMods) {
        if (loadedMods.contains("ComputerCraft")) {
            return Collections.singletonList("computercraft.MixinLuaJLuaMachine");
        }
        return Collections.emptyList();
    }

}
