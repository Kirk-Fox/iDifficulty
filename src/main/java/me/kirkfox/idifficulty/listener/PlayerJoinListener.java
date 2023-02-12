package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    /**
     * When a player joins, this method updates their difficulty and informs them of any new plugin updates if they have
     * the relevant permission.
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        DifficultyHandler.updatePlayerDifficulty(player);
        String updateString = IDifficulty.getUpdateString();
        if (updateString != null && player.hasPermission("idifficulty.updatecheck")) {
            player.sendMessage(ChatColor.DARK_AQUA + updateString);
        }
    }

}
