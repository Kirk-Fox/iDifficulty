package me.kirkfox.idifficulty.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerStarveUpdateListener implements Listener {

    /**
     * When a player regains health, this method calls {@link EntityDamageListener#starveLater(Player)} to check if
     * the player needs to be starved.
     *
     * @param event the player regain health event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerRegainHealth(EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player) {
            EntityDamageListener.starveLater((Player) event.getEntity());
        }
    }

    /**
     * When a player's food level changes, this method calls {@link EntityDamageListener#starveLater(Player)} to check if
     * the player needs to be starved.
     *
     * @param event the food level change event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        EntityDamageListener.starveLater((Player) event.getEntity());
    }

    /**
     * When a player joins the server, this method calls {@link EntityDamageListener#starveLater(Player)} to check if
     * the player needs to be starved.
     *
     * @param event the player join event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        EntityDamageListener.starveLater(event.getPlayer());
    }

}
