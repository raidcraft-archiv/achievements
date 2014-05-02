package de.raidcraft.achievements.api.achievement;

import de.raidcraft.achievements.api.requirement.Requirement;
import lombok.AccessLevel;
import lombok.Data;
import lombok.NonNull;
import lombok.Setter;

import java.util.Collection;

/**
 * @author mdoering
 */
@Data
public abstract class AbstractAchievementTemplate implements AchievementTemplate {

    @NonNull
    private final String name;
    @NonNull
    private final String displayName;
    @NonNull
    private final Collection<Requirement<?>> requirements;
    @Setter(AccessLevel.PROTECTED)
    private String description = "";

    public AbstractAchievementTemplate(String name) {

        this(name, name);
    }

    public AbstractAchievementTemplate(String name, String displayName) {

        this.name = name;
        this.displayName = displayName;
        this.requirements = loadRequirements();
    }

    protected abstract Collection<Requirement<?>> loadRequirements();
}
