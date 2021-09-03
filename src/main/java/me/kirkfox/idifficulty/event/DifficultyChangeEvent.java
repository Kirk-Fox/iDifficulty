package me.kirkfox.idifficulty.event;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerEvent;

public abstract class DifficultyChangeEvent extends PlayerEvent {

    public DifficultyChangeEvent(Player p) {
        super(p);
    }

}
