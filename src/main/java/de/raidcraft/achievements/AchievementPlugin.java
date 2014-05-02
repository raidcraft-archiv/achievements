package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.api.achievement.AchievementHolder;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import lombok.Getter;

/**
 * @author mdoering
 */
public class AchievementPlugin extends BasePlugin {

    @Getter
    private AchievementManager achievementManager;

    @Override
    public void enable() {

        achievementManager = new AchievementManager(this);
    }

    @Override
    public void disable() {
        //TODO: implement
    }

    @Override
    public void reload() {

        getAchievementManager().reload();
    }

    private void registerRequirements() {

        RequirementFactory factory = RaidCraft.getComponent(RequirementFactory.class);

        factory.registerRequirement(this, "holder.has-achievement", new Requirement<AchievementHolder<?>>() {
            @Override
            public boolean test(AchievementHolder<?> holder) {

                return holder.hasAchievement(getConfig().getString("achievement"));
            }
        });
    }
}
