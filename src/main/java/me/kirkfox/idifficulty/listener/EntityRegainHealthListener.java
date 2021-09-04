package me.kirkfox.idifficulty.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class EntityRegainHealthListener implements Listener {

    @EventHandler
    public void onEntityRegainHealth(EntityRegainHealthEvent e) {
        if(e.getEntity() instanceof Player) {
            EntityDamageListener.starveLater((Player) e.getEntity());
        }
    }

}
