package me.kirkfox.idifficulty;

import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ConfigHandler {

    private static final String[][] CONFIG_VALUES = {{"keep-inventory", "keepInv"}, {"keep-xp", "keepExp"},
            {"xp-multiplier", "expMod"}, {"damage-multiplier", "damageMod"},
            {"doubled-loot-chance", "lootChance"}, {"venom-time", "venomTime"}};
    private static final Map<String, Boolean> TOGGLE_MAP = new HashMap<>();

    public static void registerConfig(JavaPlugin plugin) {
        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();
        DifficultyHandler.registerDifficulties(config);

        for(String[] v : CONFIG_VALUES) {
            TOGGLE_MAP.put(v[1], config.getBoolean("toggle." + v[0]));
        }
    }

    public static Map<String, Boolean> getToggleMap() {
        return Collections.unmodifiableMap(TOGGLE_MAP);
    }

    public static boolean getToggle(String value) {
        return TOGGLE_MAP.get(value);
    }

}
