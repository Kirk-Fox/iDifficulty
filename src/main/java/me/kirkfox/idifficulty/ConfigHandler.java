package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConfigHandler {

    private static final String[][] CONFIG_VALUES = {{"keep-inventory", "keepInv"}, {"keep-xp", "keepExp"},
            {"mob-xp-multiplier", "mobExpMod"}, {"ore-xp-multiplier", "oreExpMod"}, {"damage-multiplier", "damageMod"},
            {"mob-doubled-loot-chance", "mobLootChance"}, {"ore-doubled-loot-chance", "oreLootChance"},
            {"venom-time", "venomTime"}, {"min-health-starvation", "minStarveHealth"}};
    private static final Map<String, Boolean> TOGGLE_MAP = new HashMap<>();

    private static JavaPlugin plugin;
    private static FileConfiguration config;
    private static Set<String> diffKeys;

    public static void registerConfig() {
        plugin = IDifficulty.getPlugin();
        plugin.saveDefaultConfig();
        enableConfig();
    }

    public static void reloadConfig() {
        plugin.reloadConfig();
        enableConfig();
        DifficultyHandler.registerDifficulties();
        DifficultyHandler.updateAllPlayerDifficulties();
    }

    private static void enableConfig() {
        config = plugin.getConfig();
        diffKeys = Objects.requireNonNull(config.getConfigurationSection("difficulties")).getKeys(false);
        updateConfig();

        for(String[] v : CONFIG_VALUES) {
            TOGGLE_MAP.put(v[1], config.getBoolean("toggle." + v[0]));
        }
    }

    private static void updateConfig() {
        int configVersion = config.getInt("config-version", 0);
        if(configVersion == 0) {
            setWithComment("config-version", 1, "DO NOT CHANGE UNDER ANY CIRCUMSTANCES");

            updateMobOreConfig("toggle", "xp-multiplier", true, "If \"true\" difficulties " +
                    "will affect how much experience %s drop", "mobs", "ores");
            updateMobOreConfig("toggle", "doubled-loot-chance", true, "If \"true\" difficulties " +
                    "will have a chance to double item drops from %s", "mobs", "ores");
            for (String key : diffKeys) {
                updateMobOreConfig("difficulties." + key, "xp-multiplier", key.equals("easy"),
                        "Sets the multiplier of experience gained from %s", "killing mobs",
                        "mining blocks and using furnaces");
                updateMobOreConfig("difficulties." + key, "doubled-loot-chance", key.equals("easy"),
                        "Sets the chance (where 0.0 is 0%% and 1.0 is 100%%) that %s by a player will drop double items",
                        "mobs killed", "ores broken");
            }

            setWithComment("toggle.min-health-starvation", false, "If \"true\" difficulties will " +
                    "affect the health at which a player will stop taking damage from starvation");

            addToConfig("min-health-starvation", 10, "Sets the health at which starvation stops " +
                    "dealing damage to the player where 20 is 10 hearts and 0 is 0 hearts");
            addToConfig("requires-permission", false, Arrays.asList("Sets whether this difficulty requires " +
                            "players that use it to have the \"idifficulty.diff.[difficulty_name]\" permission",
                    "For example, if set to true for this difficulty, players would need the permission node " +
                            "\"idifficulty.diff.easy\" to use it"));

            plugin.saveConfig();
            IDifficulty.outputLog("Config updated!");
        }
    }

    private static void addToConfig(String path, Object value, List<String> comment) {
        for (String key : diffKeys) {
            setWithComment("difficulties." + key + "." + path, value, key.equals("easy"), comment);
        }
    }

    private static void addToConfig(String path, Object value, String comment) {
        addToConfig(path, value, Collections.singletonList(comment));
    }

    private static void updateMobOreConfig(String key, String path, boolean addComment, String comment, String mob, String ore) {
        Object mod = config.get(key + "." + path);
        config.set(key + "." + path, null);
        setWithComment(key + ".mob-" + path, mod, addComment, String.format(comment, mob));
        setWithComment(key + ".ore-" + path, mod, addComment, String.format(comment, ore));
    }

    private static void setWithComment(String path, Object value, boolean addComment, List<String> comment) {
        config.set(path, value);
        if(addComment) config.setComments(path, comment);
    }

    private static void setWithComment(String path, Object value, boolean addComment, String comment) {
        setWithComment(path, value, addComment, Collections.singletonList(comment));
    }

    private static void setWithComment(String path, Object value, String comment) {
        setWithComment(path, value, true, comment);
    }

    public static Map<String, Boolean> getToggleMap() {
        return Collections.unmodifiableMap(TOGGLE_MAP);
    }

    public static boolean getToggle(String value) {
        return TOGGLE_MAP.get(value);
    }

    public static List<Difficulty> getDifficulties() {
        List<Difficulty> dList = new ArrayList<>();

        for (String key : diffKeys) {
            dList.add(new Difficulty(key));
        }
        return dList;
    }

    public static Object getDifficultyValue(String dName, String value) {
        String path = "difficulties." + dName + "." + value;
        return config.get(path);
    }

    public static String getDefaultDifficultyName() {
        return config.getString("default");
    }

}
