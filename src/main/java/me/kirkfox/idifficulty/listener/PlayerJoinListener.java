package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        DifficultyHandler.updatePlayerDifficulty(e.getPlayer());
    }

}
