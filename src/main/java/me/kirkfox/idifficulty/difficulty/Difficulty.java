package me.kirkfox.idifficulty.difficulty;

import org.bukkit.configuration.ConfigurationSection;

public class Difficulty {

    private String name;
    private boolean keepInv;
    private boolean keepExp;
    private double expMod;
    private double damageMod;
    private double lootChance;
    private int venomTime;
    private double minStarveHealth;
    private boolean needsPermission;

    public Difficulty(ConfigurationSection d) {
        this.name = d.getName();
        this.keepInv = d.getBoolean("keep-inventory");
        this.keepExp = d.getBoolean("keep-xp");
        this.expMod = d.getDouble("xp-multiplier");
        this.damageMod = d.getDouble("damage-multiplier");
        this.lootChance = d.getDouble("doubled-loot-chance");
        this.venomTime = d.getInt("venom-time");
        this.minStarveHealth = d.getDouble("min-health-starvation");
        this.needsPermission = d.getBoolean("requires-permission");
    }

    public Difficulty(Difficulty d) {
        setDifficulty(d);
    }

    protected void setDifficulty(Difficulty d) {
        name = d.getName();
        keepInv = d.getKeepInv();
        keepExp = d.getKeepExp();
        expMod = d.getExpMod();
        damageMod = d.getDamageMod();
        lootChance = d.getLootChance();
        venomTime = d.getVenomTime();
        minStarveHealth = d.getMinStarveHealth();
        needsPermission = d.getNeedsPermission();
    }

    public String getName() {
        return name;
    }

    public String getNameFormatted() {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public boolean getKeepInv() {
        return keepInv;
    }

    public boolean getKeepExp() {
        return keepExp;
    }

    public double getExpMod() {
        return expMod;
    }

    public double getDamageMod() {
        return damageMod;
    }

    public double getLootChance() {
        return lootChance;
    }

    public int getVenomTime() {
        return venomTime;
    }

    public double getMinStarveHealth() {
        return minStarveHealth;
    }

    public boolean getNeedsPermission() {
        return needsPermission;
    }

}
