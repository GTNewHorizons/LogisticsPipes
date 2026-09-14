package logisticspipes.commands.commands;

import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;

import logisticspipes.commands.abstracts.ICommandHandler;

public class ClearCommand implements ICommandHandler {

    @Override
    public String[] getNames() {
        return new String[] { "clear" };
    }

    @Override
    public boolean isCommandUsableBy(ICommandSender sender) {
        return true;
    }

    @Override
    public String[] getDescription() {
        return new String[] { "Clears the chat window from every content",
                EnumChatFormatting.GRAY + "add '"
                        + EnumChatFormatting.YELLOW
                        + "all"
                        + EnumChatFormatting.GRAY
                        + "' to also clear the send messages" };
    }

    @Override
    public void executeCommand(ICommandSender sender, String[] args) {
        if (args.length <= 0 || !args[0].equalsIgnoreCase("all")) {
            sender.addChatMessage(new ChatComponentText("%LPSTORESENDMESSAGE%"));
            sender.addChatMessage(new ChatComponentText("%LPCLEARCHAT%"));
            sender.addChatMessage(new ChatComponentText("%LPRESTORESENDMESSAGE%"));
        } else {
            sender.addChatMessage(new ChatComponentText("%LPCLEARCHAT%"));
        }
    }
}
