package logisticspipes.recipes;

import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPED;
import static net.minecraftforge.oredict.RecipeSorter.Category.SHAPELESS;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.ShapelessOreRecipe;

import logisticspipes.LogisticsPipes;
import logisticspipes.blocks.LogisticsSolidBlock;
import logisticspipes.config.Configs;
import logisticspipes.items.ItemModule;
import logisticspipes.items.ItemPipeComponents;
import logisticspipes.items.ItemUpgrade;
import logisticspipes.items.RemoteOrderer;
import logisticspipes.proxy.interfaces.ICraftingParts;

// @formatter:off
// CHECKSTYLE:OFF

public class RecipeManager {
    public static class LocalCraftingManager {
        private final CraftingManager craftingManager = CraftingManager.getInstance();

        public LocalCraftingManager() {}

        public void addRecipe(ItemStack stack, CraftingDependency dependent, Object... objects) {
            //			craftingManager.getRecipeList().add(new LPShapedOreRecipe(stack, dependent, objects));
        }

        public void addOrdererRecipe(ItemStack stack, String dye, ItemStack orderer) {
            //			craftingManager.getRecipeList().add(new ShapelessOrdererRecipe(stack, new Object[] {dye, orderer}));
        }

        public void addShapelessRecipe(ItemStack stack, CraftingDependency dependent, Object... objects) {
            //			craftingManager.getRecipeList().add(new LPShapelessOreRecipe(stack, dependent, objects));
        }

        @SuppressWarnings("unchecked")
        public void addShapelessResetRecipe(Item item, int meta) {
            craftingManager.getRecipeList().add(new ShapelessResetRecipe(item, meta));
        }

        public static class ShapelessOrdererRecipe extends ShapelessOreRecipe {
            public ShapelessOrdererRecipe(ItemStack result, Object... recipe) {
                super(result, recipe);
            }

            @Override
            public ItemStack getCraftingResult(InventoryCrafting var1) {
                ItemStack result = super.getCraftingResult(var1);
                for (int i = 0; i < var1.getInventoryStackLimit(); i++) {
                    ItemStack stack = var1.getStackInSlot(i);
                    if (stack != null && stack.getItem() instanceof RemoteOrderer) {
                        result.setTagCompound(stack.getTagCompound());
                        break;
                    }
                }
                return result;
            }
        }
    }

    public static LocalCraftingManager craftingManager = new LocalCraftingManager();

    public static void registerRecipeClasses() {
        RecipeSorter.register(
                "logisticspipes:shapedore",
                LPShapedOreRecipe.class,
                SHAPED,
                "after:minecraft:shaped before:minecraft:shapeless");
        RecipeSorter.register(
                "logisticspipes:shapelessore", LPShapelessOreRecipe.class, SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register(
                "logisticspipes:shapelessreset", ShapelessResetRecipe.class, SHAPELESS, "after:minecraft:shapeless");
        RecipeSorter.register(
                "logisticspipes:shapelessorderer",
                LocalCraftingManager.ShapelessOrdererRecipe.class,
                SHAPELESS,
                "after:minecraft:shapeless");
    }

    public static void loadRecipes(ICraftingParts parts) {

        String[] dyes = {
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"
        };
        if (!Configs.ENABLE_BETA_RECIPES) {
            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.BasicTransportPipe, 8),
                    CraftingDependency.Basic,
                    "IgI",
                    " r ",
                    'g',
                    new ItemStack(Blocks.glass_pane, 1),
                    'I',
                    Items.iron_ingot,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsBasicPipe, 8),
                    CraftingDependency.Basic,
                    "grg",
                    "cdc",
                    " G ",
                    'G',
                    parts.getChipTear2(),
                    'g',
                    Blocks.glass,
                    'd',
                    parts.getSortingLogic(),
                    'c',
                    parts.getBasicTransport(),
                    'r',
                    Blocks.redstone_torch);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsBasicPipe, 8),
                    CraftingDependency.Basic,
                    "grg",
                    "cdc",
                    " G ",
                    'G',
                    parts.getGearTear2(),
                    'g',
                    Blocks.glass,
                    'd',
                    parts.getSortingLogic(),
                    'c',
                    parts.getBasicTransport(),
                    'r',
                    Blocks.redstone_torch);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk1, 1),
                    CraftingDependency.Basic,
                    " G ",
                    "rPr",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk1, 1),
                    CraftingDependency.Basic,
                    "G",
                    "P",
                    "R",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'G',
                    parts.getChipTear2(),
                    'R',
                    Blocks.redstone_torch);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'U',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'U',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk1, 1),
                    CraftingDependency.Basic,
                    "r",
                    "P",
                    "S",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'S',
                    "gearStone",
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSatellitePipe, 1),
                    CraftingDependency.DistanceRequest,
                    "rPr",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSupplierPipe, 1),
                    CraftingDependency.DistanceRequest,
                    "lPl",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'l',
                    "dyeBlue");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk1, 1),
                    CraftingDependency.Basic,
                    "g",
                    "P",
                    "i",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'g',
                    parts.getGearTear2(),
                    'i',
                    parts.getGearTear1());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk1, 1),
                    CraftingDependency.Basic,
                    "g",
                    "P",
                    "i",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'g',
                    parts.getChipTear2(),
                    'i',
                    parts.getGearTear1());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    "r",
                    'B',
                    LogisticsPipes.LogisticsRequestPipeMk1,
                    'U',
                    parts.getGearTear3(),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsRequestPipeMk1,
                    'U',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    "r",
                    'B',
                    LogisticsPipes.LogisticsCraftingPipeMk1,
                    'U',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsCraftingPipeMk1,
                    'U',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRemoteOrdererPipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    "r",
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'U',
                    Items.ender_pearl,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsInvSysConPipe, 1),
                    CraftingDependency.Passthrough,
                    " E ",
                    "rPr",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'E',
                    Items.ender_pearl,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsEntrancePipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'U',
                    "dyeGreen");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsDestinationPipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'U',
                    "dyeRed");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsItemDisk, 1),
                    CraftingDependency.Fast_Crafting,
                    "igi",
                    "grg",
                    "igi",
                    'i',
                    "dyeBlack",
                    'r',
                    Items.redstone,
                    'g',
                    Items.gold_nugget);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK),
                    CraftingDependency.Modular_Pipes,
                    " p ",
                    "rpr",
                    " g ",
                    'p',
                    Items.paper,
                    'r',
                    Items.redstone,
                    'g',
                    Items.gold_nugget);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeGreen",
                    'G',
                    parts.getGearTear1(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    " B ",
                    'C',
                    "dyeGreen",
                    'G',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeRed",
                    'G',
                    parts.getGearTear1(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    " B ",
                    'C',
                    "dyeRed",
                    'G',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ACTIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    " G ",
                    "rBr",
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ACTIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    "G",
                    "B",
                    'G',
                    parts.getChipTear2(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getGearTear1(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    " B ",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR),
                    CraftingDependency.Active_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    'U',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    'U',
                    parts.getGearTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    'U',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR),
                    'U',
                    parts.getGearTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    'U',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR),
                    'U',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    'U',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    'U',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    'U',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    'U',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3),
                    'U',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.POLYMORPHIC_ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeOrange",
                    'G',
                    parts.getGearTear1(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.POLYMORPHIC_ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    " B ",
                    'C',
                    "dyeOrange",
                    'G',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.QUICKSORT),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getGearTear3(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.QUICKSORT),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    " B ",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getChipTear3(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.TERMINUS),
                    CraftingDependency.Modular_Pipes,
                    "CGD",
                    "rBr",
                    'C',
                    "dyeBlack",
                    'D',
                    "dyePurple",
                    'G',
                    parts.getGearTear1(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.TERMINUS),
                    CraftingDependency.Modular_Pipes,
                    "CGD",
                    " B ",
                    'C',
                    "dyeBlack",
                    'D',
                    "dyePurple",
                    'G',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    " B ",
                    'C',
                    "dyeBlue",
                    'G',
                    parts.getChipTear2(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    'U',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    'U',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    CraftingDependency.Modular_Pipes,
                    "rGR",
                    " b ",
                    "B r",
                    'R',
                    "dyeRed",
                    'B',
                    "dyeBlue",
                    'G',
                    parts.getGearTear1(),
                    'b',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    CraftingDependency.Modular_Pipes,
                    " GR",
                    " b ",
                    "B  ",
                    'R',
                    "dyeRed",
                    'B',
                    "dyeBlue",
                    'G',
                    parts.getChipTear1(),
                    'b',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    parts.getGearTear2());

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    parts.getChipTear2());

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK3),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2),
                    new ItemStack(LogisticsPipes.LogisticsParts, 1, 3));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.MODBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    'U',
                    parts.getGearTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.MODBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    'U',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.OREDICTITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.MODBASEDITEMSINK),
                    'U',
                    Items.book);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CREATIVETABBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.OREDICTITEMSINK),
                    'U',
                    parts.getGearTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CREATIVETABBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.OREDICTITEMSINK),
                    'U',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK),
                    CraftingDependency.Sink_Modules,
                    "E",
                    "B",
                    'E',
                    Items.enchanted_book,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK_MK2),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'U',
                    parts.getChipTear2(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK));
            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK_MK2),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'U',
                    parts.getGearTear2(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk1, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "uPu",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'u',
                    Items.iron_ingot,
                    'i',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk2, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk2, 1),
                    CraftingDependency.Modular_Pipes,
                    " i ",
                    "uPu",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'u',
                    Items.iron_ingot,
                    'i',
                    parts.getChipTear1());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk3, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    "iii",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk3, 1),
                    CraftingDependency.Modular_Pipes,
                    " i ",
                    "uPu",
                    " i ",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'u',
                    Items.iron_ingot,
                    'i',
                    parts.getChipTear1());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk4, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    "ggg",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot,
                    'g',
                    Items.gold_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk4, 1),
                    CraftingDependency.Modular_Pipes,
                    " i ",
                    "uPu",
                    " g ",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'u',
                    Items.iron_ingot,
                    'i',
                    parts.getChipTear1(),
                    'g',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk5, 1),
                    CraftingDependency.Large_Chasie,
                    "d",
                    "P",
                    'P',
                    LogisticsPipes.LogisticsChassisPipeMk4,
                    'd',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeControllerItem, 1),
                    CraftingDependency.Basic,
                    "g g",
                    " G ",
                    " g ",
                    'g',
                    Items.gold_ingot,
                    'G',
                    parts.getGearTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeControllerItem, 1),
                    CraftingDependency.Basic,
                    "g g",
                    " G ",
                    " g ",
                    'g',
                    Items.gold_ingot,
                    'G',
                    parts.getChipTear2());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRemoteOrderer, 1, 0),
                    CraftingDependency.DistanceRequest,
                    "gg",
                    "gg",
                    "DD",
                    'g',
                    Blocks.glass,
                    'D',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRemoteOrderer, 1, 0),
                    CraftingDependency.DistanceRequest,
                    "gg",
                    "gg",
                    "DD",
                    'g',
                    Blocks.glass,
                    'D',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingSignCreator, 1),
                    CraftingDependency.Information_System,
                    "G G",
                    " S ",
                    " D ",
                    'G',
                    parts.getGearTear2(),
                    'S',
                    Items.sign,
                    'D',
                    parts.getGearTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingSignCreator, 1),
                    CraftingDependency.Information_System,
                    "G G",
                    " S ",
                    " D ",
                    'G',
                    parts.getChipTear2(),
                    'S',
                    Items.sign,
                    'D',
                    parts.getChipTear3());

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 0),
                    CraftingDependency.Basic,
                    "iCi",
                    "i i",
                    "iri",
                    'C',
                    new ItemStack(Blocks.crafting_table, 1),
                    'r',
                    Items.redstone,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 1),
                    CraftingDependency.Basic,
                    "iii",
                    "rRr",
                    "iii",
                    'R',
                    Blocks.redstone_block,
                    'r',
                    Items.redstone,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 2),
                    CraftingDependency.Security,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    parts.getGearTear3(),
                    'r',
                    Items.redstone,
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 2),
                    CraftingDependency.Security,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    parts.getChipTear3(),
                    'r',
                    Items.redstone,
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 3),
                    CraftingDependency.Basic,
                    "wCw",
                    " G ",
                    "wSw",
                    'w',
                    "plankWood",
                    'C',
                    Blocks.crafting_table,
                    'S',
                    Blocks.chest,
                    'G',
                    "gearStone");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_FUZZYCRAFTING_TABLE),
                    CraftingDependency.Basic,
                    "Q",
                    "T",
                    'T',
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_AUTOCRAFTING_TABLE),
                    'Q',
                    Items.quartz);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_STATISTICS_TABLE),
                    CraftingDependency.Advanced_Information,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    parts.getGearTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_STATISTICS_TABLE),
                    CraftingDependency.Advanced_Information,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    parts.getChipTear2(),
                    'r',
                    Items.redstone,
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 0),
                    CraftingDependency.Upgrades,
                    false,
                    "srs",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 1),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "srs",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 2),
                    CraftingDependency.Upgrades,
                    false,
                    "PsP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 3),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PsP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 4),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "sCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 5),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCs",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 6),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 20),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.gold_ingot,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 21),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear2(),
                    'r',
                    Items.iron_ingot,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 22),
                    CraftingDependency.Active_Liquid,
                    false,
                    "RbR",
                    "bCb",
                    "RbR",
                    'C',
                    parts.getChipTear2(),
                    'R',
                    Items.redstone,
                    'b',
                    Items.glass_bottle);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 23),
                    CraftingDependency.Upgrades,
                    false,
                    "RgR",
                    "gCg",
                    "RgR",
                    'C',
                    parts.getChipTear1(),
                    'R',
                    Items.redstone,
                    'g',
                    "gearWood");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 10),
                    CraftingDependency.Upgrades,
                    false,
                    "srs",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 11),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "srs",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 12),
                    CraftingDependency.Upgrades,
                    false,
                    "PsP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 13),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PsP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 14),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "sCr",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 15),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCs",
                    "PrP",
                    'C',
                    parts.getChipTear1(),
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 24),
                    CraftingDependency.Upgrades,
                    false,
                    "Rhy",
                    "iCi",
                    "riR",
                    'C',
                    parts.getChipTear1(),
                    'R',
                    Items.redstone,
                    'r',
                    "dyeRed",
                    'y',
                    "dyeYellow",
                    'h',
                    Blocks.hopper,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 25),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    parts.getChipTear2(),
                    'r',
                    Items.quartz,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 4, ItemUpgrade.POWER_TRANSPORTATION),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PRP",
                    "CGC",
                    "PLP",
                    'C',
                    parts.getChipTear1(),
                    'R',
                    Blocks.redstone_block,
                    'G',
                    Blocks.glowstone,
                    'L',
                    Blocks.lapis_block,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_MONITORING),
                    CraftingDependency.Upgrades,
                    false,
                    "RLR",
                    "aCb",
                    "RPR",
                    'C',
                    parts.getChipTear3(),
                    'P',
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk2, 1, 0),
                    'R',
                    Items.redstone,
                    'L',
                    "dyeBlue",
                    'a',
                    "dyeGreen",
                    'b',
                    "dyeYellow");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.OPAQUE_UPGRADE),
                    CraftingDependency.Upgrades,
                    false,
                    "RbR",
                    "bCb",
                    "RbR",
                    'C',
                    parts.getChipTear1(),
                    'R',
                    Items.redstone,
                    'b',
                    "dyeWhite");

            /*
             * added by Chaos234  - Date: 20150620
             */
            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    parts.getChipTear1(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    parts.getChipTear2(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    parts.getChipTear3(),
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR));

            /* add end */

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidBasicPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    "w",
                    "B",
                    "b",
                    'B',
                    LogisticsPipes.LogisticsBasicPipe,
                    'w',
                    parts.getWaterProof(),
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSupplierPipeMk1, 1),
                    CraftingDependency.DistanceRequest,
                    "lPl",
                    " B ",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsBasicPipe,
                    'B',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSatellitePipe, 1),
                    CraftingDependency.Active_Liquid,
                    "rLr",
                    'L',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSupplierPipeMk2, 1),
                    CraftingDependency.Active_Liquid,
                    " g ",
                    "lPl",
                    " g ",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'g',
                    Items.gold_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidInsertionPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    " g ",
                    "gLg",
                    " g ",
                    'L',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'g',
                    Items.glass_bottle);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidProviderPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    "g",
                    "L",
                    'L',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'g',
                    Items.glass_bottle);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidRequestPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    "gLg",
                    'L',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'g',
                    Items.glass_bottle);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidExtractorPipe, 1),
                    CraftingDependency.Active_Liquid,
                    "w",
                    "I",
                    'I',
                    LogisticsPipes.LogisticsFluidInsertionPipe,
                    'w',
                    parts.getExtractorFluid());
        }
        if (Configs.ENABLE_BETA_RECIPES) {
            ItemStack micserv =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_MICROSERVO);
            ItemStack logproc =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_ROUTEPROCESSOR);
            ItemStack packager =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_MICROPACKAGER);
            ItemStack capsler =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_MICROCAPSULATOR);
            ItemStack expand =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_LOGICEXPANDER);
            ItemStack lense =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_FOCUSLENSE);
            ItemStack accept =
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_POWERACCEPT);
            ItemStack basic = new ItemStack(LogisticsPipes.BasicTransportPipe, 1);
            ItemStack pipe = new ItemStack(LogisticsPipes.LogisticsBasicPipe, 1);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_PIPESTRUCTURE),
                    CraftingDependency.Basic,
                    "I I",
                    "   ",
                    "I I",
                    'I',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.BasicTransportPipe, 8),
                    CraftingDependency.Basic,
                    "gSg",
                    " r ",
                    'g',
                    new ItemStack(Blocks.glass_pane, 1),
                    'S',
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_PIPESTRUCTURE),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 8, ItemPipeComponents.ITEM_MICROSERVO),
                    CraftingDependency.Basic,
                    "IrI",
                    "rIr",
                    "IrI",
                    'I',
                    Items.iron_ingot,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 8, ItemPipeComponents.ITEM_MICROPACKAGER),
                    CraftingDependency.Basic,
                    "rPr",
                    "I I",
                    "IrI",
                    'P',
                    new ItemStack(Blocks.sticky_piston, 1),
                    'I',
                    Items.iron_ingot,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 8, ItemPipeComponents.ITEM_MICROCAPSULATOR),
                    CraftingDependency.Basic,
                    "rPr",
                    "IGI",
                    "IrI",
                    'P',
                    new ItemStack(Blocks.sticky_piston, 1),
                    'G',
                    Items.glass_bottle,
                    'I',
                    Items.iron_ingot,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_ROUTEPROCESSOR),
                    CraftingDependency.Basic,
                    "nrn",
                    "rDr",
                    "nrn",
                    'n',
                    Items.gold_nugget,
                    'D',
                    Items.diamond,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 2, ItemPipeComponents.ITEM_LOGICEXPANDER),
                    CraftingDependency.Basic,
                    "nrn",
                    "rIr",
                    "nrn",
                    'n',
                    Items.gold_nugget,
                    'I',
                    Items.iron_ingot,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 2, ItemPipeComponents.ITEM_FOCUSLENSE),
                    CraftingDependency.Basic,
                    " g ",
                    "ggg",
                    " g ",
                    'g',
                    Blocks.glass);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, ItemPipeComponents.ITEM_POWERACCEPT),
                    CraftingDependency.Basic,
                    "R  ",
                    "LRI",
                    "RII",
                    'L',
                    lense,
                    'I',
                    "nuggetIron",
                    'R',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsBasicPipe, 8),
                    CraftingDependency.Basic,
                    "ppp",
                    "plp",
                    "ppp",
                    'l',
                    logproc,
                    'p',
                    basic);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk1, 1),
                    CraftingDependency.Basic,
                    " p ",
                    "rPr",
                    " m ",
                    'P',
                    pipe,
                    'p',
                    packager,
                    'm',
                    micserv,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsProviderPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk1, 1),
                    CraftingDependency.Basic,
                    " r ",
                    "pPm",
                    'P',
                    pipe,
                    'p',
                    packager,
                    'm',
                    micserv,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSatellitePipe, 1),
                    CraftingDependency.DistanceRequest,
                    " y ",
                    "rPr",
                    " p ",
                    'y',
                    "dyeYellow",
                    'P',
                    pipe,
                    'r',
                    Items.redstone,
                    'p',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSupplierPipe, 1),
                    CraftingDependency.DistanceRequest,
                    "rPr",
                    " p ",
                    'r',
                    "dyeBlue",
                    'P',
                    pipe,
                    'p',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk1, 1),
                    CraftingDependency.Basic,
                    "g",
                    "P",
                    "i",
                    'P',
                    pipe,
                    'g',
                    logproc,
                    'i',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRequestPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    "r",
                    'B',
                    LogisticsPipes.LogisticsRequestPipeMk1,
                    'U',
                    logproc,
                    'r',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk2, 1),
                    CraftingDependency.Fast_Crafting,
                    "U",
                    "B",
                    "r",
                    'B',
                    LogisticsPipes.LogisticsCraftingPipeMk1,
                    'U',
                    expand,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRemoteOrdererPipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    "r",
                    'B',
                    pipe,
                    'U',
                    Items.ender_pearl,
                    'r',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsInvSysConPipe, 1),
                    CraftingDependency.Passthrough,
                    " E ",
                    "rPr",
                    " p ",
                    'P',
                    pipe,
                    'E',
                    Items.ender_pearl,
                    'p',
                    packager,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsEntrancePipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    "P",
                    'U',
                    "dyeGreen",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'P',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsDestinationPipe, 1),
                    CraftingDependency.Passthrough,
                    "U",
                    "B",
                    "P",
                    'U',
                    "dyeRed",
                    'B',
                    LogisticsPipes.LogisticsProviderPipeMk1,
                    'P',
                    packager);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsItemDisk, 1),
                    CraftingDependency.Fast_Crafting,
                    "igi",
                    "grg",
                    "igi",
                    'i',
                    "dyeBlack",
                    'r',
                    Items.redstone,
                    'g',
                    Items.gold_nugget);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK),
                    CraftingDependency.Modular_Pipes,
                    " p ",
                    "rpr",
                    " g ",
                    'p',
                    Items.paper,
                    'r',
                    Items.redstone,
                    'g',
                    Items.gold_nugget);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeGreen",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeRed",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ACTIVE_SUPPLIER),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PASSIVE_SUPPLIER),
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    "rBr",
                    " s ",
                    'C',
                    "dyeBlue",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    's',
                    micserv,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR),
                    CraftingDependency.Active_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR),
                    'U',
                    micserv);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR),
                    'U',
                    micserv);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2),
                    'U',
                    micserv);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK2),
                    'U',
                    micserv);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ADVANCED_EXTRACTOR_MK3),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.POLYMORPHIC_ITEMSINK),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeOrange",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.QUICKSORT),
                    CraftingDependency.Active_Modules,
                    "CGC",
                    "rBr",
                    " P ",
                    'C',
                    "dyeBlue",
                    'G',
                    logproc,
                    'P',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.TERMINUS),
                    CraftingDependency.Modular_Pipes,
                    "CGD",
                    "rBr",
                    'C',
                    "dyeBlack",
                    'D',
                    "dyePurple",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    CraftingDependency.Modular_Pipes,
                    "CGC",
                    "rBr",
                    'C',
                    "dyeBlue",
                    'G',
                    packager,
                    'r',
                    Items.redstone,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER_MK2),
                    CraftingDependency.High_Tech_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.PROVIDER),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    CraftingDependency.Modular_Pipes,
                    "rGR",
                    " B ",
                    "D r",
                    'R',
                    "dyeRed",
                    'D',
                    "dyeBlue",
                    'G',
                    packager,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.BLANK),
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER),
                    expand);

            RecipeManager.craftingManager.addShapelessRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK3),
                    CraftingDependency.Modular_Pipes,
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CRAFTER_MK2),
                    new ItemStack(LogisticsPipes.LogisticsParts, 1, 3));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.MODBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.OREDICTITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.MODBASEDITEMSINK),
                    'U',
                    Items.book);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.CREATIVETABBASEDITEMSINK),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.OREDICTITEMSINK),
                    'U',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK),
                    CraftingDependency.Sink_Modules,
                    "E",
                    "B",
                    'E',
                    Items.enchanted_book,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ITEMSINK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK_MK2),
                    CraftingDependency.Sink_Modules,
                    "U",
                    "B",
                    'U',
                    expand,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.ENCHANTMENTSINK));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk1, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "uPu",
                    'P',
                    pipe,
                    'u',
                    Items.iron_ingot,
                    'i',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk2, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    'P',
                    pipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk3, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    "iii",
                    'P',
                    pipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk4, 1),
                    CraftingDependency.Modular_Pipes,
                    "iii",
                    "iPi",
                    "ggg",
                    'P',
                    pipe,
                    'i',
                    Items.iron_ingot,
                    'g',
                    Items.gold_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsChassisPipeMk5, 1),
                    CraftingDependency.Large_Chasie,
                    "d",
                    "P",
                    'P',
                    LogisticsPipes.LogisticsChassisPipeMk4,
                    'd',
                    logproc);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsPipeControllerItem, 1),
                    CraftingDependency.Basic,
                    "g g",
                    " G ",
                    " g ",
                    'g',
                    Items.gold_ingot,
                    'G',
                    logproc);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsRemoteOrderer, 1, 0),
                    CraftingDependency.DistanceRequest,
                    "gg",
                    "gg",
                    "DD",
                    'g',
                    Blocks.glass,
                    'D',
                    logproc);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsCraftingSignCreator, 1),
                    CraftingDependency.Information_System,
                    "G G",
                    " S ",
                    " D ",
                    'G',
                    logproc,
                    'S',
                    Items.sign,
                    'D',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.SOLDERING_STATION),
                    CraftingDependency.Basic,
                    "iCi",
                    "i i",
                    "iri",
                    'C',
                    new ItemStack(Blocks.crafting_table, 1),
                    'r',
                    Items.redstone,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_POWER_JUNCTION),
                    CraftingDependency.Basic,
                    "iii",
                    "rRr",
                    "iii",
                    'R',
                    Blocks.redstone_block,
                    'r',
                    Items.redstone,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_SECURITY_STATION),
                    CraftingDependency.Security,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    logproc,
                    'r',
                    Items.redstone,
                    'B',
                    pipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_AUTOCRAFTING_TABLE),
                    CraftingDependency.Basic,
                    "wCw",
                    " G ",
                    "wSw",
                    'w',
                    "plankWood",
                    'C',
                    Blocks.crafting_table,
                    'S',
                    Blocks.chest,
                    'G',
                    expand);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_FUZZYCRAFTING_TABLE),
                    CraftingDependency.Basic,
                    "Q",
                    "T",
                    'T',
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_AUTOCRAFTING_TABLE),
                    'Q',
                    Items.quartz);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(
                            LogisticsPipes.LogisticsSolidBlock, 1, LogisticsSolidBlock.LOGISTICS_STATISTICS_TABLE),
                    CraftingDependency.Advanced_Information,
                    "iDi",
                    "rBr",
                    "iii",
                    'D',
                    Items.gold_ingot,
                    'r',
                    Items.redstone,
                    'B',
                    pipe,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 0),
                    CraftingDependency.Upgrades,
                    false,
                    "srs",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 1),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "srs",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 2),
                    CraftingDependency.Upgrades,
                    false,
                    "PsP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 3),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PsP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 4),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "sCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 5),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCs",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    "slimeball");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 6),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 20),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.gold_ingot,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 21),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.iron_ingot,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 22),
                    CraftingDependency.Active_Liquid,
                    false,
                    "RbR",
                    "bCb",
                    "RbR",
                    'C',
                    capsler,
                    'R',
                    Items.redstone,
                    'b',
                    Items.glass_bottle);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 23),
                    CraftingDependency.Upgrades,
                    false,
                    "RgR",
                    "gCg",
                    "RgR",
                    'C',
                    expand,
                    'R',
                    Items.redstone,
                    'g',
                    "plankWood");

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 10),
                    CraftingDependency.Upgrades,
                    false,
                    "srs",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 11),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "srs",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 12),
                    CraftingDependency.Upgrades,
                    false,
                    "PsP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 13),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PsP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 14),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "sCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 15),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCs",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.redstone,
                    'P',
                    Items.paper,
                    's',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 24),
                    CraftingDependency.Upgrades,
                    false,
                    "Rhy",
                    "iCi",
                    "riR",
                    'r',
                    "dyeRed",
                    'y',
                    "dyeYellow",
                    'C',
                    expand,
                    'R',
                    Items.redstone,
                    'h',
                    Blocks.hopper,
                    'i',
                    Items.iron_ingot);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, 25),
                    CraftingDependency.Upgrades,
                    false,
                    "PrP",
                    "rCr",
                    "PrP",
                    'C',
                    expand,
                    'r',
                    Items.quartz,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 4, ItemUpgrade.POWER_TRANSPORTATION),
                    CraftingDependency.Power_Distribution,
                    false,
                    "PGP",
                    "RCR",
                    "PRP",
                    'C',
                    expand,
                    'R',
                    lense,
                    'G',
                    Blocks.glowstone,
                    'P',
                    Items.paper);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_MONITORING),
                    CraftingDependency.Upgrades,
                    false,
                    "RLR",
                    "aCb",
                    "RPR",
                    'L',
                    "dyeBlue",
                    'a',
                    "dyeGreen",
                    'b',
                    "dyeYellow",
                    'C',
                    logproc,
                    'P',
                    new ItemStack(LogisticsPipes.LogisticsCraftingPipeMk2, 1, 0),
                    'R',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.OPAQUE_UPGRADE),
                    CraftingDependency.Upgrades,
                    false,
                    " b ",
                    "bIb",
                    " b ",
                    'b',
                    "dyeWhite",
                    'I',
                    Items.iron_ingot);

            /*
             * added by Chaos234  - Date: 20150620
             */
            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    micserv,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK3));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    micserv,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR_MK2));

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.UpgradeItem, 1, ItemUpgrade.CRAFTING_CLEANUP),
                    CraftingDependency.Upgrades,
                    false,
                    "rRr",
                    "PCP",
                    "rBr",
                    'r',
                    Items.redstone,
                    'R',
                    "dyeRed",
                    'P',
                    Items.paper,
                    'C',
                    micserv,
                    'B',
                    new ItemStack(LogisticsPipes.ModuleItem, 1, ItemModule.EXTRACTOR));

            /* add end */

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidBasicPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    " l ",
                    "lPl",
                    " l ",
                    'l',
                    "dyeBlue",
                    'P',
                    pipe);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSupplierPipeMk1, 1),
                    CraftingDependency.DistanceRequest,
                    "lPl",
                    " b ",
                    'l',
                    "dyeBlue",
                    'P',
                    pipe,
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSatellitePipe, 1),
                    CraftingDependency.Active_Liquid,
                    " r ",
                    "rPr",
                    " c ",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'c',
                    capsler,
                    'r',
                    Items.redstone);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidSupplierPipeMk2, 1),
                    CraftingDependency.Active_Liquid,
                    " l ",
                    "cPb",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'c',
                    capsler,
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidInsertionPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    " c ",
                    "lPl",
                    " b ",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'c',
                    capsler,
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidProviderPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    " b ",
                    "lPl",
                    " c ",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'c',
                    capsler,
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidRequestPipe, 1),
                    CraftingDependency.Basic_Liquid,
                    " l ",
                    "bPc",
                    'l',
                    "dyeBlue",
                    'P',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'c',
                    capsler,
                    'b',
                    Items.bucket);

            RecipeManager.craftingManager.addRecipe(
                    new ItemStack(LogisticsPipes.LogisticsFluidExtractorPipe, 1),
                    CraftingDependency.Active_Liquid,
                    "w",
                    "I",
                    "c",
                    'I',
                    LogisticsPipes.LogisticsFluidBasicPipe,
                    'w',
                    micserv,
                    'c',
                    capsler);
        }

        for (int moduleId : LogisticsPipes.ModuleItem.getRegisteredModulesIDs()) {
            RecipeManager.craftingManager.addShapelessResetRecipe(LogisticsPipes.ModuleItem, moduleId);
        }

        //        for (int i = 1; i < 17; i++) {
        //            RecipeManager.craftingManager.addOrdererRecipe(
        //                    new ItemStack(LogisticsPipes.LogisticsRemoteOrderer, 1, i),
        //                    dyes[i - 1],
        //                    new ItemStack(LogisticsPipes.LogisticsRemoteOrderer, 1, -1));
        //            RecipeManager.craftingManager.addShapelessResetRecipe(LogisticsPipes.LogisticsRemoteOrderer, i);
        //        }
        //        RecipeManager.craftingManager.addShapelessResetRecipe(LogisticsPipes.LogisticsRemoteOrderer, 0);
    }
}
