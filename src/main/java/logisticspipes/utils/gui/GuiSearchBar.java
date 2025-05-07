package logisticspipes.utils.gui;

import codechicken.nei.TextField;
import org.lwjgl.input.Mouse;


public class GuiSearchBar extends TextField implements ISearchBar  {

    public GuiSearchBar(String ident) {
        super(ident);
    }

    @Override
    public void reposition(int left, int top, int width, int height) {
        this.x = left;
        this.y = top;
        this.w = width;
        this.h = height;
    }

    @Override
    public void renderSearchBar() {
        draw(Mouse.getX(), Mouse.getY());
    }

    @Override
    public boolean isFocused() {
        return focused();
    }

    @Override
    public boolean handleKey(char typedChar, int keyCode) {
        return handleKeyPress(keyCode, typedChar);
    }

    @Override
    public String getContent() {
        return text();
    }


    @Override
    public void onTextChange(String oldText) {

    }

    @Override
    public boolean isEmpty() {
        return text().isEmpty();
    }
}
