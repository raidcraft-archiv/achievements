package de.raidcraft.achievements.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.api.achievement.AbstractAchievementTemplate;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.action.action.Action;
import de.raidcraft.api.action.action.ActionException;
import de.raidcraft.api.action.action.ActionFactory;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.action.requirement.RequirementException;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Silthus
 */
public class YAMLAchievementTemplate extends AbstractAchievementTemplate {

    @NonNull
    private final ConfigurationSection config;

    public YAMLAchievementTemplate(@NonNull String name, ConfigurationSection config) {

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

        try {
            return RaidCraft.getComponent(RequirementFactory.class).createRequirements(getIdentifier(), config.getConfigurationSection("requirements"));
        } catch (RequirementException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in " + getIdentifier());
        }
        return new ArrayList<>();
    }

    @Override
    protected Collection<Action<?>> loadActions() {

        try {
            return RaidCraft.getComponent(ActionFactory.class).createActions(config.getConfigurationSection("actions"));
        } catch (ActionException e) {
            RaidCraft.LOGGER.warning(e.getMessage() + " in " + getIdentifier());
        }
        return new ArrayList<>();
    }

    @Override
    protected Collection<TriggerFactory> loadTrigger() {

        Collection<TriggerFactory> trigger = TriggerManager.getInstance().createTriggerFactories(config.getConfigurationSection("trigger"));
        if (trigger.isEmpty()) {
            RaidCraft.LOGGER.warning("The achievement " + getIdentifier() + " has no trigger defined!");
        }
        return trigger;
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
}
