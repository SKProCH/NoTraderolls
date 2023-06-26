package org.skproch.notraderolls;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.VillagerCareerChangeEvent;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VillagerCareerChangeListener implements Listener {
    private final JavaPlugin plugin;
    private final Logger logger;

    public VillagerCareerChangeListener(JavaPlugin plugin, Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    @EventHandler
    public void OnVillagerCareerChange(org.bukkit.event.entity.VillagerCareerChangeEvent event) {
        Villager.Profession profession = event.getProfession();
        if (profession == Villager.Profession.NITWIT) {
            return;
        }

        Villager villager = event.getEntity();
        if (villager.getVillagerExperience() != 0 || villager.getVillagerLevel() > 1) {
            return;
        }


        PersistentDataContainer villagerPersistentDataContainer = villager.getPersistentDataContainer();
        if (event.getReason() == VillagerCareerChangeEvent.ChangeReason.EMPLOYED) {
            NamespacedKey tradesNamespacedKey = new NamespacedKey(plugin, "VillagerTrades-" + profession);
            if (villagerPersistentDataContainer.has(tradesNamespacedKey, VillagerTradesDataType.Instance)) {
                List<MerchantRecipe> oldRecipes = villagerPersistentDataContainer.get(tradesNamespacedKey, VillagerTradesDataType.Instance);
                assert oldRecipes != null;

                logVillager(villager, "Non traded villager got old profession. Enqueue trades info restoring to prevent trade rolling. Villager {0} at {1}, {2}, {3}");
                Bukkit.getScheduler().runTask(plugin, () -> {
                    villager.setRecipes(oldRecipes);
                    logVillager(villager, "Non traded villager got old profession. Trades info restored to prevent trade rolling. Villager {0} at {1}, {2}, {3}");
                });
            }
        } else {
            NamespacedKey tradesNamespacedKey = new NamespacedKey(plugin, "VillagerTrades-" + villager.getProfession());
            villagerPersistentDataContainer.set(tradesNamespacedKey, VillagerTradesDataType.Instance, villager.getRecipes());

            logVillager(villager, "Villager loose profession. Trades info stored to prevent trade rolling. Villager {0} at {1}, {2}, {3}");
        }
    }

    private void logVillager(Villager villager, String text) {
        Location location = villager.getLocation();
        logger.log(Level.INFO, text,
                new Object[]{
                        villager.getName(),
                        location.getBlockX(),
                        location.getBlockY(),
                        location.getBlockZ()
                });
    }
}

