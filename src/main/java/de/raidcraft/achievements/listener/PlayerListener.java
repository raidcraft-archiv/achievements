package de.raidcraft.achievements.listener;

import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.achievements.api.events.AchievementGainEvent;
import de.raidcraft.achievements.util.AchievementUtil;
import mkremins.fanciful.FancyMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
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
	public void onAchievementGain(AchievementGainEvent event) {

		// broadcast if wanted
        Achievement achievement = event.getAchievement();
        if (achievement.getTemplate().isBroadcasting()) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                AchievementHolder holder = achievement.getHolder();
                int rank = holder.getRank();
                FancyMessage msg = new FancyMessage(holder.getDisplayName())
                        .color(ChatColor.AQUA)
                        .style(ChatColor.UNDERLINE)
                        .formattedTooltip(
                                new FancyMessage("#" + rank + " ")
                                        .color(rank < 4 ? (rank < 3 ? (rank < 2 ? ChatColor.GOLD : ChatColor.GRAY) : ChatColor.RED) : ChatColor.AQUA)
                                        .then(holder.getDisplayName()).color(ChatColor.YELLOW),
                                new FancyMessage(holder.getTotalPoints() + "")
                                        .color(ChatColor.AQUA)
                                        .then(" Erfolgspunkte").color(ChatColor.YELLOW),
                                new FancyMessage(holder.getCompletedAchievements().size() + "")
                                        .color(ChatColor.DARK_AQUA)
                                        .then("/").color(ChatColor.YELLOW)
                                        .then(plugin.getAchievementManager().getAchievements().size() + "").color(ChatColor.AQUA)
                                        .then(" Erfolge").color(ChatColor.YELLOW)
                        ).then(" hat den Erfolg ").color(ChatColor.GREEN);
                msg = AchievementUtil.getAchievementTooltip(msg, achievement);
                msg.then(" erhalten.").color(ChatColor.GREEN).send(player);
                /*
                if (player.equals(holder.getType())) {
                    //					Translator.msg(AchievementPlugin.class, player, "achievement.get",
                    //                            ChatColor.YELLOW + "Du hast den Erfolg "
                    //		                            + ChatColor.GREEN + "[%1s] "
                    //                                    + ChatColor.AQUA + "%2s "
                    //                                    + ChatColor.YELLOW + "erhalten.",
                    //							event.getAchievement().getDisplayName(),
                    //							event.getAchievement().getTemplate().getDescription()
                    getAchievementTooltip(new FancyMessage("Du hast den Erfolg ")
                            .color(ChatColor.GREEN), achievement)
                            .then(" erhalten.").color(ChatColor.GREEN).send(player);
                } else {
                    //					Translator.msg(AchievementPlugin.class, player, "achievement.broadcast",
                    //                            ChatColor.AQUA + "%1s "
                    //                                    + ChatColor.YELLOW + "hat den Erfolg " +
                    //		                            ChatColor.GREEN + "[%2s] "
                    //                                    + ChatColor.AQUA + "%3s "
                    //                                    + ChatColor.YELLOW + "erhalten.",
                    //							event.getAchievement().getHolder().getDisplayName(),
                    //							event.getAchievement().getDisplayName(),
                    //							event.getAchievement().getTemplate().getDescription()

                }*/
            }
		}
	}

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {

        plugin.getAchievementManager().clearPlayerCache(event.getPlayer());
    }
}
