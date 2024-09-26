package logisticspipes.renderer.newpipe;

import static logisticspipes.LogisticsPipes.enableVBO;

import java.util.ArrayList;
import java.util.List;

public class GLRenderListHandler {

    private List<IRenderable> collection = new ArrayList<>();

    public IRenderable getNewRenderList() {
        IRenderable list = enableVBO ? new VBOList() : new GLRenderList();
        collection.add(list);
        return list;
    }

    public void tick() {
        List<IRenderable> newCollection = new ArrayList<>(collection);
        for (IRenderable ref : collection) {
            if (!ref.check()) {
                ref.close();
                newCollection.remove(ref);
            }
        }
        if (newCollection.size() != collection.size()) {
            collection = newCollection;
        }
    }
}
