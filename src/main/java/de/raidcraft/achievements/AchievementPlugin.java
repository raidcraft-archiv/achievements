package de.raidcraft.achievements;

import de.raidcraft.achievements.actions.ResetAchievementRequirementsActions;
import de.raidcraft.achievements.commands.BaseCommands;
import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import de.raidcraft.achievements.listener.PlayerListener;
import de.raidcraft.achievements.trigger.AchievementTrigger;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.action.ActionAPI;
import de.raidcraft.api.action.requirement.Requirement;
import de.raidcraft.api.chat.Chat;
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

        Chat.registerAutoCompletionProvider(this, new AchievementAutoCompletionProvider());
        registerActionAPI();

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

	private void registerActionAPI(){
		ActionAPI.register(this)
                .action(new ResetAchievementRequirementsActions())
				.trigger(new AchievementTrigger())
                .requirement(new Requirement<Player>() {
                    @Override
                    @Information(
                            value = "has-achievement",
                            desc = "Checks if the player completed the achievement.",
                            conf = "achievement"
                    )
                    public boolean test(Player player, ConfigurationSection config) {

                        return getAchievementManager().getAchievementHolder(player).hasGainedAchievement(config.getString("achievement"));
                    }
                });
    }

    public List<Class<?>> getDatabaseClasses() {

        List<Class<?>> tables = new ArrayList<>();
        tables.add(TAchievement.class);
        tables.add(TAchievementHolder.class);
        tables.add(TAchievementTemplate.class);
        return tables;
    }
}
