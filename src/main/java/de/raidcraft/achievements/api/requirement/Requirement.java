package de.raidcraft.achievements.api.requirement;

import de.raidcraft.achievements.api.util.ReflectionUtil;

import java.io.File;

/**
 * @author mdoering
 */
@FunctionalInterface
public interface Requirement<T> {

    public boolean test(T entity, File config);

    public default boolean matchesType(Class<?> entity) {

        return ReflectionUtil.genericClassMatchesType(getClass(), entity);
    }
}
