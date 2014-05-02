package de.raidcraft.achievements.api.achievement;

import de.raidcraft.achievements.api.requirement.RequirementHolder;

/**
 * The achievement template contains generic information about the achievement.
 * It has no information about the current progress or if it was completed.
 * The achievement templates should be loaded and popuplated on server startup.
 * Depending on the implementation templates can be generated from a database,
 * file system or any other source.
 *
 * Templates need to be registered with the {@link de.raidcraft.achievements.AchievementManager#registerAchievementTemplate(AchievementTemplate)}
 */
public interface AchievementTemplate extends RequirementHolder {

    /**
     * Gets the unique name of the Achievement that can be used to compare
     * or filter it. The unique name is based off the folder structure and file name.
     *
     * @return unique name of the achievement
     */
    public String getName();

    /**
     * Gets the friendly display name of the achievement as it is displayed
     * to the player and in text messages and outputs.
     *
     * @return friendly display name
     */
    public String getDisplayName();

    /**
     * Gets the description of the achievement. The description may contain
     * information about how to get the achievement and hints for the players.
     *
     * @return achievement description
     */
    public String getDescription();
}
