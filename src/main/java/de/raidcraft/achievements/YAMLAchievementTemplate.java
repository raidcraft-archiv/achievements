package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.api.achievement.AbstractAchievementTemplate;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.config.ConfigurationBase;
import lombok.NonNull;
import org.bukkit.configuration.ConfigurationSection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class YAMLAchievementTemplate extends AbstractAchievementTemplate {

    @NonNull
    private final ConfigurationBase<AchievementPlugin> config;

    public YAMLAchievementTemplate(@NonNull String name, @NonNull ConfigurationBase<AchievementPlugin> config) {

        super(name, config.getString("name", name));
        this.config = config;
        setDescription(config.getString("description", ""));
    }

    @Override
    protected Collection<Requirement<?>> loadRequirements() {

        ConfigurationSection requirements = config.getConfigurationSection("requirements");
        if (requirements == null) return new ArrayList<>();
        return requirements.getKeys(false).stream()
                .map(key -> RaidCraft.getComponent(RequirementFactory.class)
                        .create(requirements.getString(key + ".type"), requirements.getConfigurationSection(key)))
                .collect(Collectors.toList());
    }

    @Override
    protected Collection<Action<?>> loadActions() {

        ConfigurationSection actions = config.getConfigurationSection("actions");
        if (actions == null) return new ArrayList<>();
        return actions.getKeys(false).stream()
                .map(key -> RaidCraft.getComponent(ActionFactory.class)
                        .create(actions.getString(key + ".type"), actions.getConfigurationSection(key)))
                .collect(Collectors.toList());
    }
}
