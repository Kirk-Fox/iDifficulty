package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityDeathListener implements Listener {

    /**
     * Handles experience modifier and doubled drops when a mob dies.
     *
     * @param event the entity death event
     */
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity entity = event.getEntity();
        Player player = entity.getKiller();

        // End method if entity is another player or if the entity was not killed by a player.
        if (entity instanceof Player || player == null) return;

        PlayerDifficulty diff = DifficultyHandler.getPlayerDifficulty(player);
        // Check if the mob experience modifier is enabled and calculate the experience.
        if (ConfigHandler.getToggle("mobExpMod"))
            event.setDroppedExp((int) Math.round(event.getDroppedExp() * diff.getMobExpMod()));

        // Check if mob loot doubling is disabled or if random chance fails and exit if so.
        if (!ConfigHandler.getToggle("mobLootChance") ||
                IDifficulty.getRand().nextDouble() >= diff.getMobLootChance()) return;

        List<ItemStack> doubledLoot = new ArrayList<>(event.getDrops());
        EntityEquipment equip = entity.getEquipment();

        // If entity has no equipment, skip equipment handling.
        if (equip != null) {
            // Remove armor and inventory from doubled drops.
            ItemStack[] equipment = entity.getEquipment().getArmorContents();

            for (ItemStack i : equipment) doubledLoot.remove(i);

            if (entity instanceof InventoryHolder) {
                ItemStack[] inventory = ((InventoryHolder) entity).getInventory().getContents();
                for (ItemStack i : inventory) doubledLoot.remove(i);
            }

            try {
                doubledLoot.remove(equip.getItemInMainHand());
                doubledLoot.remove(equip.getItemInOffHand());
            } catch (NoSuchMethodError e) {
                doubledLoot.remove(equip.getItemInHand());
            }
            // Add doubled drops to event drops.
        }

        event.getDrops().addAll(doubledLoot);
    }

}
