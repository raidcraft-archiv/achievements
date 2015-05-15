package de.raidcraft.achievements.config;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.api.AbstractAchievementTemplate;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.TriggerFactory;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.Collection;

/**
 * @author Silthus
 */
public abstract class YAMLAchievementTemplate<T> extends AbstractAchievementTemplate<T> {

    @NonNull
    protected final ConfigurationSection config;

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
    protected Collection<TriggerFactory> loadTrigger() {

        Collection<TriggerFactory> trigger = ActionAPI.createTrigger(config.getConfigurationSection("trigger"));
        if (trigger.isEmpty()) {
            RaidCraft.LOGGER.warning("The achievement " + getIdentifier() + " has no trigger defined!");
        }
        return trigger;
    }

    @Override
    public Achievement<T> createAchievement(AchievementHolder<T> holder) {

        Achievement<T> achievement = RaidCraft.getComponent(AchievementManager.class).getAchievement(holder, this);
        holder.addAchievement(achievement);
        return achievement;
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
