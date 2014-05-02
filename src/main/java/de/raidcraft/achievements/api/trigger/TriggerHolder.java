package de.raidcraft.achievements.api.trigger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author mdoering
 */
public interface TriggerHolder<T> {

    public Collection<Trigger<?>> getTrigger();

    @SuppressWarnings("unchecked")
    public default Collection<Trigger<T>> getTrigger(Class<?> entityClazz) {

        return getTrigger().stream()
                .filter(trigger -> trigger.matchesType(entityClazz))
                .map(trigger -> (Trigger<T>) trigger)
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
