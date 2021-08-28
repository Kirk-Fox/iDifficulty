package me.kirkfox.idifficulty.difficulty;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class DifficultyHandler {

    private static final ArrayList<Difficulty> DIFFICULTY_LIST = new ArrayList<>();
    private static Difficulty defaultDifficulty;

    public static void registerDifficulties(ConfigurationSection config) {
        ConfigurationSection d = Objects.requireNonNull(config.getConfigurationSection("difficulties"));
        String[] keys = d.getKeys(false).toArray(new String[0]);

        for (String key : keys) {
            DIFFICULTY_LIST.add(new Difficulty(Objects.requireNonNull(d.getConfigurationSection(key))));
        }
        defaultDifficulty = getDifficulty(config.getString("default"));
    }

    public static ArrayList<Difficulty> getDifficultyList() {
        return DIFFICULTY_LIST;
    }

    @Nullable
    public static Difficulty getDifficulty(String name) {
        for(Difficulty d : DIFFICULTY_LIST) {
            if(d.getName().equalsIgnoreCase(name)) {
                return d;
            }
        }
        return null;
    }

    @NotNull
    public static PlayerDifficulty getPlayerDifficulty(Player player) {
        PlayerDifficulty d = DifficultyStorage.readDifficulty(player.getUniqueId());
        return (d != null) ? d : DifficultyStorage.createDifficulty(player.getUniqueId());
    }

    @NotNull
    public static PlayerDifficulty setPlayerDifficulty(Player player, Difficulty d) {
        PlayerDifficulty pd = DifficultyStorage.updateDifficulty(player.getUniqueId(), d);
        return (pd != null) ? pd : DifficultyStorage.createDifficulty(player.getUniqueId(), d);
    }

    public static Difficulty getDefaultDifficulty() {
        return defaultDifficulty;
    }

}
