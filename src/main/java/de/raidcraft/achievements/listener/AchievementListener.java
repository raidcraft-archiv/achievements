package de.raidcraft.achievements.listener;

import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.action.trigger.Trigger;
import de.raidcraft.api.language.Translator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Arrays;

/**
 * @author Silthus
 */
public class AchievementListener extends Trigger implements Listener {

    public AchievementListener() {

        super("achievement", "gain");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAchievementGain(AchievementGainEvent event) {

        // broadcast if wanted
        if (event.getAchievement().getTemplate().isBroadcasting()) {
            Arrays.asList(Bukkit.getOnlinePlayers()).forEach(player -> {
                        if (player.equals(event.getAchievement().getHolder().getType())) return;
                        Translator.msg(AchievementPlugin.class, player, "achievement.broadcast",
                                ChatColor.GREEN + "{0}" + ChatColor.YELLOW + " hat den Erfolg " + ChatColor.BLUE + ChatColor.UNDERLINE +
                                "{1}" + ChatColor.RESET + ChatColor.YELLOW + " erhalten.",
                                event.getAchievement().getHolder().getDisplayName(),
                                event.getAchievement().getDisplayName()
                        );
                    }
            );
        }
        // inform other achievements
        informListeners("gain", event.getAchievement().getHolder().getType(), config ->
                !config.isSet("achievement")
                        || config.getString("achievement").equals(event.getAchievement().getIdentifier()));
    }
}
