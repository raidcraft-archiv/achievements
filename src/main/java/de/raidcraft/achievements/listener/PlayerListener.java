package de.raidcraft.achievements.listener;

import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.language.Translator;
import mkremins.fanciful.FancyMessage;
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
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.equals(event.getAchievement().getHolder().getType())) {
                    Translator.msg(AchievementPlugin.class, player, "achievement.get",
                            new FancyMessage()
                                    .color(ChatColor.YELLOW).text("Du hast den Erfolg ")
                                    .color(ChatColor.GREEN)
                                    .then("[" + event.getAchievement().getDisplayName() + "]")
                                    .color(ChatColor.DARK_PURPLE)
                                    .tooltip(event.getAchievement().getTemplate().getDescription())
                                    .then().color(ChatColor.YELLOW).text(" erhalten.").toJSONString()
                    );
                } else {
                    Translator.msg(AchievementPlugin.class, player, "achievement.broadcast",
                            new FancyMessage()
                                    .color(ChatColor.AQUA).text(event.getAchievement().getHolder().getDisplayName())
                                    .color(ChatColor.YELLOW).text(" hat den Erfolg ")
                                    .color(ChatColor.GREEN)
                                    .then("[" + event.getAchievement().getDisplayName() + "]")
                                    .color(ChatColor.DARK_PURPLE)
                                    .tooltip(event.getAchievement().getTemplate().getDescription())
                                    .then().color(ChatColor.YELLOW).text(" erhalten.").toJSONString()
                    );
                }
            }
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
