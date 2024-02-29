package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityDamageListener implements Listener {

    private static final Map<Difficulty, Double> HEALTH_MAP = new EnumMap<>(Difficulty.class);
    private static final Set<Player> STARVING_SET = new HashSet<>();

    /**
     * Initializes the listener and stores the typical starvation limits in HEALTH_MAP.
     */
    public EntityDamageListener() {
        HEALTH_MAP.put(Difficulty.PEACEFUL, 20.0);
        HEALTH_MAP.put(Difficulty.EASY, 10.0);
        HEALTH_MAP.put(Difficulty.NORMAL, 1.0);
        HEALTH_MAP.put(Difficulty.HARD, 0.0);
    }

    /**
     * Handles variable starvation mechanics by listening for damage caused by starvation.
     *
     * @param event - the entity damage event
     */
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        // Exit if entity is not player or if variable starvation is disabled.
        if (!(event.getEntity() instanceof Player) || !ConfigHandler.getToggle("minStarveHealth")) return;

        Player p = (Player) event.getEntity();
        // If the player was not already starving exit while checking for starvation.
        // This way there will not be multiple recurrences of starvation.
        if (event.getCause() != EntityDamageEvent.DamageCause.STARVATION) {
            starveLater(p);
            return;
        }

        // Check if plugin needs to starve player.
        if (shouldForceStarve(p, event.getDamage())) {
            STARVING_SET.add(p);
            // Starve player after the player's starvation rate.
            new BukkitRunnable() {
                @Override
                public void run() {
                    starve(p, event.getDamage());
                }
            }.runTaskLater(IDifficulty.getPlugin(), getStarvationRate(p));
        } else {
            // Check if plugin needs to prevent starvation.
            if (DifficultyHandler.getPlayerDifficulty(p).getMinStarveHealth() > p.getHealth() - event.getDamage())
                event.setCancelled(true);
            STARVING_SET.remove(p);
        }
    }

    /**
     * Checks if player should starve, then calls damage event to start starvation cycle.
     *
     * @param player player to starve
     * @param damage starvation damage
     */
    public static void starve(Player player, double damage) {
        if (shouldForceStarve(player)) {
            STARVING_SET.add(player);

            EntityDamageEvent starveEvent = new EntityDamageEvent(
                    player, EntityDamageEvent.DamageCause.STARVATION,
                    DamageSource.builder(DamageType.STARVE).build(), damage
            );
            Bukkit.getPluginManager().callEvent(starveEvent);
            if (!starveEvent.isCancelled()) {
                player.damage(starveEvent.getDamage(), starveEvent.getDamageSource());
            }
        } else {
            STARVING_SET.remove(player);
        }
    }

    /**
     * Method starves player if necessary after starvation rate. This method is to be used when some change means that
     * a player must be checked for starvation (e.g. joining the server, gaining health, changing difficulty).
     *
     * @param player player to starve
     * @param damage starvation damage
     */
    public static void starveLater(Player player, double damage) {
        new BukkitRunnable() {
            @Override
            public void run() {
                // If player is already starving, do not initiate a new starvation recurrence.
                if (!STARVING_SET.contains(player)) {
                    starve(player, damage);
                }
            }
        }.runTaskLater(IDifficulty.getPlugin(), getStarvationRate(player));
    }

    public static void starveLater(Player player) {
        starveLater(player, 1.0);
    }

    /**
     * Checks a player's health and the damage being applied to the player to determine if the player should starve.
     * The method checks if the player has an empty food bar, if the player will drop to or below the default starvation limit,
     * and if the players minimum starvation is below the player's resulting health.
     *
     * @param player the starving player
     * @param damage the damage on the player
     * @return if the plugin needs to starve the player after the event
     */
    private static boolean shouldForceStarve(Player player, double damage) {
        double h = player.getHealth() - damage;
        return (player.getFoodLevel() == 0 && h <= HEALTH_MAP.get(player.getWorld().getDifficulty()) &&
                DifficultyHandler.getPlayerDifficulty(player).getMinStarveHealth() < h);
    }

    private static boolean shouldForceStarve(Player player) {
        return shouldForceStarve(player, 0.0);
    }

    /**
     * Method gets the rate (in ticks) that the player should starve at. Older versions do not have
     * {@link Player#getStarvationRate()} and in such case this method returns the default value of 80 ticks.
     *
     * @param player the starving player
     * @return the time in server ticks between instances of starvation damage
     */
    private static int getStarvationRate(Player player) {
        int ticks;
        try {
            ticks = player.getStarvationRate();
        } catch (NoSuchMethodError e) {
            ticks = 80;
        }
        return ticks;
    }

}
