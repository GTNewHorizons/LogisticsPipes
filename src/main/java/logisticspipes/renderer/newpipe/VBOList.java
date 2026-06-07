package logisticspipes.renderer.newpipe;

import com.gtnewhorizon.gtnhlib.client.renderer.DirectTessellator;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.IVertexArrayObject;
import com.gtnewhorizon.gtnhlib.client.renderer.vao.VertexBufferType;

public class VBOList {

    private IVertexArrayObject vbo;
    private boolean isValid = true;
    private long lastUsed = System.currentTimeMillis();
    private boolean isFilled = false;

    public void startListCompile() {
        DirectTessellator.startCapturing();
    }

    public void stopCompile() {
        vbo = DirectTessellator.stopCapturingToVBO(VertexBufferType.IMMUTABLE);
        isFilled = true;
    }

    public void render() {
        if (!isValid) {
            throw new UnsupportedOperationException("Can't use a removed list");
        }
        vbo.render();
        lastUsed = System.currentTimeMillis();
    }

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

    public boolean isInvalid() {
        return !isValid;
    }

    public boolean isFilled() {
        return isFilled;
    }

    public void close() {
        vbo.delete();
        isValid = false;
    }
}
