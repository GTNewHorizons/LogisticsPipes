package logisticspipes.commands.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentText;

import logisticspipes.commands.abstracts.ICommandHandler;
import logisticspipes.commands.exception.MissingArgumentException;
import logisticspipes.utils.item.ItemIdentifier;

public class NameLookupCommand implements ICommandHandler {

    @Override
    public String[] getNames() {
        return new String[] { "name" };
    }

    @Override
    public boolean isCommandUsableBy(ICommandSender sender) {
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[] { "Displays the serverside stored name for", "the <item id> and <meta data>" };
    }

    @Override
    public void executeCommand(ICommandSender sender, String[] args) {
        if (args.length < 2) {
            throw new MissingArgumentException();
        }
        String idString = args[0];
        String metaString = args[1];
        int id = Integer.parseInt(idString);
        int meta = Integer.parseInt(metaString);
        ItemIdentifier item = ItemIdentifier.get(Item.getItemById(id), meta, null);
        sender.addChatMessage(new ChatComponentText("Name: " + item.getFriendlyNameCC()));
    }
}
