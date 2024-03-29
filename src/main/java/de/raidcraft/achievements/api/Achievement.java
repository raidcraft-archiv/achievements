package de.raidcraft.achievements.api;

import java.sql.Timestamp;

/**
 * Represents the achievement attached to the achievement holder (e.g. player).
 * An achievement is created from an {@link AchievementTemplate} when it interacts with
 * a valid {@link AchievementHolder}. At this point it should always be saved in a
 * database, even if the achievement was not yet unlocked.
 * The progress of the achivement is also saved in this object.
 */
public interface Achievement<T> {

    /**
     * @see AchievementTemplate#getIdentifier()
     */
    default String getIdentifier() {

        return getTemplate().getIdentifier();
    }

    /**
     * @see AchievementTemplate#getDisplayName()
     */
    default String getDisplayName() {

        return getTemplate().getDisplayName();
    }

    /**
     * Gets the holder of the achievement the template is assigned to.
     *
     * @return achievement holder
     *
     * @see AchievementHolder
     */
    AchievementHolder<T> getHolder();

    /**
     * Gets the template of the achievement. The template holds all the information
     * and is loaded once per config file.
     *
     * @return achievement template
     *
     * @see AchievementTemplate
     */
    AchievementTemplate getTemplate();

    /**
     * Checks if the achievement is active and needs to be checked for requirements
     * and trigger. Achievements that are completed are removed from the active list.
     *
     * @return list of active achievements
     */
    default boolean isActive() {

        return getCompletionDate() == null;
    }

    /**
     * Checks if the achievements was completed and is no longer active.
     *
     * @return true if achievement was completed
     */
    default boolean isCompleted() {

        return getCompletionDate() != null;
    }

    void setCompletionDate(Timestamp timestamp);

    /**
     * Gets the time the achievement was gained. Can be null if the achievement
     * is still active and not yet finished.
     *
     * @return completion time of the achievement. can be null if not completed
     */
    Timestamp getCompletionDate();

    boolean unlock();

    void remove();

    void save();

    void delete();
}
