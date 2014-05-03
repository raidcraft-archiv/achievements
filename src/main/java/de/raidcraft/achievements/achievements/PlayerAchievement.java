package de.raidcraft.achievements.achievements;

import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.api.achievement.AbstractAchievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.AchievementTemplate;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class PlayerAchievement extends AbstractAchievement<Player> {

    public PlayerAchievement(AchievementHolder<Player> holder, AchievementTemplate template) {

        super(holder, template);
    }

    @Override
    public void save() {

        TAchievement.save(this);
    }

    @Override
    public void delete() {

        TAchievement.delete(this);
    }
}
