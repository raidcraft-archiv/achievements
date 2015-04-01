package de.raidcraft.achievements.api.events;

import de.raidcraft.achievements.api.Achievement;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Silthus
 */
public class AchievementGainEvent extends Event {

    @Getter
    private final Achievement achievement;

    public AchievementGainEvent(Achievement achievement) {

        this.achievement = achievement;
    }

    //<-- Handler -->//
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
