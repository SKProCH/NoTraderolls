package org.skproch.notraderolls;

import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("ALL")
public final class NoTraderolls extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger logger = getLogger();
        getServer().getPluginManager().registerEvents(new VillagerCareerChangeListener(this, logger), this);
        logger.log(Level.INFO, "NoTraderolls handler installed. Trade rolling now prevented.");
    }

    @Override
    public void onDisable() {
        HandlerList.unregisterAll(this);
    }
}
