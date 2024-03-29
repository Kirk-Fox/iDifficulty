package me.kirkfox.idifficulty.command;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DifficultyTabCompleter implements TabCompleter {

    /**
     * Requests a list of possible completions for a command argument.
     *
     * @param sender the command sender
     * @param command the command
     * @param alias the alias used
     * @param args the current command arguments
     * @return a list of available following arguments or null if there are none
     */
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {

        int numArgs = args.length;
        if (numArgs > 2) return null;

        String[] commands = {"set", "list", "info", "view", "reload", "help"};
        List<String> commandList = new ArrayList<>();
        for (String c : commands) {
            if (sender.hasPermission("idifficulty." + c) || c.equals("help")) commandList.add(c);
        }
        if (numArgs == 1) {
            return commandList;
        }
        List<String> difficultyList = new ArrayList<>();
        for (Difficulty d : DifficultyHandler.getDifficultyList()) {
            if (!d.getNeedsPermission() || sender.hasPermission("idifficulty.diff." + d.getName())) {
                difficultyList.add(d.getName());
            }
        }

        if ("set".equals(args[0]) || "info".equals(args[0])) {
            return difficultyList;
        }
        if ("help".equals(args[0])) {
            commandList.remove("help");
            return commandList;
        }

        return null;
    }
}
