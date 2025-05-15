package logisticspipes.asm;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gtnewhorizon.gtnhmixins.IEarlyMixinLoader;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;
import logisticspipes.LPConstants;

@MCVersion("1.7.10")
@Name("Logistics Pipes Core")
@TransformerExclusions("logisticspipes.asm.")
public class LogisticsPipesCoreLoader implements IFMLLoadingPlugin, IEarlyMixinLoader {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "logisticspipes.asm.LogisticsClassTransformer" };
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        LPConstants.COREMOD_LOADED = true;
    }

    @Override
    public String getMixinConfig() {
        return "mixins.LogisticsPipes.early.json";
    }

    @Override
    public List<String> getMixins(Set<String> loadedCoreMods) {
        return Arrays.asList("minecraft.MixinAddInfoPart", "minecraft.MixinTileEntity", "minecraft.MixinWorld");
    }
}
