package de.raidcraft.achievements.listener;

import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.language.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Arrays;

/**
 * @author Silthus
 */
public class PlayerListener implements Listener {

    private final AchievementPlugin plugin;

    public PlayerListener(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAchievementGain(AchievementGainEvent event) {

        // broadcast if wanted
        if (event.getAchievement().getTemplate().isBroadcasting()) {
            Arrays.asList(Bukkit.getOnlinePlayers()).forEach(player -> {
                        if (player.equals(event.getAchievement().getHolder().getType())) {
                            Translator.msg(AchievementPlugin.class, player, "achievement.get",
                                    ChatColor.YELLOW + "Du hast den Erfolg " + ChatColor.BLUE + ChatColor.UNDERLINE +
                                            "{0}" + ChatColor.RESET + ChatColor.YELLOW + " erhalten.",
                                    event.getAchievement().getDisplayName()
                            );
                        } else {
                            Translator.msg(AchievementPlugin.class, player, "achievement.broadcast",
                                    ChatColor.GREEN + "{0}" + ChatColor.YELLOW + " hat den Erfolg " + ChatColor.BLUE + ChatColor.UNDERLINE +
                                            "{1}" + ChatColor.RESET + ChatColor.YELLOW + " erhalten.",
                                    event.getAchievement().getHolder().getDisplayName(),
                                    event.getAchievement().getDisplayName()
                            );
                        }
                    }
            );
        }
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
