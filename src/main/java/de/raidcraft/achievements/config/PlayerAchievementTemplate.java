package de.raidcraft.achievements.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.trigger.Trigger;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author mdoering
 */
public class PlayerAchievementTemplate extends YAMLAchievementTemplate<Player> {

    public PlayerAchievementTemplate(@NonNull String name, ConfigurationSection config) {

        super(name, config);
    }

    @Override
    public boolean processTrigger(Player entity, Trigger trigger) {

        AchievementHolder<Player> holder = RaidCraft.getComponent(AchievementPlugin.class).getAchievementManager().getAchievementHolder(entity);
        return !holder.hasGainedAchievement(this) && super.processTrigger(entity, trigger);
    }

    @Override
    protected Collection<Requirement<Player>> getApplicableRequirements() {

        return getRequirements(Player.class);
    }

    @Override
    public Achievement<Player> createAchievement(Player entity) {

        return createAchievement(RaidCraft.getComponent(AchievementManager.class).getAchievementHolder(entity));
    }

    @Override
    protected Collection<Requirement<Player>> loadRequirements() {

        return ActionAPI.createRequirements(getListenerId(), config.getConfigurationSection("requirements"), getTriggerEntityType());
    }

    @Override
    protected Collection<Action<Player>> loadActions() {

        return ActionAPI.createActions(config.getConfigurationSection("actions"), getTriggerEntityType());
    }

    @Override
    public Class<Player> getTriggerEntityType() {

        return Player.class;
    }
}
