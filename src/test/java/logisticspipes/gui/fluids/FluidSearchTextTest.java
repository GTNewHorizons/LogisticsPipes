package logisticspipes.gui.fluids;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class FluidSearchTextTest {

    @Test
    void findsLocalizedNamesFullPinyinAndInitials() {
        FluidSearchText acid = new FluidSearchText("硫酸", "Sulfuric Acid", "sulfuricacid");
        assertTrue(matches(acid, "硫酸"));
        assertTrue(matches(acid, "liusuan"));
        assertTrue(matches(acid, "LS"));
        assertTrue(matches(acid, "liu suan"));
    }

    @Test
    void searchesEnglishAndRegistryKeysAlongsidePinyin() {
        FluidSearchText aluminium = new FluidSearchText("熔融铝", "Molten Aluminium", "molten.aluminium");
        assertTrue(matches(aluminium, "  MOLTEN Aluminium  "));
        assertTrue(matches(aluminium, "molten.aluminium"));
        assertTrue(matches(aluminium, "rongronglü"));
        assertTrue(matches(aluminium, "aluminium rrl"));
        assertFalse(matches(aluminium, "molten iron"));
    }

    @Test
    void retainsLatinTextAndNumbersInMixedNames() {
        FluidSearchText coolant = new FluidSearchText("IC2 冷却液", "IC2 Coolant", "ic2coolant");
        assertTrue(matches(coolant, "ic2 lqy"));
        assertTrue(matches(coolant, "lengqueye"));
        assertTrue(matches(coolant, "ic2 coolant"));
        assertFalse(matches(coolant, "ic3"));
    }

    private static boolean matches(FluidSearchText fluid, String query) {
        return fluid.matches(FluidSearchText.normalize(query).split(" "));
    }
}
