package me.kirkfox.idifficulty.difficulty;

import org.bukkit.configuration.ConfigurationSection;

public class Difficulty {

    private String name;
    private Boolean keepInv;
    private Boolean keepExp;
    private Double expMod;
    private Double damageMod;
    private Double lootChance;
    private Integer venomTime;
    private boolean needsPermission;

    public Difficulty(ConfigurationSection d) {
        this.name = d.getName();
        this.keepInv = d.getBoolean("keep-inventory");
        this.keepExp = d.getBoolean("keep-xp");
        this.expMod = d.getDouble("xp-multiplier");
        this.damageMod = d.getDouble("damage-multiplier");
        this.lootChance = d.getDouble("doubled-loot-chance");
        this.venomTime = d.getInt("venom-time");
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
        needsPermission = d.getNeedsPermission();
    }

    private void resetDifficulty() {
        Difficulty d = DifficultyHandler.getDifficulty(name);
        if(d == null) {
            d = DifficultyHandler.getDefaultDifficulty();
        }
        setDifficulty(d);
    }

    public String getName() {
        return name;
    }

    public String getNameFormatted() {
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public boolean getKeepInv() {
        notNull(keepInv);
        return keepInv;
    }

    public boolean getKeepExp() {
        notNull(keepExp);
        return keepExp;
    }

    public double getExpMod() {
        notNull(expMod);
        return expMod;
    }

    public double getDamageMod() {
        notNull(damageMod);
        return damageMod;
    }

    public double getLootChance() {
        notNull(lootChance);
        return lootChance;
    }

    public int getVenomTime() {
        notNull(venomTime);
        return venomTime;
    }

    public boolean getNeedsPermission() {
        return needsPermission;
    }

    private void notNull(Object o) {
        if(o == null) resetDifficulty();
    }

}
