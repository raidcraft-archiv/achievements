package de.raidcraft.achievements.api;

import de.raidcraft.api.action.action.ActionHolder;
import de.raidcraft.api.action.requirement.RequirementHolder;
import de.raidcraft.api.action.trigger.TriggerHolder;
import de.raidcraft.api.action.trigger.TriggerListener;

/**
 * The achievement template contains generic information about the achievement.
 * It has no information about the current progress or if it was completed.
 * The achievement templates should be loaded and popuplated on server startup.
 * Depending on the implementation templates can be generated from a database,
 * file system or any other source.
 */
public interface AchievementTemplate<T> extends RequirementHolder, ActionHolder, TriggerHolder, TriggerListener<T> {

    /**
     * Gets the unique displayName of the Achievement that can be used to compare
     * or filter it. The unique displayName is based off the folder structure and file displayName.
     *
     * @return unique displayName of the achievement
     */
    String getIdentifier();

    /**
     * Gets the friendly display displayName of the achievement as it is displayed
     * to the player and in withText messages and outputs.
     *
     * @return friendly display displayName
     */
    String getDisplayName();

    /**
     * Gets the description of the achievement. The description may contain
     * information about how to get the achievement and hints for the players.
     *
     * @return achievement description
     */
    String getDescription();

    /**
     * Gets the amount of points the achievement is worth. This has no effect
     * and is only for toplist calculations and e-penis comparisons :).
     *
     * @return achievement points
     */
    int getPoints();

    /**
     * Sets the achievement as enabled allowing it to be gained by players.
     * This will also enable the listening to triggers for active achievements.
     *
     * @param enabled false to lock achievement
     */
    void setEnabled(boolean enabled);

    /**
     * Checks if the achievement is enabled and can be gained by players.
     * A new achievement must first be enabled before it can be used.
     * Players with the permission "rcachievements.ignore-disabled" will also
     * be able to gain disabled achievements.
     *
     * @return true if enabled. default is false
     */
    boolean isEnabled();

    /**
     * Sets the achievement as secret achievements. Secret achievements dont display their
     * displayName and description but still get broadcasted with their displayName when gained.
     *
     * @param secret true will hide the displayName and description in the overview
     */
    void setSecret(boolean secret);

    /**
     * Checks if the displayName and description of the achievement are hidden in the overview.
     *
     * @return true if achievement is secret. default is false
     */
    boolean isSecret();

    /**
     * Sets the achievement to broadcast to all players when someone gains the achievement.
     * The default is that all achievements are broadcasted.
     *
     * @param broadcasting false disables broadcasting of the achievement
     */
    void setBroadcasting(boolean broadcasting);

    /**
     * Checks if the achievement is broadcasting to all players when someone gains it.
     *
     * @return true if broadcasting is enabled. default is true
     */
    boolean isBroadcasting();

    /**
     * Creates a valid achievement from this template and the given holder.
     *
     * @param holder to create achievement for
     *
     * @return active achievement
     */
    Achievement<T> createAchievement(AchievementHolder<T> holder);

    Achievement<T> createAchievement(T entity);

    /**
     * Registers all trigger listeners of this achievement.
     */
    void registerListeners();

    /**
     * Unregisters all trigger listeners associated with this achievement.
     */
    void unregisterListeners();
}
