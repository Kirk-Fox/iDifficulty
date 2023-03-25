package me.kirkfox.idifficulty.command;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDataStorage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.List;

public class DifficultyCommand implements CommandExecutor {

    private static final String PERMISSION = "idifficulty.";

    private static final ChatColor COLOR_MAIN = ChatColor.DARK_AQUA;
    private static final ChatColor COLOR_CMD = ChatColor.GOLD;
    private static final ChatColor COLOR_ERROR = ChatColor.RED;

    private static final String IDIFF_SET_USAGE = COLOR_MAIN + "Use this command to set your own difficulty to " +
            COLOR_CMD + "<difficulty>" + COLOR_MAIN + " or set " + COLOR_CMD + "<player>" + COLOR_MAIN +
            "'s difficulty to " + COLOR_CMD + "<difficulty>";
    private static final String IDIFF_LIST_USAGE = COLOR_MAIN + "Use this command to see a list of difficulties.";
    private static final String IDIFF_INFO_USAGE = COLOR_MAIN + "Use this command to view information about the " +
            "difficulty named \"" + COLOR_CMD + "<difficulty>" + COLOR_MAIN + "\" including whether a player will " +
            "keep their inventory on death, the amount of damage done by enemies, the experience bonus/penalty from " +
            "mobs and ores, and more.";
    private static final String IDIFF_VIEW_USAGE = COLOR_MAIN + "Use this command to set your own difficulty to view " +
            "your own difficulty or view " + COLOR_CMD + "<player>" + COLOR_MAIN + "'s difficulty";
    private static final String IDIFF_RELOAD_USAGE = COLOR_MAIN + "Use this command to reload iDifficulty's config.yml file";

    /**
     * Called when a user uses the /idifficulty command
     *
     * @param sender the command sender
     * @param cmd the command that was used
     * @param label the alias of the command that was used
     * @param args the passed command arguments
     * @return true (invalid command use is handled by plugin)
     */
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {

        boolean isPlayer = sender instanceof Player;
        Player player = (isPlayer ? (Player) sender : null);

        if (args.length == 0) {
            PluginDescriptionFile description = IDifficulty.getPlugin().getDescription();
            sender.sendMessage(COLOR_MAIN + "This server is running " + COLOR_CMD + description.getName() +
                            " v" + description.getVersion(), COLOR_MAIN + description.getDescription(),
                    COLOR_MAIN + "Type " + COLOR_CMD + "/idiff help" + COLOR_MAIN + " for a list of commands available to you.");
            return true;
        }

        switch (args[0]) {
            case "help":
                if (args.length == 1) {
                    helpDifficulty(sender, isPlayer);
                } else {
                    helpDifficultySpecific(sender, args[1]);
                }
                break;

            case "set":
                if (args.length < 2) {
                    sender.sendMessage(COLOR_MAIN + "Please specify a difficulty. Type " +
                            COLOR_CMD + "/idiff list" + COLOR_MAIN + " to see a list of possible difficulties.");
                    return true;
                }
                Difficulty d;
                d = DifficultyHandler.getDifficulty(args[1]);
                if (d == null) {
                    sender.sendMessage(COLOR_ERROR + "Invalid option for <difficulty>. Type " + COLOR_CMD +
                            "/idiff list" + COLOR_ERROR + " to see a list of possible difficulties.");
                } else if (isPlayer && args.length < 3) {
                    setDifficulty(player, d);
                } else if (args.length > 2) {
                    setDifficultyOthers(sender, args[2], d);
                } else {
                    sender.sendMessage(COLOR_ERROR + "Only online players may change their own difficulty!");
                }
                break;

            case "list":
                sendDifficultyList(sender);
                break;

            case "info":
                if (args.length < 2) {
                    sender.sendMessage(COLOR_MAIN + "Please specify a difficulty. Type " + COLOR_CMD +
                            "/idiff list" + COLOR_MAIN + " to see a list of possible difficulties.");
                    return true;
                }
                sendDifficultyInfo(sender, args[1]);
                break;

            case "view":
                if (args.length == 1) {
                    if (isPlayer) {
                        viewDifficulty(player);
                    } else {
                        sender.sendMessage(COLOR_ERROR + "Only online players may view their own difficulty!");
                    }
                } else {
                    viewDifficultyOthers(sender, args[1]);
                }
                break;

            case "reload":
                if (sender.hasPermission(PERMISSION + "reload")) {
                    ConfigHandler.reloadConfig();
                    sender.sendMessage(COLOR_MAIN + "iDifficulty config file reloaded!");
                } else {
                    sender.sendMessage(COLOR_ERROR + "You don't have permission to reload the iDifficulty config file!");
                }
                break;

            default:
                sender.sendMessage(COLOR_ERROR + "Unrecognized subcommand! Type " + COLOR_CMD +
                        "/idiff help" + COLOR_ERROR + " for a list of possible commands.");
        }
        return true;
    }

    /**
     * Handles the /idiff help command when it is called. Checks what permissions the user has and displays
     * info about the commands available to them.
     *
     * @param sender the command sender
     * @param isPlayer whether the sender is a player
     */
    private void helpDifficulty(@NotNull CommandSender sender, boolean isPlayer) {

        boolean hasPerm = false;

        if (sender.hasPermission(PERMISSION + "set") && isPlayer) {
            sender.sendMessage(COLOR_CMD + "/idiff set <difficulty>" + COLOR_MAIN +
                    " - Sets your difficulty");
            hasPerm = true;
        }

        if (sender.hasPermission(PERMISSION + "set.others")) {
            sender.sendMessage(COLOR_CMD + "/idiff set <difficulty> <player>" + COLOR_MAIN + " - Sets " +
                    COLOR_CMD + "<player>" + COLOR_MAIN + "'s difficulty");
            hasPerm = true;
        }

        if (sender.hasPermission(PERMISSION + "list")) {
            sender.sendMessage(COLOR_CMD + "/idiff list" + COLOR_MAIN + " - Gives a list of difficulties");
            hasPerm = true;
        }

        if (sender.hasPermission(PERMISSION + "info")) {
            sender.sendMessage(COLOR_CMD + "/idiff info <difficulty>" + COLOR_MAIN +
                    " - Gives info on " + COLOR_CMD + "<difficulty>");
            hasPerm = true;
        }

        if ((sender.hasPermission(PERMISSION + "view")) && isPlayer) {
            sender.sendMessage(COLOR_CMD + "/idiff view" + COLOR_MAIN +
                    " - Gives your current difficulty");
            hasPerm = true;
        }

        if (sender.hasPermission(PERMISSION + "view.others")) {
            sender.sendMessage(COLOR_CMD + "/idiff view <player>" + COLOR_MAIN + " - Gives " +
                    COLOR_CMD + "<player>" + COLOR_MAIN + "'s current difficulty");
            hasPerm = true;
        }

        if (sender.hasPermission(PERMISSION + "reload")) {
            sender.sendMessage(COLOR_CMD + "/idiff reload" + COLOR_MAIN + " - Reloads iDifficulty config file");
        }

        if (!hasPerm) {
            sender.sendMessage(COLOR_ERROR + "You don't have access to any commands associated with the iDifficulty plugin. " +
                    "Notify an administrator if you think this is a mistake.");
        }

    }

    /**
     * Handles the /idiff help command when a subcommand is specified, providing more detail on that subcommand.
     *
     * @param sender the command sender
     * @param command the subcommand in question
     */
    private void helpDifficultySpecific(@NotNull CommandSender sender, @NotNull String command) {

        String commandUsage = COLOR_MAIN + "Usage: " + COLOR_CMD + "/idiff " + ChatColor.BOLD + command + ChatColor.RESET + COLOR_CMD;

        switch (command) {
            case "set":
                sender.sendMessage(commandUsage + " <difficulty> [<player>]", IDIFF_SET_USAGE);
                break;

            case "list":
                sender.sendMessage(commandUsage, IDIFF_LIST_USAGE, COLOR_MAIN + "For more information on a " +
                        "specific difficulty, type " + COLOR_CMD + "/idiff info <difficulty>");
                break;

            case "info":
                sender.sendMessage(commandUsage + " <difficulty>", IDIFF_INFO_USAGE);
                break;

            case "view":
                sender.sendMessage(commandUsage + " [<player>]", IDIFF_VIEW_USAGE);
                break;

            case "reload":
                sender.sendMessage(commandUsage, IDIFF_RELOAD_USAGE);
                break;

            default:
                sender.sendMessage(COLOR_ERROR + "Unrecognized subcommand! Type " + COLOR_CMD +
                    "/idiff help" + COLOR_ERROR + " for a list of possible commands.");
        }
    }

    /**
     * Sets the difficulty of the player given that adequate permissions and requirements are in place to do so.
     *
     * @param player the player
     * @param newD the new difficulty
     */

    private void setDifficulty(@NotNull Player player, @NotNull Difficulty newD) {
        Date date = new Date();
        Date dateChanged = PlayerDataStorage.getDateChanged(player.getUniqueId());
        long timeDelay = ConfigHandler.getTimeDelay();

        if (dateChanged != null && !player.hasPermission(PERMISSION + "ignoredelay")) {

            if (date.before(new Date(dateChanged.getTime() + timeDelay * 60000))) {
                long timeLeft = dateChanged.getTime() + timeDelay * 60000 - date.getTime();
                String timeLeftString = (timeLeft < 60000) ? timeLeft/1000 + " seconds" : timeLeft/60000 + " minutes";
                player.sendMessage(COLOR_ERROR + "You can't change your difficulty for another " + timeLeftString);
                return;
            }

            if (timeDelay < 0) {
                player.sendMessage(COLOR_ERROR + "You have already set your difficulty. If this was a mistake," +
                        "please notify an administrator.");
                return;
            }

        }

        if (!player.hasPermission(PERMISSION + "set")) {
            player.sendMessage(COLOR_ERROR + "You don't have permission to change your current difficulty!");
            return;
        }

        if (newD.getNeedsPermission() && !player.hasPermission(PERMISSION + "diff." + newD.getName())) {
            player.sendMessage(COLOR_ERROR + "You don't have permission to use " + newD.getNameFormatted() + " difficulty!");
            return;
        }

        setDifficultyPermitted(player, newD);
    }

    /**
     * Sets the difficulty of the player without checking permissions.
     *
     * @param player the player
     * @param newD the new difficulty
     */
    private void setDifficultyPermitted(@NotNull Player player, @NotNull Difficulty newD) {
        DifficultyHandler.setPlayerDifficulty(player, newD);
        player.sendMessage(COLOR_MAIN + "Your difficulty has been changed to " + COLOR_CMD + newD.getNameFormatted());
    }

    /**
     * Sets the difficulty of the player specified by the command sender given adequate permissions are in place.
     *
     * @param sender the command sender
     * @param pName the name of the player
     * @param newD the new difficulty of the player
     */
    private void setDifficultyOthers(@NotNull CommandSender sender, @NotNull String pName, @NotNull Difficulty newD) {

        Player p = IDifficulty.getPlayer(pName);

        if (sender.equals(p)) {
            setDifficulty(p, newD);
            return;
        }

        if (!sender.hasPermission(PERMISSION + "set.others")) {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to change other players' difficulties!");
            return;
        }

        if (p == null) {
            sender.sendMessage(COLOR_ERROR + "The player '" + pName + "' was not found!");
            return;
        }

        if (newD.getNeedsPermission() && !sender.hasPermission(PERMISSION + "diff." + newD.getName())) {
            sender.sendMessage(COLOR_ERROR + "You do not have permission to use this difficulty!");
            return;
        }

        setDifficultyPermitted(p, newD);
        sender.sendMessage(COLOR_MAIN + p.getName() + "'s difficulty has been changed to " + COLOR_CMD + newD.getNameFormatted());
    }

    /**
     * Sends a human-readable list of the available difficulties if the sender has permission to see it.
     *
     * @param sender the command sender
     */
    private void sendDifficultyList(@NotNull CommandSender sender) {

        if (!sender.hasPermission(PERMISSION + "list")) {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view a list of difficulties!");
            return;
        }

        List<Difficulty> dList = DifficultyHandler.getDifficultyList();
        StringBuilder dString = new StringBuilder();
        int s = dList.size();
        for (int i = 0; i < s; i++) {
            String name = dList.get(i).getNameFormatted();
            dString.append(i < s - 1 ? name + ", " : "and " + name);
        }

        sender.sendMessage(COLOR_MAIN + "The available difficulties are " + dString);

    }

    /**
     * Sends detailed info about a specified difficulty if the difficulty exists and if the command sender
     * has permission to see such info.
     *
     * @param sender the command sender
     * @param difficulty the difficulty name
     */
    private void sendDifficultyInfo(@NotNull CommandSender sender, @NotNull String difficulty) {

        if (!sender.hasPermission(PERMISSION + "info")) {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view difficulty info!");
            return;
        }

        Difficulty d = DifficultyHandler.getDifficulty(difficulty);

        if (d == null) {
            sender.sendMessage(COLOR_ERROR + "Invalid option for <difficulty>. Type " +
                    COLOR_CMD + "/idiff list" + COLOR_ERROR + " to see a list of possible difficulties.");
            return;
        }

        int venomTime = d.getVenomTime();
        int h = d.getMinStarveHealth();
        String starveHealth = (h%2==0) ? String.valueOf(h/2) : String.valueOf((double) h/2);
        sender.sendMessage(ChatColor.BLUE + "The " + COLOR_CMD + d.getNameFormatted() + ChatColor.BLUE +
                " difficulty setting will make the following changes:");
        if (ConfigHandler.getToggle("keepInv")) sender.sendMessage(COLOR_MAIN + "Player will " + COLOR_CMD +
                (d.getKeepInv() ? "keep" : "drop") + COLOR_MAIN + " items on death.");
        if (ConfigHandler.getToggle("keepExp")) sender.sendMessage(COLOR_MAIN + "Player will " + COLOR_CMD +
                (d.getKeepExp() ? "keep" : "drop") + COLOR_MAIN + " experience on death.");
        if (ConfigHandler.getToggle("damageMod")) sender.sendMessage(COLOR_MAIN + "Player will take " + COLOR_CMD +
                Math.round(d.getDamageMod()*100) + "%" + COLOR_MAIN + " of regular damage from mobs");
        if (ConfigHandler.getToggle("mobExpMod")) sender.sendMessage(COLOR_MAIN + "Player will acquire " + COLOR_CMD +
                Math.round(d.getMobExpMod()*100) + "%" + COLOR_MAIN + " of regular experience when killing mobs");
        if (ConfigHandler.getToggle("oreExpMod")) sender.sendMessage(COLOR_MAIN + "Player will acquire " + COLOR_CMD +
                Math.round(d.getOreExpMod()*100) + "%" + COLOR_MAIN + " of regular experience when mining ore and using furnaces");
        if (ConfigHandler.getToggle("mobLootChance")) sender.sendMessage(COLOR_MAIN + "There is a " + COLOR_CMD +
                Math.round(d.getMobLootChance()*100) + "%" + COLOR_MAIN + " chance that mobs killed by the player " +
                "will drop twice as many items");
        if (ConfigHandler.getToggle("oreLootChance")) sender.sendMessage(COLOR_MAIN + "There is a " + COLOR_CMD +
                Math.round(d.getOreLootChance()*100) + "%" + COLOR_MAIN + " chance that ore mined by the player " +
                "will drop twice as many items");
        if (ConfigHandler.getToggle("venomTime")) sender.sendMessage(COLOR_MAIN + "Player will " +
                (venomTime > 0 ? "" : COLOR_CMD + "not " + COLOR_MAIN) + "be poisoned by cave spiders" +
                (venomTime > 0 ? " for " + COLOR_CMD + venomTime + COLOR_MAIN + " second" + (venomTime == 1 ? "" : "s") : ""));
        if (ConfigHandler.getToggle("minStarveHealth")) sender.sendMessage(COLOR_MAIN + "Player will starve to " +
                COLOR_CMD + (h <= 0 ? "death" : starveHealth + (COLOR_MAIN + " hearts of health before stopping")));
        sender.sendMessage(COLOR_MAIN + "To change to this difficulty, type: " + COLOR_CMD + "/idiff set " + d.getName());

    }

    /**
     * Sends a player their current difficulty if they have permission to see it.
     *
     * @param player the player
     */
    private void viewDifficulty(@NotNull Player player) {
        if (player.hasPermission(PERMISSION + "view")) {
            player.sendMessage(COLOR_MAIN + "Your current difficulty is " + COLOR_CMD +
                    DifficultyHandler.getPlayerDifficulty(player).getNameFormatted());
        } else {
            player.sendMessage(COLOR_ERROR + "You don't have permission to view your current difficulty!");
        }
    }

    /**
     * Shows the command sender the difficulty of a player provided they have permission to see it.
     *
     * @param sender the command sender
     * @param pName the player name
     */
    private void viewDifficultyOthers(@NotNull CommandSender sender, @NotNull String pName) {
        Player p = IDifficulty.getPlayer(pName);
        if (sender.equals(p)) {
            viewDifficulty(p);
            return;
        }

        if (!sender.hasPermission(PERMISSION + "view.others")) {
            sender.sendMessage(COLOR_ERROR + "You don't have permission to view other players' difficulties!");
            return;
        }

        if(p == null) {
            sender.sendMessage(COLOR_ERROR + "The player '" + pName + "' was not found!");
            return;
        }

        sender.sendMessage(COLOR_MAIN + p.getName() + "'s current difficulty is " + COLOR_CMD +
                DifficultyHandler.getPlayerDifficulty(p).getNameFormatted());
    }

}
