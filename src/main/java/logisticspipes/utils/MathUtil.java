package logisticspipes.utils;

import org.jetbrains.annotations.NotNull;

public class MathUtil {
    /**
     * The minimum of the given values.
     * @param values The values to compare.
     * @return The minimum value.
     */
    public static double min(double @NotNull ... values) {
        double min = Double.MAX_VALUE;
        for (double d : values) {
            min = Math.min(min, d);
        }
        return min;
    }
}
