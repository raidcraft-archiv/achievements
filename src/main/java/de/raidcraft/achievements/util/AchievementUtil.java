package de.raidcraft.achievements.util;

import de.raidcraft.achievements.api.Achievement;
import mkremins.fanciful.FancyMessage;
import org.bukkit.ChatColor;

/**
 * @author Silthus
 */
public class AchievementUtil {


    public static FancyMessage getAchievementTooltip(FancyMessage msg, Achievement achievement) {

        boolean secret = achievement.getTemplate().isSecret();
        FancyMessage description;
        if (secret) {
            description = new FancyMessage("*** ??? *** ??? *** ??? ***")
                    .style(ChatColor.ITALIC)
                    .color(ChatColor.GRAY);
        } else {
            description = new FancyMessage(achievement.getTemplate().getDescription())
                    .style(ChatColor.ITALIC)
                    .color(ChatColor.GREEN);
        }
        return msg.then("[").color(ChatColor.DARK_PURPLE)
                .then(achievement.getDisplayName())
                .color(ChatColor.GOLD)
                .formattedTooltip(
                        new FancyMessage(achievement.getDisplayName())
                                .color(ChatColor.YELLOW)
                                .then(" (+").color(ChatColor.GREEN)
                                .then(achievement.getTemplate().getPoints() + "")
                                .color(ChatColor.AQUA)
                                .then(")").color(ChatColor.GREEN),
                        description
                ).then("]").color(ChatColor.DARK_PURPLE);
    }
}
