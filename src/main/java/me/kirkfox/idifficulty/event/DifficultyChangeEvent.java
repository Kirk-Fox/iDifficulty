package me.kirkfox.idifficulty.event;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class DifficultyChangeEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Difficulty oldDifficulty;
    private Difficulty newDifficulty;

    public DifficultyChangeEvent(Player p, Difficulty oldD, Difficulty newD) {
        super(p);
        this.oldDifficulty = oldD;
        this.newDifficulty = newD;
    }

    public Difficulty getOldDifficulty() {
        return oldDifficulty;
    }

    public Difficulty getNewDifficulty() {
        return newDifficulty;
    }

    public void setNewDifficulty(Difficulty newD) {
        this.newDifficulty = newD;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
