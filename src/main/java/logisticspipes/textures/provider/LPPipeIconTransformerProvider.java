package logisticspipes.textures.provider;

import java.util.ArrayList;

import net.minecraft.util.IIcon;

import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.object3d.interfaces.IIconTransformation;

public class LPPipeIconTransformerProvider {

    public ArrayList<IIconTransformation> icons = new ArrayList<>();

    public IIconTransformation getIcon(int iconIndex) {
        return icons.get(iconIndex);
    }

    public void setIcon(int index, IIcon icon) {
        while (icons.size() < index + 1) {
            icons.add(null);
        }
        if (icons.get(index) != null) {
            icons.get(index).update(icon);
        } else {
            icons.set(index, SimpleServiceLocator.cclProxy.createIconTransformer(icon));
        }
    }
}
