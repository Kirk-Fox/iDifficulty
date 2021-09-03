package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.event.DifficultyChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DifficultyChangeListener implements Listener {

    @EventHandler
    public void onDifficultyChange(DifficultyChangeEvent e) {
        EntityDamageListener.starveIfApplicable(e.getPlayer(), 1.0);
    }

}
