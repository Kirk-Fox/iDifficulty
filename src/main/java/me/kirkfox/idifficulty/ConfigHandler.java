package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDataStorage;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;

public class ConfigHandler {

    private static final String[][] CONFIG_VALUES = {{"keep-inventory", "keepInv"}, {"keep-xp", "keepExp"},
            {"mob-xp-multiplier", "mobExpMod"}, {"ore-xp-multiplier", "oreExpMod"}, {"damage-multiplier", "damageMod"},
            {"mob-doubled-loot-chance", "mobLootChance"}, {"ore-doubled-loot-chance", "oreLootChance"},
            {"venom-time", "venomTime"}, {"min-health-starvation", "minStarveHealth"},
            {"money-lost-on-death", "moneyLostOnDeath"}};
    private static final Map<String, Boolean> TOGGLE_MAP = new HashMap<>();

    private static JavaPlugin plugin;
    private static FileConfiguration config;
    private static Set<String> diffKeys;

    /**
     * Saves default configuration if one does not exist, then enables it.
     */
    public static void registerConfig() {
        plugin = IDifficulty.getPlugin();
        plugin.saveDefaultConfig();
        enableConfig();
    }

    /**
     * Reloads the configuration from the config.yml and updates {@link DifficultyHandler} with new settings.
     */
    public static void reloadConfig() {
        plugin.reloadConfig();
        enableConfig();
        DifficultyHandler.registerDifficulties();
        // Ensure that players do not retain out-of-date difficulty settings.
        try {
            PlayerDataStorage.loadDifficulties();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Stores list of difficulties and setting toggles and updates the configuration if necessary.
     */
    private static void enableConfig() {
        config = plugin.getConfig();
        diffKeys = Objects.requireNonNull(config.getConfigurationSection("difficulties")).getKeys(false);

        // Update the configuration if it is from a previous version.
        updateConfig();

        for (String[] v : CONFIG_VALUES) {
            TOGGLE_MAP.put(v[1], config.getBoolean("toggle." + v[0]));
        }
    }

    /**
     * Reads the configuration's version number to add/change the necessary options.
     */
    private static void updateConfig() {
        // If "config-version" does not exist, this correlates to version 0, thus, it must be updated.
        int configVersion = config.getInt("config-version", 0);
        if (configVersion == 0) {
            // Add "config-version".
            setWithComment("config-version", 1, "DO NOT CHANGE UNDER ANY CIRCUMSTANCES");

            // Split experience and doubled loot toggles into separate settings for mobs and ores.
            updateMobOreConfig("toggle", "xp-multiplier", true, "If \"true\" difficulties " +
                    "will affect how much experience %s drop", "mobs", "ores");
            updateMobOreConfig("toggle", "doubled-loot-chance", true, "If \"true\" difficulties " +
                    "will have a chance to double item drops from %s", "mobs", "ores");

            // Split experience and doubled loot difficulty options into separate settings for mobs and ores.
            for (String key : diffKeys) {
                updateMobOreConfig("difficulties." + key, "xp-multiplier", key.equals("easy"),
                        "Sets the multiplier of experience gained from %s", "killing mobs",
                        "mining blocks and using furnaces");
                updateMobOreConfig("difficulties." + key, "doubled-loot-chance", key.equals("easy"),
                        "Sets the chance (where 0.0 is 0%% and 1.0 is 100%%) that %s by a player will drop double items",
                        "mobs killed", "ores broken");
            }

            // Add toggle for variable starvation.
            setWithComment("toggle.min-health-starvation", false, "If \"true\" difficulties will " +
                    "affect the health at which a player will stop taking damage from starvation");

            // Add variable starvation and permission requirement settings to each difficulty.
            addToConfig("min-health-starvation", 10, "Sets the health at which starvation stops " +
                    "dealing damage to the player where 20 is 10 hearts and 0 is 0 hearts");
            addToConfig("requires-permission", false, Arrays.asList("Sets whether this difficulty requires " +
                            "players that use it to have the \"idifficulty.diff.[difficulty_name]\" permission",
                    "For example, if set to true for this difficulty, players would need the permission node " +
                            "\"idifficulty.diff.easy\" to use it"));

            // Save the configuration to config.yml.
            plugin.saveConfig();
            IDifficulty.outputLog("Config updated!");
        }
    }

    /**
     * Adds a configuration setting to all difficulties.
     *
     * @param path the name of the new setting
     * @param value the value to initialize the setting as
     * @param comment a list of strings that will appear as comments above this setting in the configuration
     */
    private static void addToConfig(String path, Object value, List<String> comment) {
        for (String key : diffKeys) {
            setWithComment("difficulties." + key + "." + path, value, key.equals("easy"), comment);
        }
    }

    /**
     * Adds a configuration setting to all difficulties.
     *
     * @param path the name of the new setting
     * @param value the value to initialize the setting as
     * @param comment a string that will appear as a comment above this setting in the configuration
     */
    private static void addToConfig(String path, Object value, String comment) {
        addToConfig(path, value, Collections.singletonList(comment));
    }

    /**
     * Splits a given setting into separate settings for mobs and ores.
     *
     * @param key location of setting
     * @param path name of setting
     * @param addComment if a comment should be added
     * @param comment a comment that will appear above the setting
     * @param mob string that comment is formatted with for mob setting
     * @param ore string that comment is formatted with for ore setting
     */
    private static void updateMobOreConfig(String key, String path, boolean addComment, String comment, String mob, String ore) {
        Object mod = config.get(key + "." + path);
        config.set(key + "." + path, null);
        setWithComment(key + ".mob-" + path, mod, addComment, String.format(comment, mob));
        setWithComment(key + ".ore-" + path, mod, addComment, String.format(comment, ore));
    }

    /**
     * Sets a value and adds a comment to that value.
     *
     * @param path setting path
     * @param value the value to initialize
     * @param addComment if a comment should be added
     * @param comment a list of strings that will appear as comments above this setting in the configuration
     */
    private static void setWithComment(String path, Object value, boolean addComment, List<String> comment) {
        config.set(path, value);
        if (addComment) config.setComments(path, comment);
    }

    /**
     * Sets a value and adds a comment to that value.
     *
     * @param path setting path
     * @param value the value to initialize
     * @param addComment if a comment should be added
     * @param comment a string that will appear as a comment above this setting in the configuration
     */
    private static void setWithComment(String path, Object value, boolean addComment, String comment) {
        setWithComment(path, value, addComment, Collections.singletonList(comment));
    }

    /**
     * Sets a value and adds a comment to that value.
     *
     * @param path setting path
     * @param value the value to initialize
     * @param comment a string that will appear as a comment above this setting in the configuration
     */
    private static void setWithComment(String path, Object value, String comment) {
        setWithComment(path, value, true, comment);
    }

    /**
     * Gets the length of the delay before a player can change their difficulty.
     *
     * @return the time delay in minutes
     */
    public static long getTimeDelay() {
        return config.getLong("time-delay");
    }

    /**
     * Gets an unmodifiable map of the toggleable settings and their values.
     *
     * @return a map of the toggle values
     */
    public static Map<String, Boolean> getToggleMap() {
        return Collections.unmodifiableMap(TOGGLE_MAP);
    }

    /**
     * Gets the value of a specific toggleable setting.
     *
     * @param value the identifier for the toggleable setting
     * @return the value of the setting
     */
    public static boolean getToggle(String value) {
        return TOGGLE_MAP.get(value);
    }

    /**
     * Creates a list of all difficulties detailed in the configuration.
     *
     * @return a list of difficulties
     */
    public static List<Difficulty> getDifficulties() {
        List<Difficulty> dList = new ArrayList<>();

        for (String key : diffKeys) {
            dList.add(new Difficulty(key));
        }
        return dList;
    }

    /**
     * Gets the value of a setting for a specific difficulty.
     *
     * @param dName name of the difficulty
     * @param value name of the setting
     * @return the difficulty setting's value
     */
    public static Object getDifficultyValue(String dName, String value) {
        String path = "difficulties." + dName + "." + value;
        return config.get(path);
    }

    /**
     * Gets the name of the default difficulty.
     *
     * @return the default difficulty's name
     */
    @NotNull
    public static String getDefaultDifficultyName() {
        String name = config.getString("default");
        assert name != null;
        return name;
    }

}
