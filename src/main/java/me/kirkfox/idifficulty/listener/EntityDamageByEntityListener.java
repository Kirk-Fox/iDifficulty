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
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity d = event.getDamager();
        EntityType dType = d.getType();
        if(event.getEntity() instanceof Player && !(dType == EntityType.PLAYER || isTamedWolf(d) ||
                isPlayerProjectile(d) || isPlayerAreaEffectCloud(d) || dType == EntityType.PRIMED_TNT)) {
            Player p = (Player) event.getEntity();
            PlayerDifficulty pd = DifficultyHandler.getPlayerDifficulty(p);
            double damage = ConfigHandler.getToggle("damageMod") ? event.getDamage() * pd.getDamageMod() : event.getDamage();
            boolean isCaveSpider = dType == EntityType.CAVE_SPIDER;
            boolean isBee;
            try {
                isBee = dType == EntityType.BEE;
            } catch (NoSuchFieldError error) {
                isBee = false;
            }
            event.setDamage(damage);
            if(ConfigHandler.getToggle("venomTime") && (isCaveSpider || isBee) && p.getHealth() > event.getFinalDamage()) {
                p.damage(event.getFinalDamage());
                int venomTime = pd.getVenomTime();
                if(venomTime > 0) {
                    p.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20*(pd.getVenomTime() + (isBee ? 3 : 0)), 0));
                }
                if(isBee) {
                    ((Bee) d).setHasStung(true);
                }
                event.setCancelled(true);
            }
        }
    }

    private boolean isTamedWolf(Entity entity) {
        return entity instanceof Wolf && ((Wolf) entity).isTamed();
    }

    private boolean isPlayerProjectile(Entity entity) {
        return entity instanceof Projectile && isPlayerProjectileSource(((Projectile) entity).getShooter());
    }

    private boolean isPlayerAreaEffectCloud(Entity entity) {
        try {
            return entity.getType() == EntityType.AREA_EFFECT_CLOUD && isPlayerProjectileSource(((AreaEffectCloud) entity).getSource());
        } catch (NoSuchFieldError e) {
            return false;
        }
    }

    private boolean isPlayerProjectileSource(ProjectileSource p) {
        return p instanceof Player || p instanceof BlockProjectileSource;
    }

}
