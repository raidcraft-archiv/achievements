package de.raidcraft.achievements.api.achievement;

import de.raidcraft.achievements.api.requirement.Requirement;
import lombok.Data;
import lombok.NonNull;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;

/**
 * @author mdoering
 */
@Data
public abstract class AbstractAchievement<T> implements Achievement<T> {

    @NonNull
    private final int id;
    @NonNull
    private final AchievementHolder<T> holder;
    @NonNull
    private final AchievementTemplate template;
    @NonNull
    private final Collection<Requirement<T>> applicableRequirements;
    private Timestamp gainedDate;

    @SuppressWarnings("unchecked")
    public AbstractAchievement(int id, AchievementHolder<T> holder, AchievementTemplate template) {

        this.id = id;
        this.holder = holder;
        this.template = template;
        this.applicableRequirements = template.getRequirements(holder.getType().getClass());
    }

    @Override
    public void unlock() {

        setGainedDate(Timestamp.from(Instant.now()));
        // TODO: add rewards and stuff
    }

    @Override
    public void remove() {

        setGainedDate(null);
        getHolder().removeAchievement(this);
    }
}
