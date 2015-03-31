package de.raidcraft.achievements.achievements;

import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.api.AbstractAchievement;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public class PlayerAchievement extends AbstractAchievement<Player> {

    public PlayerAchievement(AchievementHolder<Player> holder, AchievementTemplate template) {

        super(holder, template);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Requirement<Player>> getApplicableRequirements() {

        return getTemplate().getRequirements(Player.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Action<Player>> getApplicableActions() {

        return getTemplate().getRequirements(Player.class);
    }

    @Override
    public void save() {

        if (isCompleted()) {
            TAchievement.save(this);
            getApplicableRequirements().forEach(requirement -> requirement.delete(getHolder().getType()));
        }
    }

    @Override
    public void delete() {

        TAchievement.delete(this);
        getApplicableRequirements().forEach(requirement -> requirement.delete(getHolder().getType()));
    }
}