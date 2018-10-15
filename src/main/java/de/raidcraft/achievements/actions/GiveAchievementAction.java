package de.raidcraft.achievements.actions;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.api.AchievementException;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.api.action.action.Action;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

public class GiveAchievementAction implements Action<Player> {

    @Information(
            value = "achievement",
            aliases = {"achievement.give"},
            desc = "Gives the player the achievement with the id.",
            conf = {
                    "achievement: of the achievement"
            }
    )
    @Override
    public void accept(Player player, ConfigurationSection config) {

        try {
            AchievementManager achievementManager = RaidCraft.getComponent(AchievementManager.class);
            AchievementTemplate achievement = achievementManager.getAchievementTemplate(config.getString("achievement"));
            achievementManager.getAchievementHolder(player).addAchievement(achievement).unlock();
        } catch (AchievementException e) {
            e.printStackTrace();
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
    }
}
