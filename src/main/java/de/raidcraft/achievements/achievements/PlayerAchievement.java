package de.raidcraft.achievements.achievements;

import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.api.achievement.AbstractAchievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.AchievementTemplate;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerAchievement extends AbstractAchievement<Player> {

    public PlayerAchievement(AchievementHolder<Player> holder, AchievementTemplate template) {

        super(holder, template);
    }

    @Override
    public Class<Player> getTriggerEntityType() {

        return Player.class;
    }

    @Override
    public void save() {

        if (isCompleted()) {
            TAchievement.save(this);
            getApplicableRequirements().forEach(requirement -> requirement.delete(getHolder().getType()));
        } else {
            getApplicableRequirements().forEach(Requirement::save);
        }
    }

    @Override
    public void delete() {

        TAchievement.delete(this);
        getApplicableRequirements().forEach(requirement -> requirement.delete(getHolder().getType()));
    }
}