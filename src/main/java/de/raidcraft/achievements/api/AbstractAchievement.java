package de.raidcraft.achievements.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.api.events.AchievementGainEvent;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

/**
 * @author mdoering
 */
@ToString(of = {"holder", "template"})
@EqualsAndHashCode(of = {"holder", "template"})
@Data
public abstract class AbstractAchievement<T> implements Achievement<T> {

    @NonNull
    private final AchievementHolder<T> holder;
    @NonNull
    private final AchievementTemplate template;
    private Timestamp completionDate;

    @SuppressWarnings("unchecked")
    public AbstractAchievement(AchievementHolder<T> holder, AchievementTemplate template) {

        this.holder = holder;
        this.template = template;
    }

    protected abstract Collection<Requirement<T>> getApplicableRequirements();

    protected abstract Collection<Action<T>> getApplicableActions();

    @Override
    public boolean unlock() {

        if (!getTemplate().isEnabled() && !getHolder().hasPermission("rcachievement.ignore-disabled")) return false;
        // check if achievement is already unlocked
        if (getCompletionDate() != null) return false;
        // add and unlock the achievement before calling the event to avoid an inifinite loop
        getHolder().addAchievement(this);
        this.setCompletionDate(Timestamp.from(Instant.now()));

        // inform other plugins that the holder gained an achievement
        AchievementGainEvent event = new AchievementGainEvent(this);
        RaidCraft.callEvent(event);

        // trigger all applicable actions
        getApplicableActions().forEach(action -> action.accept(getHolder().getType()));
        // and remove all persistent requirements
        getApplicableRequirements().forEach(requirement -> requirement.delete(getHolder().getType()));
        save();
        return true;
    }

    @Override
    public void remove() {

        getHolder().removeAchievement(this);
        delete();
    }
}
