package logisticspipes.nei;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import codechicken.nei.guihook.GuiContainerManager;
import cpw.mods.fml.common.Mod;
import logisticspipes.LogisticsPipes;
import logisticspipes.config.Configs;
import logisticspipes.gui.GuiLogisticsCraftingTable;
import logisticspipes.gui.GuiSolderingStation;
import logisticspipes.gui.orderer.GuiRequestTable;

public class NEILogisticsPipesConfig implements IConfigureNEI {

    public static boolean added = false;

    @Override
    public void loadConfig() {

        if (Configs.TOOLTIP_INFO && !NEILogisticsPipesConfig.added) {
            GuiContainerManager.addTooltipHandler(new DebugHelper());
            NEILogisticsPipesConfig.added = true;
        }

        GuiContainerManager.addDrawHandler(new DrawHandler());

        /*
         * MultiItemRange main = new MultiItemRange(); main.add(LogisticsPipes.LogisticsNetworkMonitior);
         * main.add(LogisticsPipes.LogisticsRemoteOrderer); main.add(LogisticsPipes.LogisticsCraftingSignCreator);
         * MultiItemRange pipesChassi = new MultiItemRange(); pipesChassi.add(LogisticsPipes.LogisticsChassisPipeMk1);
         * pipesChassi.add(LogisticsPipes.LogisticsChassisPipeMk2);
         * pipesChassi.add(LogisticsPipes.LogisticsChassisPipeMk3);
         * pipesChassi.add(LogisticsPipes.LogisticsChassisPipeMk4);
         * pipesChassi.add(LogisticsPipes.LogisticsChassisPipeMk5); MultiItemRange modules = new MultiItemRange();
         * modules.add(LogisticsPipes.ModuleItem, 0, 1000); addSetRange("LogisticsPipes", main);
         * addSetRange("LogisticsPipes.Modules", modules); //addSetRange("LogisticsPipes.Pipes", pipes);
         * addSetRange("LogisticsPipes.Pipes.Chassi", pipesChassi);
         */

        API.registerRecipeHandler(new NEISolderingStationRecipeManager());
        API.registerUsageHandler(new NEISolderingStationRecipeManager());
        API.registerGuiOverlay(GuiSolderingStation.class, "solderingstation");
        API.registerGuiOverlayHandler(
                GuiLogisticsCraftingTable.class,
                new LogisticsCraftingOverlayHandler(),
                "crafting");
        API.registerGuiOverlayHandler(GuiRequestTable.class, new LogisticsCraftingOverlayHandler(), "crafting");

        if (LogisticsPipes.isGTNH) {
            // unused stuff
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsPipeComponents, 1, OreDictionary.WILDCARD_VALUE));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 0));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 11));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsSolidBlock, 1, 12));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsPipeBlock, 1));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsParts, 1, 3));
            // electric stuff
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 30));
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 32));
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 33));
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 34));
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 35));
//            API.hideItem(new ItemStack(LogisticsPipes.UpgradeItem, 1, 36));
//            API.hideItem(new ItemStack(LogisticsPipes.ModuleItem, 1, 301));
            // This are used to connect 2 LP networks through chests and might lag, it is disabled for now
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsInvSysConPipe));
            // Should be added when Logistics Item Card is added
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsEntrancePipe));
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsDestinationPipe));
            // Hidden until someone can come up with a proper recipe idea
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsItemCard));

            // Fluid Transport Item
            API.hideItem(new ItemStack(LogisticsPipes.LogisticsFluidContainer));
        }
    }

    @Override
    public String getName() {
        return LogisticsPipes.class.getAnnotation(Mod.class).name();
    }

    @Override
    public String getVersion() {
        return LogisticsPipes.class.getAnnotation(Mod.class).version();
    }
}
