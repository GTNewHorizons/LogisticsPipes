package logisticspipes.utils.gui;

public interface ISearchBar {

    void reposition(int left, int top, int width, int heigth);

    void renderSearchBar();

    boolean handleClick(int x, int y, int k);

    boolean isFocused();

    boolean handleKey(char typedChar, int keyCode);

    String getContent();

    boolean isEmpty();
}
