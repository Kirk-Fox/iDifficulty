package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;

public class Difficulty {

    private String name;
    private boolean keepInv;
    private boolean keepExp;
    private double mobExpMod;
    private double oreExpMod;
    private double damageMod;
    private double mobLootChance;
    private double oreLootChance;
    private int venomTime;
    private int minStarveHealth;
    private boolean needsPermission;

    public Difficulty(String name) {
        this.name = name;
        this.keepInv = (boolean) getValue("keep-inventory");
        this.keepExp = (boolean) getValue("keep-xp");
        this.mobExpMod = (double) getValue("mob-xp-multiplier");
        this.oreExpMod = (double) getValue("ore-xp-multiplier");
        this.damageMod = (double) getValue("damage-multiplier");
        this.mobLootChance = (double) getValue("mob-doubled-loot-chance");
        this.oreLootChance = (double) getValue("ore-doubled-loot-chance");
        this.venomTime = (int) getValue("venom-time");
        this.minStarveHealth = (int) getValue("min-health-starvation");
        this.needsPermission = (boolean) getValue("requires-permission");
    }

    public Difficulty(Difficulty d) {
        setDifficulty(d);
    }

    private Object getValue(String key) { return ConfigHandler.getDifficultyValue(this.name, key); }

    protected void setDifficulty(Difficulty d) {
        name = d.getName();
        keepInv = d.getKeepInv();
        keepExp = d.getKeepExp();
        mobExpMod = d.getMobExpMod();
        oreExpMod = d.getOreExpMod();
        damageMod = d.getDamageMod();
        mobLootChance = d.getMobLootChance();
        oreLootChance = d.getOreLootChance();
        venomTime = d.getVenomTime();
        minStarveHealth = d.getMinStarveHealth();
        needsPermission = d.getNeedsPermission();
    }

    public String getName() { return name; }

    public String getNameFormatted() {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public boolean getKeepInv() { return keepInv; }

    public boolean getKeepExp() { return keepExp; }

    public double getMobExpMod() { return mobExpMod; }

    public double getOreExpMod() { return oreExpMod; }

    public double getDamageMod() { return damageMod; }

    public double getMobLootChance() { return mobLootChance; }

    public double getOreLootChance() {return oreLootChance; }

    public int getVenomTime() { return venomTime; }

    public int getMinStarveHealth() { return minStarveHealth; }

    public boolean getNeedsPermission() { return needsPermission; }

}
