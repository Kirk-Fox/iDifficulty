package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class ConfigHandler {

    private static final int CONFIG_VERSION = 1;
    private static final String[][] CONFIG_VALUES = {{"keep-inventory", "keepInv"}, {"keep-xp", "keepExp"},
            {"xp-multiplier", "expMod"}, {"damage-multiplier", "damageMod"},
            {"doubled-loot-chance", "lootChance"}, {"venom-time", "venomTime"}, {"min-health-starvation", "minStarveHealth"}};
    private static final Map<String, Boolean> TOGGLE_MAP = new HashMap<>();

    private static JavaPlugin plugin;
    private static FileConfiguration config;
    private static ConfigurationSection diffConfig;
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
        diffConfig = Objects.requireNonNull(config.getConfigurationSection("difficulties"));
        diffKeys = diffConfig.getKeys(false);
        if(!config.contains("config-version", true)) {
            config.createSection("config-version");
            config.set("config-version", CONFIG_VERSION);
            addToConfig("requires-permission", false);
            plugin.saveConfig();
            IDifficulty.outputLog("Config updated!");
        }

        for(String[] v : CONFIG_VALUES) {
            TOGGLE_MAP.put(v[1], config.getBoolean("toggle." + v[0]));
        }
    }

    private static void addToConfig(String path, Object value) {
        for (String key : diffKeys) {
            diffConfig.set(key + "." + path, value);
        }
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
