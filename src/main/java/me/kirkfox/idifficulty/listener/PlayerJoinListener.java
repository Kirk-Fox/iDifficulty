package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final String updateString;

    public PlayerJoinListener(String updateString) {
        this.updateString = updateString;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DifficultyHandler.updatePlayerDifficulty(p);
        EntityDamageListener.starve(p, 1.0);
        if(updateString != null && (p.isOp() || p.hasPermission("idifficulty.*"))) {
            p.sendMessage(ChatColor.DARK_AQUA + updateString);
        }
    }

}
