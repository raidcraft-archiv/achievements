package de.raidcraft.achievements.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.AchievementException;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.api.action.action.Action;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 * @author mdoering
 */
public class ResetAchievementRequirementsActions implements Action<Player> {

    @Override
    @Information(
            value = "achievement.reset",
            desc = "Resets all persistent requirements of the achievement.",
            conf = "achievement: <identifier>"
    )
    public void accept(Player player, ConfigurationSection config) {

        try {
            AchievementTemplate achievement = RaidCraft.getComponent(AchievementPlugin.class).getAchievementManager()
                    .getAchievementTemplate(config.getString("achievement"));
            achievement.getRequirements(Player.class).forEach(objectRequirement -> objectRequirement.delete(player));
        } catch (AchievementException e) {
            e.printStackTrace();
        }
    }
}
