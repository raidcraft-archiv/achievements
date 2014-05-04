package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.commands.BaseCommands;
import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mdoering
 */
public class AchievementPlugin extends BasePlugin {

    @Getter
    private AchievementManager achievementManager;

    @Override
    public void enable() {

        achievementManager = new AchievementManager(this);
        registerCommands(BaseCommands.class);
    }

    @Override
    public void disable() {

        getAchievementManager().unload();
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

    @Override
    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TAchievement.class);
        tables.add(TAchievementHolder.class);
        tables.add(TAchievementTemplate.class);
        return tables;
    }
}
