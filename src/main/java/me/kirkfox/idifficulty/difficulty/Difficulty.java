package me.kirkfox.idifficulty.difficulty;

import me.kirkfox.idifficulty.ConfigHandler;

public class Difficulty {

    private final String name;
    private final boolean keepInv;
    private final boolean keepExp;
    private final double mobExpMod;
    private final double oreExpMod;
    private final double damageMod;
    private final double mobLootChance;
    private final double oreLootChance;
    private final int venomTime;
    private final int minStarveHealth;
    private final boolean needsPermission;

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

    private Object getValue(String key) { return ConfigHandler.getDifficultyValue(this.name, key); }

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

    public double getOreLootChance() { return oreLootChance; }

    public int getVenomTime() { return venomTime; }

    public int getMinStarveHealth() { return minStarveHealth; }

    public boolean getDoesNotNeedPermission() { return !needsPermission; }

}
