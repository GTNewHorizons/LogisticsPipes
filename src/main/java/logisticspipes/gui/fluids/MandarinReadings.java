package logisticspipes.gui.fluids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;

final class MandarinReadings {

    private static final String RESOURCE = "/assets/logisticspipes/search/mandarin.txt";
    private static final String[] READINGS = load();

    private MandarinReadings() {}

    static String get(char character) {
        return READINGS[character];
    }

    private static String[] load() {
        InputStream input = MandarinReadings.class.getResourceAsStream(RESOURCE);
        if (input == null) throw new IllegalStateException("Missing resource " + RESOURCE);
        String[] readings = new String[Character.MAX_VALUE + 1];
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#")) continue;
                int separator = line.indexOf(' ');
                String reading = line.substring(0, separator);
                for (int i = separator + 1; i < line.length(); i++) {
                    readings[line.charAt(i)] = reading;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Could not load " + RESOURCE, e);
        }
        return readings;
    }
}
