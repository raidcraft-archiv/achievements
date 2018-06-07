package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementException;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.achievements.util.AchievementUtil;
import de.raidcraft.api.chat.AutoCompletionProvider;
import de.raidcraft.util.fanciful.FancyMessage;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class AchievementAutoCompletionProvider extends AutoCompletionProvider {

    public AchievementAutoCompletionProvider() {

        super('!', 1, "Bitte gebe mindestens einen Buchstaben des Achievements an wenn du es mit ![Tab] vervollständigen willst.");
    }

    @Override
    protected List<String> getAutoCompleteList(Player player, @Nullable String message) {

        AchievementHolder<Player> holder = RaidCraft.getComponent(AchievementManager.class).getAchievementHolder(player);
        return holder.getCompletedAchievements().stream()
                .filter(a -> message == null || a.getDisplayName().toLowerCase().startsWith(message.toLowerCase()))
                .map(Achievement::getDisplayName)
                .collect(Collectors.toList());
    }

    @Override
    public FancyMessage autoComplete(Player player, FancyMessage fancyMessage, String item) {

        try {
            AchievementManager achievementManager = RaidCraft.getComponent(AchievementManager.class);
            AchievementHolder<Player> holder = achievementManager.getAchievementHolder(player);
            AchievementTemplate achievement = achievementManager.getAchievementTemplateByName(item);
            return AchievementUtil.getAchievementTooltip(fancyMessage, holder.getAchievement(achievement));
        } catch (AchievementException e) {
            player.sendMessage(ChatColor.RED + e.getMessage());
        }
        return fancyMessage;
    }
}
