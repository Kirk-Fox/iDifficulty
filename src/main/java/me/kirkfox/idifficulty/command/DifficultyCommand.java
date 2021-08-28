package me.kirkfox.idifficulty.command;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;

public class DifficultyCommand implements CommandExecutor {

    private static final String PERMISSION = "idifficulty.";

    private static final ChatColor COLOR_MAIN = ChatColor.DARK_AQUA;
    private static final ChatColor COLOR_CMD = ChatColor.GOLD;
    private static final ChatColor COLOR_ERROR = ChatColor.RED;

    private boolean isOp;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        boolean isPlayer = sender instanceof Player;
        Player player = null;
        if(isPlayer) {
            player = (Player) sender;
        }
        isOp = sender.isOp();
        if(args.length == 0) {
            PluginDescriptionFile description = IDifficulty.getPlugin().getDescription();
            sender.sendMessage(COLOR_MAIN + "This server is running " + COLOR_CMD + description.getName() +
                            " v" + description.getVersion(), COLOR_MAIN + description.getDescription(),
                    COLOR_MAIN + "Type " + COLOR_CMD + "/idiff help" + COLOR_MAIN + " for a list of commands available to you.");
            return true;
        }
        switch(args[0]) {
            case "help":
                if(args.length == 1) {
                    helpDifficulty(sender, isPlayer);
                } else {
                    helpDifficultySpecific(sender, args[1]);
                }
                break;
            case "set":
                if(args.length < 2) {
                    sender.sendMessage(COLOR_MAIN + "Please specify a difficulty. Type " +
                            COLOR_CMD + "/idiff list" + COLOR_MAIN + " to see a list of possible difficulties.");
                    return true;
                }
                Difficulty d;
                d = DifficultyHandler.getDifficulty(args[1]);
                if(d == null) {
                    sender.sendMessage(COLOR_ERROR + "Invalid option for <difficulty>. Type " + COLOR_CMD +
                            "/idiff list" + COLOR_ERROR + " to see a list of possible difficulties.");
                } else if(isPlayer && args.length < 3) {
                    setDifficulty(player, d, false);
                } else if(args.length > 2) {
                    setDifficultyOthers(sender, args[2], d);
                } else {
                    sender.sendMessage(COLOR_ERROR + "Only online players may change their own difficulty!");
                }
                break;
            case "list":
                sendDifficultyList(sender);
                break;
            case "info":
                if(args.length < 2) {
                    sender.sendMessage(COLOR_MAIN + "Please specify a difficulty. Type " + COLOR_CMD +
                            "/idiff list" + COLOR_MAIN + " to see a list of possible difficulties.");
                    return true;
                }
                sendDifficultyInfo(sender, args[1]);
                break;
            case "view":
                if(args.length == 1) {
                    if(isPlayer) {
                        viewDifficulty(player);
                    } else {
                        sender.sendMessage(COLOR_ERROR + "Only online players may view their own difficulty!");
                    }
                } else {
                    viewDifficultyOthers(sender, args[1]);
                }
                break;
            default:
                sender.sendMessage(COLOR_ERROR + "Unrecognized subcommand! Type " + COLOR_CMD +
                        "/idiff help" + COLOR_ERROR + " for a list of possible commands.");
        }
        return true;
    }

    private void helpDifficulty(CommandSender sender, boolean isPlayer) {
        boolean hasPerm = false;
        if((isOp || sender.hasPermission(PERMISSION + "set")) && isPlayer) {
            sender.sendMessage(COLOR_CMD + "/idiff set <difficulty>" + COLOR_MAIN +
                    " - Sets your difficulty");
            hasPerm = true;
        }
        if(isOp || sender.hasPermission(PERMISSION + "set.others")) {
            sender.sendMessage(COLOR_CMD + "/idiff set <difficulty> <player>" + COLOR_MAIN + " - Sets " +
                    COLOR_CMD + "<player>" + COLOR_MAIN + "'s difficulty");
            hasPerm = true;
        }
        if(isOp || sender.hasPermission(PERMISSION + "list")) {
            sender.sendMessage(COLOR_CMD + "/idiff list" + COLOR_MAIN + " - Gives a list of difficulties");
            hasPerm = true;
        }
        if(isOp || sender.hasPermission(PERMISSION + "info")) {
            sender.sendMessage(COLOR_CMD + "/idiff info <difficulty>" + COLOR_MAIN +
                    " - Gives info on " + COLOR_CMD + "<difficulty>");
            hasPerm = true;
        }
        if((isOp || sender.hasPermission(PERMISSION + "view")) && isPlayer) {
            sender.sendMessage(COLOR_CMD + "/idiff view" + COLOR_MAIN +
                    " - Gives your current difficulty");
            hasPerm = true;
        }
        if(isOp || sender.hasPermission(PERMISSION + "view.others")) {
            sender.sendMessage(COLOR_CMD + "/idiff view <player>" + COLOR_MAIN + " - Gives " +
                    COLOR_CMD + "<player>" + COLOR_MAIN + "'s current difficulty");
            hasPerm = true;
        }
        if(!hasPerm) {
            sender.sendMessage(COLOR_ERROR + "You don't have access to any commands associated with the iDifficulty plugin. " +
                    "Notify an administrator if you think this is a mistake.");
        }
    }

    private void helpDifficultySpecific(CommandSender sender, @NotNull String command) {
        String commandUsage = COLOR_MAIN + "Usage: " + COLOR_CMD + "/idiff " + ChatColor.BOLD + command + ChatColor.RESET + COLOR_CMD;
        switch(command) {
            case "set":
                sender.sendMessage(commandUsage + " <difficulty> [<player>]",
                    COLOR_MAIN + "Use this command to set your own difficulty to " + COLOR_CMD + "<difficulty>" +
                            COLOR_MAIN + " or set " + COLOR_CMD + "<player>" + COLOR_MAIN + "'s difficulty to " +
                            COLOR_CMD + "<difficulty>");
                break;
            case "list":
                sender.sendMessage(commandUsage, COLOR_MAIN + "Use this command to see a list of difficulties.",
                    COLOR_MAIN + "For more information on a specific difficulty, type " + COLOR_CMD +
                            "/idiff info <difficulty>");
                break;
            case "info":
                sender.sendMessage(commandUsage + " <difficulty>",
                    COLOR_MAIN + "Use this command to view information about the difficulty named \"" + COLOR_CMD +
                            "<difficulty>" + COLOR_MAIN + "\" including whether a player will keep their inventory " +
                            "on death, the amount of damage done by enemies, the experience bonus/penalty from mobs " +
                            "and ores, and more.");
                break;
            case "view":
                sender.sendMessage(commandUsage + " [<player>]",
                    COLOR_MAIN + "Use this command to set your own difficulty to view your own difficulty or view " +
                            COLOR_CMD + "<player>" + COLOR_MAIN + "'s difficulty");
                break;
            default:
                sender.sendMessage(COLOR_ERROR + "Unrecognized subcommand! Type " + COLOR_CMD +
                    "/idiff help" + COLOR_ERROR + " for a list of possible commands.");
        }
    }

    private String setDifficulty(Player player, Difficulty d, boolean skipPerm) {
        Date date = new Date();
        PlayerDifficulty pd = DifficultyHandler.getPlayerDifficulty(player);
        Date dateChanged = pd.getDateChanged();
        long timeDelay = IDifficulty.getPlugin().getConfig().getLong("time-delay");
        if(dateChanged == null || (date.after(new Date(dateChanged.getTime() + timeDelay * 60000)) && timeDelay > -1) ||
                player.hasPermission(PERMISSION + "ignoredelay") || isOp || skipPerm) {
            if(isOp || player.hasPermission(PERMISSION + "set") || skipPerm) {
                pd.setDateChanged(date);
                String dName = DifficultyHandler.setPlayerDifficulty(player, d).getNameFormatted();
                player.sendMessage(COLOR_MAIN + "Your difficulty has been changed to " + dName);
                return dName;
            }
            player.sendMessage(COLOR_ERROR + "You don't have permission to change your current difficulty!");
        } else if (timeDelay < 0) {
            player.sendMessage(COLOR_ERROR + "You have already set your difficulty. If this was a mistake, please notify an administrator.");
        } else {
            long timeLeft = dateChanged.getTime() + timeDelay * 60000 - date.getTime();
            String timeLeftString = (timeLeft < 60000) ? timeLeft/1000 + " seconds" : timeLeft/60000 + " minutes";
            player.sendMessage(COLOR_ERROR + "You can't change your difficulty for another " + timeLeftString);
        }
        return null;
    }

    private void setDifficultyOthers(CommandSender sender, String pName, Difficulty d) {
        Player p = IDifficulty.getPlayer(pName);
        if(sender.equals(p)) {
            setDifficulty(p, d, false);
        } else if(isOp || sender.hasPermission(PERMISSION + "set.others")) {
            if(p != null) {
                String dName = setDifficulty(p, d, true);
                sender.sendMessage(COLOR_MAIN + p.getName() + "'s difficulty has been changed to " + dName);
            } else {
                sender.sendMessage(COLOR_ERROR + "The player '" + pName + "' was not found!");
            }
        } else {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to change other players' difficulties!");
        }
    }

    private void sendDifficultyList(CommandSender sender) {
        if(isOp || sender.hasPermission(PERMISSION + "list")) {
            ArrayList<Difficulty> dList = DifficultyHandler.getDifficultyList();
            StringBuilder dString = new StringBuilder();
            int s = dList.size();
            for (int i = 0; i < s; i++) {
                String name = dList.get(i).getNameFormatted();
                dString.append(i < s - 1 ? name + ", " : "and " + name);
            }
            sender.sendMessage(COLOR_MAIN + "The available difficulties are " + dString);
        } else {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view a list of difficulties!");
        }
    }

    private void sendDifficultyInfo(CommandSender sender, String difficulty) {
        if(isOp || sender.hasPermission(PERMISSION + "info")) {
            Difficulty d = DifficultyHandler.getDifficulty(difficulty);
            if (d != null) {
                int venomTime = d.getVenomTime();
                sender.sendMessage(COLOR_MAIN + "The " + d.getNameFormatted() + " difficulty setting will make the following changes:");
                if(ConfigHandler.getToggle("keepInv")) sender.sendMessage(COLOR_MAIN + "Player will " + COLOR_CMD +
                        (d.getKeepInv() ? "keep" : "drop") + COLOR_MAIN + " items on death.");
                if(ConfigHandler.getToggle("keepExp")) sender.sendMessage(COLOR_MAIN + "Player will " + COLOR_CMD +
                        (d.getKeepExp() ? "keep" : "drop") + COLOR_MAIN + " experience on death.");
                if(ConfigHandler.getToggle("damageMod")) sender.sendMessage(COLOR_MAIN + "Player will take " + COLOR_CMD +
                        Math.round(d.getDamageMod()*100) + "%" + COLOR_MAIN + " of regular damage from mobs");
                if(ConfigHandler.getToggle("expMod")) sender.sendMessage(COLOR_MAIN + "Player will acquire " + COLOR_CMD +
                        Math.round(d.getExpMod()*100) + "%" + COLOR_MAIN + " of regular experience when killing mobs, " +
                        "mining blocks, and using furnaces");
                if(ConfigHandler.getToggle("lootChance")) sender.sendMessage(COLOR_MAIN + "There is a " + COLOR_CMD +
                        Math.round(d.getLootChance()*100) + "%" + COLOR_MAIN + " chance that blocks mined and mobs " +
                        "killed by the player will drop twice as many items");
                if(ConfigHandler.getToggle("venomTime")) sender.sendMessage(COLOR_MAIN + "Player will " +
                        (venomTime > 0 ? "" : COLOR_CMD + "not " + COLOR_MAIN) + "be poisoned by cave spiders" +
                        (venomTime > 0 ? " for " + COLOR_CMD + venomTime + COLOR_MAIN + " second" + (venomTime == 1 ? "" : "s") : ""));
                sender.sendMessage(COLOR_MAIN + "To change to this difficulty, type: " + COLOR_CMD + "/idiff set " + d.getName());
            } else {
                sender.sendMessage(COLOR_ERROR + "Invalid option for <difficulty>. Type " +
                        COLOR_CMD + "/idiff list" + COLOR_ERROR + " to see a list of possible difficulties.");
            }
        } else {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view difficulty info!");
        }
    }

    private void viewDifficulty(Player player) {
        if(isOp || player.hasPermission(PERMISSION + "view")) {
            player.sendMessage(COLOR_MAIN + "Your current difficulty is " +
                    DifficultyHandler.getPlayerDifficulty(player).getNameFormatted());
        } else {
            player.sendMessage(COLOR_ERROR + "You don't have permission to view your current difficulty!");
        }
    }

    private void viewDifficultyOthers(CommandSender sender, String pName) {
        Player p = IDifficulty.getPlayer(pName);
        if(sender.equals(p)) {
            viewDifficulty(p);
        } else if(isOp || sender.hasPermission(PERMISSION + "view.others")) {
            if(p != null) {
                sender.sendMessage(COLOR_MAIN + p.getName() + "'s current difficulty is " +
                        DifficultyHandler.getPlayerDifficulty(p).getNameFormatted());
            } else {
                sender.sendMessage(COLOR_ERROR + "The player '" + pName + "' was not found!");
            }
        } else {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view other players' difficulties!");
        }
    }

}
