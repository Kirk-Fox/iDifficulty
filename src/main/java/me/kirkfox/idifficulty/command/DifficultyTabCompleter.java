package me.kirkfox.idifficulty.command;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DifficultyTabCompleter implements TabCompleter {

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String alias, @NotNull String[] args) {
        List<String> commandList = new ArrayList<>(Arrays.asList("set", "list", "info", "view", "help"));
        if(args.length == 1) {
            return commandList;
        }
        List<String> difficultyList = new ArrayList<>();
        for (Difficulty d : DifficultyHandler.getDifficultyList()) {
            difficultyList.add(d.getName());
        }
        if(args.length == 2) {
            if("set".equals(args[0]) || "info".equals(args[0])) {
                return difficultyList;
            }
            if("help".equals(args[0])) {
                commandList.remove("help");
                return commandList;
            }
        }

        return null;
    }
}
