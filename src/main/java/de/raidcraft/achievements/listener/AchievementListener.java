package de.raidcraft.achievements.listener;

import de.raidcraft.api.achievement.events.AchievementGainEvent;
import de.raidcraft.api.action.trigger.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

/**
 * @author Silthus
 */
public class AchievementListener extends Trigger implements Listener {

    public AchievementListener() {

        super("achievement", "gain");
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onAchievementGain(AchievementGainEvent event) {

        // inform other achievements
        informListeners("gain", event.getAchievement().getHolder().getType(), config ->
                !config.isSet("achievement")
                        || config.getString("achievement").equals(event.getAchievement().getIdentifier()));
    }
}
