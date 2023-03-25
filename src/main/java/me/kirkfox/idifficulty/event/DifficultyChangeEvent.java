package me.kirkfox.idifficulty.event;

import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

public class DifficultyChangeEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    private final Difficulty oldDifficulty;
    private Difficulty newDifficulty;
    private boolean cancel;

    /**
     * Initializes DifficultyChangeEvent.
     *
     * @param player the player that is changing difficulty
     * @param newDiff the new difficulty
     */
    public DifficultyChangeEvent(Player player, Difficulty newDiff) {
        super(player);
        this.oldDifficulty = DifficultyHandler.getPlayerDifficulty(player);
        this.newDifficulty = newDiff;
        this.cancel = false;
    }

    /**
     * Gets the difficulty before the event.
     *
     * @return the old difficulty
     */
    public Difficulty getOldDifficulty() { return oldDifficulty; }

    /**
     * Gets the new difficulty.
     *
     * @return the new difficulty
     */
    public Difficulty getNewDifficulty() { return newDifficulty; }

    /**
     * Sets the new difficulty.
     *
     * @param newDiff the new difficulty
     */
    public void setNewDifficulty(Difficulty newDiff) { this.newDifficulty = newDiff; }

    /**
     * Checks if the event is cancelled.
     *
     * @return if the event is cancelled.
     */
    public boolean isCancelled() { return cancel; }

    /**
     * Sets if the event should be cancelled.
     *
     * @param cancel if the event should be cancelled.
     */
    public void setCancelled(boolean cancel) { this.cancel = cancel; }

    @Override
    @NotNull
    public HandlerList getHandlers() { return HANDLERS; }

    public static HandlerList getHandlerList() { return HANDLERS; }

}
