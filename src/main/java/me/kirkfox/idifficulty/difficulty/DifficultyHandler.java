package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.event.DifficultyChangeEvent;
import org.bukkit.Bukkit;
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
    public static PlayerDifficulty getPlayerDifficulty(Player p) {
        PlayerDifficulty d = DifficultyStorage.readDifficulty(p.getUniqueId());
        return (d != null) ? d : DifficultyStorage.createDifficulty(p.getUniqueId());
    }

    @NotNull
    public static PlayerDifficulty setPlayerDifficulty(Player p, Difficulty d) {
        DifficultyChangeEvent e = new DifficultyChangeEvent(p, getPlayerDifficulty(p), d);
        Bukkit.getPluginManager().callEvent(e);
        Difficulty newD = e.getNewDifficulty();
        PlayerDifficulty pd = DifficultyStorage.updateDifficulty(p.getUniqueId(), newD);
        return (pd != null) ? pd : DifficultyStorage.createDifficulty(p.getUniqueId(), newD);
    }

    public static void updatePlayerDifficulty(Player p) {
        UUID uuid = p.getUniqueId();
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
