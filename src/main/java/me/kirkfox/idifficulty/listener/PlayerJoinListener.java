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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        DifficultyHandler.updatePlayerDifficulty(p);
        EntityDamageListener.starveLater(p);
        String u = IDifficulty.getUpdateString();
        if(u != null && (p.isOp() || p.hasPermission("idifficulty.*"))) {
            p.sendMessage(ChatColor.DARK_AQUA + u);
        }
    }

}
