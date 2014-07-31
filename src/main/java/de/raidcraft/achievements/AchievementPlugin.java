package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.commands.BaseCommands;
import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import de.raidcraft.achievements.listener.AchievementListener;
import de.raidcraft.achievements.listener.PlayerListener;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.requirement.RequirementFactory;
import de.raidcraft.api.action.trigger.TriggerManager;
import lombok.Getter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

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

        registerTrigger();
        registerRequirements();

        achievementManager = new AchievementManager(this);
        registerCommands(BaseCommands.class);
        registerEvents(new PlayerListener(this));
    }

    @Override
    public void disable() {

        getAchievementManager().unload();
    }

    @Override
    public void reload() {

        getAchievementManager().reload();
    }

    private void registerTrigger() {

        TriggerManager.getInstance().registerTrigger(this, new AchievementListener());
    }

    private void registerRequirements() {

        RequirementFactory factory = RaidCraft.getComponent(RequirementFactory.class);

        factory.registerRequirement(this, "holder.has-achievement", (Player player, ConfigurationSection config) -> !getConfig().isSet("achievement")
                || getAchievementManager().getAchievementHolder(player).hasGainedAchievement(getConfig().getString("achievement")));
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
