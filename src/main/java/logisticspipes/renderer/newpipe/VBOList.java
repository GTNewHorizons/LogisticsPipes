package logisticspipes.renderer.newpipe;

import com.gtnewhorizon.gtnhlib.client.renderer.TessellatorManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VBOManager;
import com.gtnewhorizon.gtnhlib.client.renderer.vbo.VertexBuffer;
import com.gtnewhorizon.gtnhlib.client.renderer.vertex.DefaultVertexFormat;

public class VBOList implements IRenderable {

    private final int listID = VBOManager.generateDisplayLists(1);
    private boolean isValid = true;
    private long lastUsed = System.currentTimeMillis();
    private boolean isFilled = false;

    @Override
    public int getID() {
        return listID;
    }

    @Override
    public void startListCompile() {
        TessellatorManager.startCapturing();
    }

    @Override
    public void stopCompile() {
        VertexBuffer vbo = TessellatorManager.stopCapturingToVBO(DefaultVertexFormat.POSITION_TEXTURE_NORMAL);
        VBOManager.registerVBO(listID, vbo);
        isFilled = true;
    }

    @Override
    public void render() {
        if (!isValid) {
            throw new UnsupportedOperationException("Can't use a removed list");
        }
        VBOManager.get(listID).render();
        lastUsed = System.currentTimeMillis();
    }

    @Override
    public boolean check() {
        if (!isValid) {
            return true;
        }
        if (lastUsed + 1000 * 60 < System.currentTimeMillis()) {
            isValid = false;
            return false;
        }
        return true;
    }

    @Override
    public boolean isInvalid() {
        return !isValid;
    }

    @Override
    public boolean isFilled() {
        return isFilled;
    }

    @Override
    public void close() {
        VertexBuffer buffer = VBOManager.get(listID);
        buffer.close();
        isValid = false;
    }
}
