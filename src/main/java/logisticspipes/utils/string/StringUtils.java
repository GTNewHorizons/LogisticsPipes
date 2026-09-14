package logisticspipes.utils.string;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;

import org.lwjgl.input.Keyboard;

import logisticspipes.pipes.PipeFluidSupplierMk2;
import logisticspipes.pipes.basic.CoreUnroutedPipe;
import logisticspipes.pipes.basic.LogisticsBlockGenericPipe;

public final class StringUtils {

    public static final String KEY_HOLDSHIFT = "misc.holdshift";
    public static final List<String> UNTRANSLATED_STRINGS = new ArrayList<>();

    private StringUtils() {}

    public static String translate(String key) {
        String result = StatCollector.translateToLocal(key);
        if (result.equals(key) && !StringUtils.UNTRANSLATED_STRINGS.contains(key) && !key.contains(".tip")) {
            StringUtils.UNTRANSLATED_STRINGS.add(key);
        }
        return result;
    }

    public static void addShiftAddition(ItemStack stack, List<String> list) {
        if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
            String baseKey = MessageFormat.format("{0}.tip", stack.getItem().getUnlocalizedName(stack));
            String key = baseKey + 1;
            String translation = StringUtils.translate(key);
            int i = 1;

            while (!translation.equals(key)) {
                list.add(translation);
                key = baseKey + ++i;
                translation = StringUtils.translate(key);
            }

            addExtraInfo(stack, list);
        } else {
            String baseKey = MessageFormat.format("{0}.tip", stack.getItem().getUnlocalizedName(stack));
            String key = baseKey + 1;
            String translation = StringUtils.translate(key);
            if (!translation.equals(key)) {
                list.add(StringUtils.translate(StringUtils.KEY_HOLDSHIFT));
            }
        }
    }

    private static void addExtraInfo(ItemStack stack, List<String> list) {
        Class<? extends CoreUnroutedPipe> pipeClass = LogisticsBlockGenericPipe.getPipeClassByItem(stack.getItem());
        if (pipeClass == null) {
            return;
        }

        if (PipeFluidSupplierMk2.class.isAssignableFrom(pipeClass)) {
            list.add(StringUtils.translate("item.logisticspipes.tip.active_destination"));
        }
    }

    public static String getFormatedStackSize(long stackSize, boolean forceDisplayNumber) {
        String s;

        if (stackSize == 1 && !forceDisplayNumber) {
            s = "";
        } else if (stackSize < 0) {
            s = "Inf";
        } else if (stackSize < 1000) {
            s = stackSize + "";
        } else if (stackSize < 1000000) {
            s = stackSize / 1000 + "K";
        } else if (stackSize < 1000000000) {
            s = stackSize / 1000000 + "M";
        } else if (stackSize <= Integer.MAX_VALUE) {
            s = stackSize / 1000000000 + "." + (stackSize / 100000000) % 10 + "G";
        } else {
            s = "Inf";
        }
        return s;
    }

    public static String toPercent(double value) {
        if (value > 1) {
            value = 1;
        }
        if (value < 0) {
            value = 0;
        }
        value *= 100;
        int percent = (int) value;
        return percent + "%";
    }

    public static String getWithMaxWidth(String name, int width, FontRenderer fontRenderer) {
        return cutToWidth(name, width, fontRenderer);
    }

    public static String getCuttedString(String input, int maxLength, FontRenderer renderer) {
        return cutToWidth(input, maxLength, renderer);
    }

    private static String cutToWidth(String text, int maxWidth, FontRenderer renderer) {
        if (renderer.getStringWidth(text) <= maxWidth) {
            return text;
        }
        String result = text + "...";
        while (renderer.getStringWidth(result) > maxWidth && text.length() > 1) {
            text = text.substring(0, text.length() - 1);
            result = text + "...";
        }
        return result;
    }

    public static String getStringWithSpacesFromInteger(int source) {
        String data = Integer.toString(source);
        return StringUtils.insertThousandsSeparators(data);
    }

    public static String getStringWithSpacesFromLong(long source) {
        String data = Long.toString(source);
        return StringUtils.insertThousandsSeparators(data);
    }

    public static String insertThousandsSeparators(String source) {
        StringBuilder sb = new StringBuilder();
        int i;
        for (i = source.length(); i > 3; i -= 3) {
            sb.insert(0, source.substring(i - 3, i));
            sb.insert(0, ' ');
        }
        sb.insert(0, source.substring(0, i));
        return sb.toString();
    }
}
