package de.raidcraft.achievements.api.trigger;

import de.raidcraft.achievements.api.util.ReflectionUtil;

/**
 * @author mdoering
 */
public interface Trigger<T> {

    public default boolean matchesType(Class<?> clazzType) {

        return ReflectionUtil.genericClassMatchesType(getClass(), clazzType);
    }
}
