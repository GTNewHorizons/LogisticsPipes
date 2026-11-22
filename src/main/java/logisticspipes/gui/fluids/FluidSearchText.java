package logisticspipes.gui.fluids;

import java.util.Locale;
import java.util.regex.Pattern;

final class FluidSearchText {

    private static final Pattern SEPARATORS = Pattern.compile("[^\\p{L}\\p{N}]+", Pattern.UNICODE_CHARACTER_CLASS);
    private static final Pattern HAN = Pattern.compile("\\p{IsHan}");

    private final String[] names;

    FluidSearchText(String localized, String english, String registryName) {
        String local = normalize(localized);
        String englishName = normalize(english);
        String registry = normalize(registryName);
        if (!HAN.matcher(local).find()) {
            names = new String[] { local, englishName, registry };
            return;
        }
        StringBuilder pinyin = new StringBuilder(local.length() * 3);
        StringBuilder initials = new StringBuilder(local.length());
        for (int i = 0; i < local.length(); i++) {
            char character = local.charAt(i);
            String syllable = MandarinReadings.get(character);
            if (syllable != null) {
                pinyin.append(syllable);
                initials.append(syllable.charAt(0));
            } else if (character != ' ') {
                pinyin.append(character);
                initials.append(character);
            }
        }
        names = new String[] { local, englishName, registry, pinyin.toString(), initials.toString() };
    }

    static String normalize(String text) {
        return SEPARATORS.matcher(text.toLowerCase(Locale.ROOT).replace('ü', 'v')).replaceAll(" ").trim();
    }

    boolean matches(String[] terms) {
        for (String term : terms) {
            boolean found = false;
            for (String name : names) {
                if (name.contains(term)) {
                    found = true;
                    break;
                }
            }
            if (!found) return false;
        }
        return true;
    }
}
