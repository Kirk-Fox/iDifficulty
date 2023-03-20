package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.Difficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDamageByEntityListener implements Listener {

    /**
     * Handles damage modifiers from attacks by entities and the length of venom from cave spiders and bees.
     *
     * @param event the entity damage event
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        EntityType dType = damager.getType();

        // If the damaged entity is not a player or the attacker is a player, don't modify attack.
        if (!(event.getEntity() instanceof Player) || dType == EntityType.PLAYER || isTamedWolf(damager) ||
                isPlayerProjectile(damager) || isPlayerAreaEffectCloud(damager) || dType == EntityType.PRIMED_TNT) return;

        Player player = (Player) event.getEntity();
        Difficulty difficulty = DifficultyHandler.getPlayerDifficulty(player);
        // Check if damage modifiers are enabled and adjust damage accordingly.
        event.setDamage(ConfigHandler.getToggle("damageMod") ? event.getDamage() * difficulty.getDamageMod() : event.getDamage());

        boolean isCaveSpider = dType == EntityType.CAVE_SPIDER;
        boolean isBee = (dType.name().equals("BEE"));

        // Check if venom time is disabled, if the attacker is not venomous, or if the player will die from the attack
        // and exit if any of these are true.
        // If the player dies from the attack, adding the potion effect would cause it to affect them after respawn.
        if (!ConfigHandler.getToggle("venomTime") || !(isCaveSpider || isBee) ||
                player.getHealth() <= event.getFinalDamage()) return;

        // Cancel event to prevent original poison effect and apply the damage to the player directly.
        event.setCancelled(true);
        player.damage(event.getFinalDamage());

        // Calculate and apply poison effect.
        if (difficulty.getVenomTime() > 0) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,
                    20*(difficulty.getVenomTime() + (isBee ? 3 : 0)), 0));
        }

        // If attacker is a bee, remove its stinger.
        if (isBee) ((Bee) damager).setHasStung(true);

    }

    /**
     * Checks if an entity is a tamed wolf.
     *
     * @param entity the entity in question
     * @return if the entity is a tamed wolf
     */
    private boolean isTamedWolf(Entity entity) {
        return entity instanceof Wolf && ((Wolf) entity).isTamed();
    }

    /**
     * Checks if an entity is a projectile originating from a player
     *
     * @param entity the entity in question
     * @return if the entity is a player projectile
     */
    private boolean isPlayerProjectile(Entity entity) {
        return entity instanceof Projectile && isPlayerProjectileSource(((Projectile) entity).getShooter());
    }

    /**
     * Checks if an entity is an area effect cloud from a player
     *
     * @param entity the entity in question
     * @return if the entity is a player area effect cloud
     */
    private boolean isPlayerAreaEffectCloud(Entity entity) {
        try {
            return entity.getType() == EntityType.AREA_EFFECT_CLOUD && isPlayerProjectileSource(((AreaEffectCloud) entity).getSource());
        } catch (NoSuchFieldError e) {
            return false;
        }
    }

    /**
     * Checks if a projectile source is a player
     *
     * @param projSource the projectile source
     * @return if the projectile source is a player
     */
    private boolean isPlayerProjectileSource(ProjectileSource projSource) {
        return projSource instanceof Player || projSource instanceof BlockProjectileSource;
    }

}
