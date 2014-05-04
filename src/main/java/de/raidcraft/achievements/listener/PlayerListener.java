package de.raidcraft.achievements.listener;

import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Silthus
 */
public class PlayerListener implements Listener {

    private final AchievementPlugin plugin;

    public PlayerListener(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {

        // start listening in on achievement trigger a few seconds after joining
        Bukkit.getScheduler().runTaskLater(plugin, () -> {

            if (!event.getPlayer().isOnline()) {
                return;
            }
            AchievementHolder<Player> holder = plugin.getAchievementManager().getAchievementHolder(
                    event.getPlayer().getUniqueId(), event.getPlayer());
            // this will trigger all achievements to start listening
            plugin.getAchievementManager().getAchievements().forEach(holder::addAchievement);
            holder.getAchievements().forEach(Achievement::registerListeners);
        }, 100L);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        // unregister all achievement listeners
        AchievementHolder<Player> holder = plugin.getAchievementManager().getAchievementHolder(
                event.getPlayer().getUniqueId(), event.getPlayer());
        holder.getAchievements().forEach(Achievement::unregisterListeners);
    }
}
