package de.raidcraft.achievements.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.AbstractAchievementTemplate;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import de.raidcraft.api.config.ConfigurationBase;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
        setPoints(config.getInt("points", 10));
        setEnabled(config.getBoolean("enabled", isEnabled()));
        setSecret(config.getBoolean("secret", isSecret()));
        setBroadcasting(config.getBoolean("broadcasting", isBroadcasting()));
        // load everything
        setActions(loadActions());
        setRequirements(loadRequirements());
        setTrigger(loadTrigger());
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

    @Override
    protected Collection<TriggerFactory> loadTrigger() {

        ConfigurationSection trigger = config.getConfigurationSection("trigger");
        List<TriggerFactory> list = new ArrayList<>();
        if (trigger != null) {
            list = trigger.getKeys(false).stream()
                    .map(key -> TriggerManager.getInstance().getTrigger(trigger.getString(key + ".type"), trigger.getConfigurationSection(key)))
                    .collect(Collectors.toList());
        }
        if (list.isEmpty()) {
            RaidCraft.LOGGER.warning("The achievement " + getIdentifier() + " has no trigger defined!");
        }
        return list;
    }

    @Override
    public <T> Achievement<T> createAchievement(AchievementHolder<T> holder) {

        return RaidCraft.getComponent(AchievementManager.class).getAchievement(holder, this);
    }

    public String getCreator() {

        return config.getString("meta.creator");
    }

    public void teleport(Player player) {

        if (config.isConfigurationSection("meta.location")) {
            Location location = new Location(
                    Bukkit.getWorld(config.getString("meta.location.world", player.getWorld().getName())),
                    config.getInt("meta.location.x"),
                    config.getInt("meta.location.y"),
                    config.getInt("meta.location.z")
            );
            player.teleport(location);
            player.sendMessage(ChatColor.GREEN + "You have been warped to the location of " + getDisplayName());
        } else {
            player.sendMessage(ChatColor.RED + "No creation location available!");
        }
    }

    @Override
    public void setEnabled(boolean enabled) {

        super.setEnabled(enabled);
        config.set("enabled", enabled);
        config.save();
    }

    @Override
    public void setSecret(boolean secret) {

        super.setSecret(secret);
        config.set("secret", secret);
        config.save();
    }

    @Override
    public void setBroadcasting(boolean broadcasting) {

        super.setBroadcasting(broadcasting);
        config.set("broadcasting", broadcasting);
        config.save();
    }
}
