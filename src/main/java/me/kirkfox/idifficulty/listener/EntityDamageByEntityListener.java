package me.kirkfox.idifficulty.listener;

import me.kirkfox.idifficulty.ConfigHandler;
import me.kirkfox.idifficulty.difficulty.DifficultyHandler;
import me.kirkfox.idifficulty.difficulty.PlayerDifficulty;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.BlockProjectileSource;
import org.bukkit.projectiles.ProjectileSource;

public class EntityDamageByEntityListener implements Listener {

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent e) {
        Entity d = e.getDamager();
        EntityType dType = d.getType();
        if(e.getEntity() instanceof Player && !(dType == EntityType.PLAYER || isTamedWolf(d) ||
                isPlayerProjectile(d) || isPlayerAreaEffectCloud(d) || dType == EntityType.PRIMED_TNT)) {
            Player p = (Player) e.getEntity();
            PlayerDifficulty pd = DifficultyHandler.getPlayerDifficulty(p);
            double damage = ConfigHandler.getToggle("damageMod") ? e.getDamage() * pd.getDamageMod() : e.getDamage();
            boolean isCaveSpider = dType == EntityType.CAVE_SPIDER;
            boolean isBee;
            try {
                isBee = dType == EntityType.BEE;
            } catch (NoSuchFieldError error) {
                isBee = false;
            }
            if(ConfigHandler.getToggle("venomTime") && (isCaveSpider || isBee)) {
                p.damage(damage);
                int venomTime = pd.getVenomTime();
                if(venomTime > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*(pd.getVenomTime() + (isBee ? 3 : 0)), 0));
                }
                if(isBee) {
                    ((Bee) d).setHasStung(true);
                }
                e.setCancelled(true);
            }
            e.setDamage(damage);
        }
    }

    private boolean isTamedWolf(Entity e) {
        return e instanceof Wolf && ((Wolf) e).isTamed();
    }

    private boolean isPlayerProjectile(Entity e) {
        return e instanceof Projectile && isPlayerProjectileSource(((Projectile) e).getShooter());
    }

    private boolean isPlayerAreaEffectCloud(Entity e) {
        return e instanceof AreaEffectCloud && isPlayerProjectileSource(((AreaEffectCloud) e).getSource());
    }

    private boolean isPlayerProjectileSource(ProjectileSource p) {
        return p instanceof Player || p instanceof BlockProjectileSource;
    }

}
