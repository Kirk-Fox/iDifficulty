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
    private static final Set<Player> DYING_SET = new HashSet<>();

    public EntityDamageListener() {
        HEALTH_MAP.put(Difficulty.PEACEFUL, 20.0);
        HEALTH_MAP.put(Difficulty.EASY, 10.0);
        HEALTH_MAP.put(Difficulty.NORMAL, 1.0);
        HEALTH_MAP.put(Difficulty.HARD, 0.0);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            if(e.getCause() == EntityDamageEvent.DamageCause.STARVATION) {
                if(shouldStarve(p, e.getDamage())) {
                    STARVING_SET.add(p);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            starve(p, e.getDamage());
                        }
                    }.runTaskLater(IDifficulty.getPlugin(), getStarvationRate(p));
                } else {
                    if(shouldNotStarve(p, e.getDamage())) e.setCancelled(true);
                    STARVING_SET.remove(p);
                }
            } else {
                starveLater(p);
            }
        }
    }

    public static void starve(Player p, double damage) {
        if(shouldStarve(p)) {
            STARVING_SET.add(p);
            EntityDamageEvent starveEvent = new EntityDamageEvent(p,
                    EntityDamageEvent.DamageCause.STARVATION, damage);
            Bukkit.getPluginManager().callEvent(starveEvent);
            double d = starveEvent.getDamage();
            if(p.getHealth() <= d) DYING_SET.add(p);
            p.damage(d);
        } else {
            STARVING_SET.remove(p);
        }
    }

    public static void starveLater(Player p, double damage) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if(!STARVING_SET.contains(p)) {
                    starve(p, damage);
                }
            }
        }.runTaskLater(IDifficulty.getPlugin(), getStarvationRate(p));
    }

    public static void starveLater(Player p) {
        starveLater(p, 1.0);
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

    private static boolean shouldNotStarve(Player p, double damage) {
        return ConfigHandler.getToggle("minStarveHealth") &&
                DifficultyHandler.getPlayerDifficulty(p).getMinStarveHealth() > p.getHealth() - damage;
    }

    public static boolean isDyingFromStarvation(Player p) {
        if(DYING_SET.contains(p)) {
            DYING_SET.remove(p);
            return true;
        }
        return false;
    }

    private static int getStarvationRate(Player p) {
        int ticks;
        try {
            ticks = p.getStarvationRate();
        } catch (NoSuchMethodError e) {
            ticks = 80;
        }
        return ticks;
    }

}
