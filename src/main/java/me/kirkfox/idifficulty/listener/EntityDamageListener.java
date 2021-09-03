package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.IDifficulty;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import org.bukkit.Bukkit;
import org.bukkit.Difficulty;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityDamageListener implements Listener {

    private static final Map<Difficulty, Double> HEALTH_MAP = new HashMap<>();
    private static final Set<Player> STARVING_SET = new HashSet<>();

    public EntityDamageListener() {
        HEALTH_MAP.put(Difficulty.PEACEFUL, 20.0);
        HEALTH_MAP.put(Difficulty.EASY, 10.0);
        HEALTH_MAP.put(Difficulty.NORMAL, 1.0);
        HEALTH_MAP.put(Difficulty.HARD, 0.0);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
            Player p = (Player) e.getEntity();
            if(shouldStarve(p, e.getDamage())) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        starve(p, e.getDamage());
                    }
                }.runTaskLater(IDifficulty.getPlugin(), p.getStarvationRate());
            }
        }
    }

    public static void starve(Player p, double damage) {
        if(shouldStarve(p)) {
            STARVING_SET.add(p);
            EntityDamageEvent starveEvent = new EntityDamageEvent(p,
                    EntityDamageEvent.DamageCause.STARVATION, damage);
            Bukkit.getPluginManager().callEvent(starveEvent);
            p.damage(starveEvent.getDamage());
        } else {
            STARVING_SET.remove(p);
        }
    }

    public static void starveIfApplicable(Player p, double damage) {
        if(!STARVING_SET.contains(p)) {
            starve(p, damage);
        }
    }

    private static boolean shouldStarve(Player p, double damage) {
        double h = p.getHealth() - damage;
        return ConfigHandler.getToggle("minStarveHealth") && p.getFoodLevel() == 0 &&
                h <= HEALTH_MAP.get(p.getWorld().getDifficulty()) &&
                DifficultyHandler.getPlayerDifficulty(p).getMinStarveHealth() < h;
    }

    private static boolean shouldStarve(Player p) {
        return shouldStarve(p, 0.0);
    }

}
