package logisticspipes.proxy.buildcraft.gates;

import java.util.ArrayList;

import net.minecraft.item.ItemStack;

import buildcraft.api.statements.IStatementParameter;
import buildcraft.api.statements.ITriggerInternal;
import buildcraft.transport.Pipe;
import logisticspipes.pipes.basic.CoreRoutedPipe;
import logisticspipes.proxy.SimpleServiceLocator;
import logisticspipes.proxy.buildcraft.subproxies.LPBCPipe;
import logisticspipes.textures.provider.LPActionTriggerIconProvider;
import logisticspipes.utils.item.ItemIdentifier;

public class TriggerHasDestination extends LPTrigger implements ITriggerInternal {

    public TriggerHasDestination() {
        super("LogisticsPipes:trigger.hasDestination");
    }

    @Override
    public int getIconIndex() {
        return LPActionTriggerIconProvider.triggerHasDestinationIconIndex;
    }

    @Override
    public String getDescription() {
        return "Item has destination";
    }

    @Override
    public boolean isTriggerActive(Pipe pipe, IStatementParameter parameter) {
        if (pipe instanceof LPBCPipe) {
            if (((LPBCPipe) pipe).pipe.pipe instanceof CoreRoutedPipe) {
                if (parameter != null && parameter.getItemStack() != null) {
                    ItemStack item = parameter.getItemStack();
                    return SimpleServiceLocator.logisticsManager.hasDestination(
                            ItemIdentifier.get(item),
                            false,
                            ((CoreRoutedPipe) ((LPBCPipe) pipe).pipe.pipe).getRouter().getSimpleID(),
                            new ArrayList<>()) != null;
                }
            }
        }
        return false;
    }

    @Override
    public boolean requiresParameter() {
        return true;
    }
}
