package me.kirkfox.idifficulty.economy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.OfflinePlayer;

public class VaultEconomy {

    private final Economy economy;

    public VaultEconomy(Economy economy) {
        this.economy = economy;
    }

    public void withdrawPlayer(OfflinePlayer offlinePlayer, double money) {
        economy.withdrawPlayer(offlinePlayer, money);
    }

}
