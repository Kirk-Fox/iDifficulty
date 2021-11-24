package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;

public class Difficulty {

    private String name;
    private boolean keepInv;
    private boolean keepExp;
    private double expMod;
    private double damageMod;
    private double lootChance;
    private int venomTime;
    private int minStarveHealth;
    private boolean needsPermission;

    public Difficulty(String name) {
        this.name = name;
        this.keepInv = (boolean) ConfigHandler.getDifficultyValue(name, "keep-inventory");
        this.keepExp = (boolean) ConfigHandler.getDifficultyValue(name, "keep-xp");
        this.expMod = (double) ConfigHandler.getDifficultyValue(name, "xp-multiplier");
        this.damageMod = (double) ConfigHandler.getDifficultyValue(name, "damage-multiplier");
        this.lootChance = (double) ConfigHandler.getDifficultyValue(name, "doubled-loot-chance");
        this.venomTime = (int) ConfigHandler.getDifficultyValue(name, "venom-time");
        this.minStarveHealth = (int) ConfigHandler.getDifficultyValue(name, "min-health-starvation");
        this.needsPermission = (boolean) ConfigHandler.getDifficultyValue(name, "requires-permission");
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

    public int getMinStarveHealth() {
        return minStarveHealth;
    }

    public boolean getNeedsPermission() {
        return needsPermission;
    }

}
