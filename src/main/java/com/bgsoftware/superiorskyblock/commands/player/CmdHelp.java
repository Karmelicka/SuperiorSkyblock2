package com.bgsoftware.superiorskyblock.commands.player;

import com.bgsoftware.superiorskyblock.SuperiorSkyblockPlugin;
import com.bgsoftware.superiorskyblock.api.commands.SuperiorCommand;
import com.bgsoftware.superiorskyblock.commands.ISuperiorCommand;
import com.bgsoftware.superiorskyblock.core.SequentialListBuilder;
import com.bgsoftware.superiorskyblock.core.messages.Message;
import com.bgsoftware.superiorskyblock.player.PlayerLocales;
import org.bukkit.command.CommandSender;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CmdHelp implements ISuperiorCommand {

    @Override
    public List<String> getAliases() {
        return Arrays.asList("help", "pomoc");
    }

    @Override
    public String getPermission() {
        return "superior.island.help";
    }

    @Override
    public String getUsage(java.util.Locale locale) {
        return "pomoc";
    }

    @Override
    public String getDescription(java.util.Locale locale) {
        return Message.COMMAND_DESCRIPTION_HELP.getMessage(locale);
    }

    @Override
    public int getMinArgs() {
        return 1;
    }

    @Override
    public int getMaxArgs() {
        return 2;
    }

    @Override
    public boolean canBeExecutedByConsole() {
        return true;
    }
    @Override
    public boolean displayCommand() {
        return false;
    }
    @Override
    public void execute(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        List<SuperiorCommand> subCommands = new SequentialListBuilder<SuperiorCommand>()
                .filter(subCommand -> subCommand.displayCommand() && (subCommand.getPermission().isEmpty() ||
                        sender.hasPermission(subCommand.getPermission())))
                .build(plugin.getCommands().getSubCommands());

        if (subCommands.isEmpty()) {
            Message.NO_COMMAND_PERMISSION.send(sender);
            return;
        }

        Message.ISLAND_HELP_HEADER.send(sender);

        java.util.Locale locale = PlayerLocales.getLocale(sender);

        for (SuperiorCommand _subCommand : subCommands) {
            String description = _subCommand.getDescription(locale);
            if (description == null)
                new NullPointerException("The description of the command " + _subCommand.getAliases().get(0) + " is null.").printStackTrace();

            Message.ISLAND_HELP_LINE.send(sender, plugin.getCommands().getLabel() + " " + _subCommand.getUsage(locale), description == null ? "" : description);
        }
    }

    @Override
    public List<String> tabComplete(SuperiorSkyblockPlugin plugin, CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
