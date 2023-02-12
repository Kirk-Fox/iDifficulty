package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.event.DifficultyChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class DifficultyChangeListener implements Listener {

    /**
     * Method called when a players difficulty changes to see if the player should be starving.
     *
     * @param event the difficulty change event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDifficultyChange(DifficultyChangeEvent event) {
        EntityDamageListener.starveLater(event.getPlayer());
    }

}
