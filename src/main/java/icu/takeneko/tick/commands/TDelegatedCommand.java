package icu.takeneko.tick.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.SyntaxErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TDelegatedCommand extends CommandBase {

    private final CommandDispatcher<ICommandSender> dispatcher;
    private final String name;

    public TDelegatedCommand(
        CommandDispatcher<ICommandSender> command,
        String name
    ) {
        this.dispatcher = command;
        this.name = name;
    }

    @Override
    public String getCommandName() {
        return name;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        ParseResults<ICommandSender> parseResults = dispatcher.parse(name, sender);
        if (parseResults.getContext().getNodes().isEmpty()) {
            //this should not happen
            throw new IllegalArgumentException("getCommandUsage called but no ParseResult available.");
        }
        Map<CommandNode<ICommandSender>, String> map = dispatcher.getSmartUsage(Iterables.getLast(parseResults.getContext().getNodes()).getNode(), sender);
        if (map.isEmpty()) {
            throw new IllegalArgumentException("No usage available for " + name);
        }
        return new ArrayList<>(map.values()).get(0);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args) {
        String command = name + " " + String.join(" ", args);
        ParseResults<ICommandSender> parseResults = dispatcher.parse(command, sender);
        if (parseResults.getContext().getNodes().isEmpty()) {
            return null;
        }
        try {
            Suggestions sug = dispatcher.getCompletionSuggestions(parseResults)
                .get();
            return sug.getList().stream().map(Suggestion::getText).collect(Collectors.toCollection(ArrayList::new));
        } catch (ExecutionException | InterruptedException e) {
            return null;
        }
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        String command = name + " " + String.join(" ", args);
        try {
            dispatcher.execute(command, sender);
        } catch (CommandSyntaxException e) {
            throw new SyntaxErrorException(e.getMessage());
        } catch (Exception e) {
            throw new CommandException(e.getMessage(), e);
        }
    }
}
