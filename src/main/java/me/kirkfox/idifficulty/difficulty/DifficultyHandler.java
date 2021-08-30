package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DifficultyHandler {

    private static List<Difficulty> difficultyList = new ArrayList<>();
    private static Difficulty defaultDifficulty;

    public static void registerDifficulties() {
        difficultyList = ConfigHandler.getDifficulties();
        defaultDifficulty = getDifficulty(ConfigHandler.getDefaultDifficultyName());
    }

    public static List<Difficulty> getDifficultyList() {
        return difficultyList;
    }

    @Nullable
    public static Difficulty getDifficulty(String name) {
        for(Difficulty d : difficultyList) {
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

    public static void updatePlayerDifficulty(Player player) {
        UUID uuid = player.getUniqueId();
        PlayerDifficulty pd = DifficultyStorage.readDifficulty(uuid);
        if(pd == null) {
            DifficultyStorage.createDifficulty(uuid, defaultDifficulty);
        } else {
            Difficulty d = getDifficulty(pd.getName());
            DifficultyStorage.updateDifficulty(uuid, d != null ? d : defaultDifficulty);
        }

    }

    public static Difficulty getDefaultDifficulty() {
        return defaultDifficulty;
    }

}
