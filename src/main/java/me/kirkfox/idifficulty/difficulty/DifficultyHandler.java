package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.event.DifficultyChangeEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;

public class DifficultyHandler {

    private static final Map<String, Difficulty> DIFFICULTY_MAP = new HashMap<>();
    private static final List<Difficulty> DIFFICULTY_LIST = new ArrayList<>();
    private static Difficulty defaultDifficulty;

    /**
     * Gets difficulty info from config file and loads into memory.
     */
    public static void registerDifficulties() {
        DIFFICULTY_MAP.clear();
        DIFFICULTY_LIST.clear();
        ConfigHandler.getDifficulties().forEach(d -> {
            DIFFICULTY_MAP.put(d.getName(), d);
            DIFFICULTY_LIST.add(d);
        });

        defaultDifficulty = getDifficulty(ConfigHandler.getDefaultDifficultyName());
    }

    /**
     * Gets the number of difficulties.
     *
     * @return the size of the map of difficulties
     */
    public static int getDifficultyNumber() {
        return DIFFICULTY_MAP.size();
    }

    /**
     * Gets list of difficulties.
     *
     * @return an ordered list of the difficulties
     */
    @NotNull
    public static @UnmodifiableView List<Difficulty> getDifficultyList() {
        return Collections.unmodifiableList(DIFFICULTY_LIST);
    }

    /**
     * Gets the difficulty associated with a certain string.
     *
     * @param name the name of the difficulty
     * @return the relevant difficulty or null if not found
     */
    @Nullable
    public static Difficulty getDifficulty(String name) {
        return DIFFICULTY_MAP.get(name);
    }

    /**
     * Gets the difficulty of a given player.
     *
     * @param player the player
     * @return the difficulty setting for the player
     */
    @NotNull
    public static Difficulty getPlayerDifficulty(@NotNull Player player) {
        return PlayerDataStorage.readDifficulty(player.getUniqueId());
    }

    /**
     * Sets the difficulty of a given player while calling {@link DifficultyChangeEvent}.
     *
     * @param player the player
     * @param diff the new difficulty
     */
    public static void setPlayerDifficulty(Player player, Difficulty diff) {
        DifficultyChangeEvent event = new DifficultyChangeEvent(player, diff);
        IDifficulty.getPlugin().getServer().getPluginManager().callEvent(event);
        Difficulty newD = event.isCancelled() ? event.getOldDifficulty() : event.getNewDifficulty();
        PlayerDataStorage.updateDifficulty(player.getUniqueId(), newD);
    }

    /**
     * Gets the default difficulty as set in the config.
     *
     * @return the default difficulty
     */
    @NotNull
    public static Difficulty getDefaultDifficulty() {
        return (defaultDifficulty == null ? DIFFICULTY_LIST.get(0) : defaultDifficulty);
    }

}
