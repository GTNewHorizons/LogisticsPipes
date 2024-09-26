package logisticspipes.renderer.newpipe;

public interface IRenderable {

    int getID();

    void startListCompile();

    void stopCompile();

    void render();

    boolean check();

    boolean isInvalid();

    boolean isFilled();

    void close();
}
