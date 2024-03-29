package de.raidcraft.achievements.api;

import de.raidcraft.api.action.TriggerFactory;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.TriggerListenerConfigWrapper;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
@ToString(exclude = {"requirements", "actions", "trigger"})
@EqualsAndHashCode(of = {"identifier"})
@Data
public abstract class AbstractAchievementTemplate<T> implements AchievementTemplate<T> {

    @NonNull
    private final String identifier;
    @NonNull
    private final String displayName;
    @NonNull
    private Collection<Requirement<T>> requirements = new ArrayList<>();
    @NonNull
    private Collection<Action<T>> actions = new ArrayList<>();
    @NonNull
    private Collection<TriggerFactory> trigger = new ArrayList<>();
    @Setter(AccessLevel.PROTECTED)
    private String description = "";
    private int points = 10;
    private boolean enabled = true;
    private boolean secret = false;
    private boolean broadcasting = true;

    public AbstractAchievementTemplate(String identifier) {

        this(identifier, identifier);
    }

    public AbstractAchievementTemplate(String identifier, String displayName) {

        this.identifier = identifier;
        this.displayName = displayName;
    }

    public Collection<Action<?>> getActions() {

        return new ArrayList<>(actions);
    }

    public Collection<Requirement<?>> getRequirements() {

        return new ArrayList<>(requirements);
    }

    protected abstract Collection<Requirement<T>> getApplicableRequirements();

    @Override
    public void registerListeners() {

        if (!isEnabled()) return;
        getTrigger().forEach(factory -> factory.registerListener(this));
    }

    @Override
    public void unregisterListeners() {

        getTrigger().forEach(factory -> factory.unregisterListener(this));
    }

    @Override
    public boolean processTrigger(T entity, TriggerListenerConfigWrapper trigger) {

        if (getApplicableRequirements().isEmpty()) {
            return createAchievement(entity).unlock();
        }
        boolean allUnorderedMatch = false;

        // first lets filter out all ordered and unordered requirements
        List<Requirement<T>> orderedRequirements = getApplicableRequirements().stream()
                .filter(Requirement::isOrdered)
                .sorted()
                .collect(Collectors.toList());
        List<Requirement<T>> unorderedRequirements = getApplicableRequirements().stream()
                .filter(req -> !req.isOrdered())
                .collect(Collectors.toList());

        allUnorderedMatch = true;
        // now we go thru all unordered requirements and test them
        for (Requirement<T> requirement : unorderedRequirements) {
            boolean test = requirement.test(entity);
            // only set to false and not back to true
            // also dont set to false when the withRequirement is optional
            if (allUnorderedMatch && !requirement.isOptional()) {
                allUnorderedMatch = test;
                // we dont break here because we want to check all requirements
                // maybe one is counting or persistent
            }
        }

        // lets now check all of our ordered requirements and abort if one does not match
        for (Requirement<T> requirement : orderedRequirements) {
            if (!requirement.test(entity)) {
                return false;
            }
        }

        return allUnorderedMatch && createAchievement(entity).unlock();
    }

    protected abstract Collection<Requirement<T>> loadRequirements();

    protected abstract Collection<Action<T>> loadActions();

    protected abstract Collection<TriggerFactory> loadTrigger();
}
