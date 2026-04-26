package logisticspipes.renderer.newpipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import logisticspipes.proxy.MainProxy;

public class GLRenderListHandler {

    private final List<VBOList> collection = new ArrayList<>();

    public VBOList getNewRenderList() {
        VBOList list = new VBOList();
        collection.add(list);
        return list;
    }

    public void tick() {
        if (collection.isEmpty() || MainProxy.getGlobalTick() % 20 != 0) return;
        Iterator<VBOList> it = collection.iterator();
        while (it.hasNext()) {
            VBOList ref = it.next();
            if (!ref.check()) {
                ref.close();
                it.remove();
            }
        }
    }
}
