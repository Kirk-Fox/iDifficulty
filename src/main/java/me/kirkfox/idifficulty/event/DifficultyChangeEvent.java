package me.kirkfox.idifficulty.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

public abstract class DifficultyChangeEvent extends PlayerEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public DifficultyChangeEvent(Player p) {
        super(p);
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

}
