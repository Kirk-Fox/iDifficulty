package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.event.DifficultyChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DifficultyChangeListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDifficultyChange(DifficultyChangeEvent e) {
        EntityDamageListener.starveLater(e.getPlayer());
    }

}
