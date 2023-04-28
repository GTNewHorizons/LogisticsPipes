package logisticspipes.utils.gui;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

import org.lwjgl.input.Keyboard;

import logisticspipes.utils.Color;

public class SearchBar {

    public String searchinput1 = "";
    public String searchinput2 = "";
    private boolean isActive;
    private boolean displaycursor = true;
    private long oldSystemTime = 0;
    private int searchWidth;
    private final boolean numberOnly;
    private final boolean alignRight;

    private final FontRenderer fontRenderer;
    private final LogisticsBaseGuiScreen screen;
    private int left, top, heigth, width;

    public SearchBar(FontRenderer fontRenderer, LogisticsBaseGuiScreen screen, int left, int top, int width,
            int heigth) {
        this(fontRenderer, screen, left, top, width, heigth, true);
    }

    public SearchBar(FontRenderer fontRenderer, LogisticsBaseGuiScreen screen, int left, int top, int width, int heigth,
            boolean isActive) {
        this(fontRenderer, screen, left, top, width, heigth, isActive, false);
    }

    public SearchBar(FontRenderer fontRenderer, LogisticsBaseGuiScreen screen, int left, int top, int width, int heigth,
            boolean isActive, boolean numberOnly) {
        this(fontRenderer, screen, left, top, width, heigth, isActive, numberOnly, false);
    }

    public SearchBar(FontRenderer fontRenderer, LogisticsBaseGuiScreen screen, int left, int top, int width, int heigth,
            boolean isActive, boolean numberOnly, boolean alignRight) {
        this.fontRenderer = fontRenderer;
        this.screen = screen;
        this.left = left;
        this.top = top;
        this.width = width;
        this.heigth = heigth;
        searchWidth = width - 10;
        this.isActive = isActive;
        this.numberOnly = numberOnly;
        this.alignRight = alignRight;
    }

    public void reposition(int left, int top, int width, int heigth) {
        this.left = left;
        this.top = top;
        this.width = width;
        this.heigth = heigth;
        searchWidth = width - 10;
    }

    public void renderSearchBar() {
        if (isFocused()) {
            screen.drawRect(left, top - 2, left + width, top + heigth, Color.BLACK);
            screen.drawRect(left + 1, top - 1, left + width - 1, top + heigth - 1, Color.WHITE);
        } else {
            screen.drawRect(left + 1, top - 1, left + width - 1, top + heigth - 1, Color.BLACK);
        }
        screen.drawRect(left + 2, top, left + width - 2, top + heigth - 2, Color.DARKER_GREY);
        if (alignRight) {
            fontRenderer.drawString(
                    searchinput1 + searchinput2,
                    left + 5 + searchWidth - fontRenderer.getStringWidth(searchinput1 + searchinput2),
                    top + 3,
                    0xFFFFFF);
        } else {
            fontRenderer.drawString(searchinput1 + searchinput2, left + 5, top + 3, 0xFFFFFF);
        }
        if (isFocused()) {
            int linex;
            if (alignRight) {
                linex = left + 5 + searchWidth - fontRenderer.getStringWidth(searchinput2);
            } else {
                linex = left + 5 + fontRenderer.getStringWidth(searchinput1);
            }
            if (System.currentTimeMillis() - oldSystemTime > 500) {
                displaycursor = !displaycursor;
                oldSystemTime = System.currentTimeMillis();
            }
            if (displaycursor) {
                screen.drawRect(linex, top + 1, linex + 1, top + heigth - 3, Color.WHITE);
            }
        }
    }

    /**
     * @return Boolean, true if click was handled.
     */
    public boolean handleClick(int x, int y, int k) {
        if (x >= left + 2 && x < left + width - 2 && y >= top && y < top + heigth) {
            focus();
            if (k == 1) {
                searchinput1 = "";
                searchinput2 = "";
            }
            return true;
        } else if (isFocused()) {
            unFocus();
            return true;
        }
        return false;
    }

    private void unFocus() {
        isActive = false;
        if (numberOnly) {
            searchinput1 += searchinput2;
            searchinput2 = "";
            try {
                int value = Integer.parseInt(searchinput1);
                searchinput1 = Integer.toString(value);
            } catch (Exception e) {
                searchinput1 = "";
            }
            if (searchinput1.isEmpty() && searchinput2.isEmpty()) {
                searchinput1 = "0";
            }
        }
    }

    private void focus() {
        isActive = true;
    }

    public boolean isFocused() {
        return isActive;
    }

    /**
     * @return Boolean, true if key was handled.
     */
    public boolean handleKey(char typedChar, int keyCode) {
        if (!isFocused()) {
            return false;
        }
        if (keyCode == Keyboard.KEY_ESCAPE) {
            return false;
        }
        if (keyCode == Keyboard.KEY_RETURN) {
            unFocus();
        } else if (keyCode == Keyboard.KEY_BACK) {
            if (searchinput1.length() > 0) {
                searchinput1 = searchinput1.substring(0, searchinput1.length() - 1);
            }
        } else if (keyCode == Keyboard.KEY_LEFT) {
            if (searchinput1.length() > 0) {
                searchinput2 = searchinput1.substring(searchinput1.length() - 1) + searchinput2;
                searchinput1 = searchinput1.substring(0, searchinput1.length() - 1);
            }
        } else if (keyCode == Keyboard.KEY_RIGHT) {
            if (searchinput2.length() > 0) {
                searchinput1 += searchinput2.substring(0, 1);
                searchinput2 = searchinput2.substring(1);
            }
        } else if (keyCode == Keyboard.KEY_HOME) {
            searchinput2 = searchinput1 + searchinput2;
            searchinput1 = "";
        } else if (keyCode == Keyboard.KEY_END) {
            searchinput1 = searchinput1 + searchinput2;
            searchinput2 = "";
        } else if (keyCode == Keyboard.KEY_DELETE) {
            if (searchinput2.length() > 0) {
                searchinput2 = searchinput2.substring(1);
            }
        } else if (keyCode == Keyboard.KEY_V && GuiScreen.isCtrlKeyDown()) {
            boolean isFine = true;
            if (numberOnly) {
                try {
                    Integer.valueOf(SearchBar.getClipboardString());
                } catch (Exception e) {
                    isFine = false;
                }
            }
            if (isFine) {
                String toAdd = SearchBar.getClipboardString();
                while (fontRenderer.getStringWidth(searchinput1 + toAdd + searchinput2) > searchWidth) {
                    toAdd = toAdd.substring(0, toAdd.length() - 1);
                }
                searchinput1 = searchinput1 + toAdd;
            }
        } else
            if ((!numberOnly && !Character.isISOControl(typedChar)) || (numberOnly && Character.isDigit(typedChar))) {
                if (fontRenderer.getStringWidth(searchinput1 + typedChar + searchinput2) <= searchWidth) {
                    searchinput1 += typedChar;
                }
            } else {
                // ignore this key/character
            }
        return true;
    }

    public String getContent() {
        return searchinput1 + searchinput2;
    }

    public boolean isEmpty() {
        return searchinput1.isEmpty() && searchinput2.isEmpty();
    }

    private static String getClipboardString() {
        try {
            Transferable transferable = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null);
            if (transferable != null && transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return (String) transferable.getTransferData(DataFlavor.stringFlavor);
            }
        } catch (Exception ignored) {}
        return "";
    }
}
