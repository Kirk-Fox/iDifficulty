package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class PlayerDeathListener implements Listener {

    /**
     * Handles keep inventory and keep experience mechanics upon a player's death.
     *
     * @param event the player death event
     */
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player p = event.getEntity();
        Difficulty d = DifficultyHandler.getPlayerDifficulty(p);
        
        boolean keepInv = d.getKeepInv();
        boolean keepExp = d.getKeepExp();

        // Check if keep inventory setting is enabled.
        if (ConfigHandler.getToggle("keepInv")) {
            event.setKeepInventory(keepInv);
            List<ItemStack> drops = event.getDrops();
            
            // Clear item drops and handle manually.
            drops.clear();
            if (!keepInv) {
                ItemStack[] inv = p.getInventory().getContents();
                
                // Check if VANISHING_CURSE exists.
                Enchantment vanish = null;
                boolean vanishingCurse = true;
                try {
                    vanish = Enchantment.VANISHING_CURSE;
                } catch (NoSuchFieldError e) {
                    vanishingCurse = false;
                }
                
                // Drop all items without the curse of vanishing.
                for (ItemStack i : inv)
                    if (i != null && !(vanishingCurse && i.containsEnchantment(vanish))) drops.add(i);
            }
        }

        // Check if keep experience setting is enabled and calculate dropped experience.
        if (ConfigHandler.getToggle("keepExp")) {
            event.setKeepLevel(keepExp);
            if (!keepExp) {
                event.setNewLevel(0);
                event.setNewExp(0);
            }
            event.setDroppedExp(keepExp ? 0 : Math.min(7*p.getLevel(), 100));
        }

        // Check if player was killed by difficulty-induced starvation. If so, adjust death message accordingly.
        if (EntityDamageListener.isDyingFromStarvation(p)) {
            event.setDeathMessage(p.getName() + " starved to death");
        }
    }

}
