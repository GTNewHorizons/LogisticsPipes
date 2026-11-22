package logisticspipes.gui.fluids;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;

import codechicken.nei.recipe.StackInfo;
import cpw.mods.fml.common.Loader;
import logisticspipes.utils.FluidIdentifier;
import logisticspipes.utils.gui.DummyContainer;

public final class FluidPicker {

    public static final int COLUMNS = 8;
    public static final int ROWS = 8;
    public static final int PAGE_SIZE = COLUMNS * ROWS;
    private static final Object[] NO_FORMAT_ARGS = new Object[0];

    private static List<Entry> index;
    private static WeakReference<NetHandlerPlayClient> connection = new WeakReference<>(null);
    private static int maxFluidId = -1;
    private static boolean listensForReloads;

    private final List<Entry> allFluids = getIndex();
    private List<Entry> results = allFluids;
    private String query = "";
    private int page;

    public static FluidStack getDraggedFluid(ItemStack stack) {
        if (stack == null) return null;
        if (Loader.isModLoaded("NotEnoughItems")) {
            FluidStack fluid = StackInfo.getFluid(stack);
            if (fluid != null && fluid.getFluid() != null) return fluid;
        }
        // NEI can list a placeable fluid block beside its fluid display with the same icon.
        if (stack.getItem() instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(stack.getItem());
            Fluid fluid = block instanceof IFluidBlock fluidBlock ? fluidBlock.getFluid()
                    : FluidRegistry.lookupFluidForBlock(block);
            if (fluid != null && FluidRegistry.getFluidName(fluid) != null) return new FluidStack(fluid, 1);
        }
        FluidIdentifier fluid = DummyContainer.getFluidForSlot(stack);
        return fluid == null ? null : fluid.makeFluidStack(1);
    }

    private static List<Entry> getIndex() {
        Minecraft client = Minecraft.getMinecraft();
        if (!listensForReloads && client.getResourceManager() instanceof IReloadableResourceManager manager) {
            manager.registerReloadListener(resources -> index = null);
            listensForReloads = true;
        }
        NetHandlerPlayClient currentConnection = client.getNetHandler();
        int maxId = FluidRegistry.getMaxID();
        if (index != null && connection.get() == currentConnection && maxFluidId == maxId) return index;

        net.minecraft.client.resources.Locale english = null;
        if (!"en_US".equals(client.getLanguageManager().getCurrentLanguage().getLanguageCode())) {
            english = new net.minecraft.client.resources.Locale();
            english.loadLocaleDataFiles(client.getResourceManager(), Collections.singletonList("en_US"));
        }
        List<Entry> entries = new ArrayList<>(maxId + 1);
        for (int id = 0; id <= maxId; id++) {
            Fluid fluid = FluidRegistry.getFluid(id);
            if (fluid == null) continue;
            FluidStack stack = new FluidStack(fluid, 1);
            String registryName = FluidRegistry.getFluidName(fluid);
            String localized = EnumChatFormatting.getTextWithoutFormattingCodes(fluid.getLocalizedName(stack));
            if (localized == null || localized.isEmpty()) localized = registryName;
            String englishName = english == null ? localized : getEnglishName(english, fluid.getUnlocalizedName());
            entries.add(new Entry(fluid, localized, new FluidSearchText(localized, englishName, registryName)));
        }
        connection = new WeakReference<>(currentConnection);
        maxFluidId = maxId;
        index = entries;
        return index;
    }

    private static String getEnglishName(net.minecraft.client.resources.Locale english, String key) {
        String name = english.formatMessage(key, NO_FORMAT_ARGS);
        if (!name.equals(key)) return name;
        String itemKey = key + ".name";
        name = english.formatMessage(itemKey, NO_FORMAT_ARGS);
        if (!name.equals(itemKey)) return name;
        name = StatCollector.translateToFallback(key);
        return name.equals(key) ? StatCollector.translateToFallback(itemKey) : name;
    }

    public void search(String text) {
        String next = FluidSearchText.normalize(text);
        if (next.equals(query)) return;
        if (next.isEmpty()) {
            results = allFluids;
        } else {
            // An appended character can only narrow the current results.
            List<Entry> candidates = next.startsWith(query) ? results : allFluids;
            String[] terms = next.split(" ");
            List<Entry> matches = new ArrayList<>();
            for (Entry entry : candidates) {
                if (entry.searchText.matches(terms)) matches.add(entry);
            }
            results = matches;
        }
        query = next;
        page = 0;
    }

    public Entry get(int cell) {
        int index = page * PAGE_SIZE + cell;
        return cell >= 0 && cell < PAGE_SIZE && index < results.size() ? results.get(index) : null;
    }

    public void changePage(int direction) {
        int pages = pageCount();
        if (pages > 0) page = Math.floorMod(page + direction, pages);
    }

    public int pageCount() {
        return (results.size() + PAGE_SIZE - 1) / PAGE_SIZE;
    }

    public String pageLabel() {
        return results.isEmpty() ? "0/0" : (page + 1) + "/" + pageCount();
    }

    public static final class Entry {

        private final Fluid fluid;
        private final String name;
        private final FluidSearchText searchText;
        private ItemStack icon;

        private Entry(Fluid fluid, String name, FluidSearchText searchText) {
            this.fluid = fluid;
            this.name = name;
            this.searchText = searchText;
        }

        public String name() {
            return name;
        }

        public FluidStack fluid() {
            return new FluidStack(fluid, 1);
        }

        public ItemStack icon() {
            if (icon == null) icon = FluidIdentifier.get(fluid, null, null).getItemIdentifier().makeNormalStack(1);
            return icon;
        }
    }
}
