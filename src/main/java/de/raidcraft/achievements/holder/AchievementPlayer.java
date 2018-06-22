package de.raidcraft.achievements.holder;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.api.AbstractAchievementHolder;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementException;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * @author Silthus
 */
public class AchievementPlayer extends AbstractAchievementHolder<Player> {

    public AchievementPlayer(Player type) {

        super(type);
    }

    @Override
    public int getRank() {

        return RaidCraft.getComponent(AchievementManager.class).getRank(this);
    }

    @Override
    protected CaseInsensitiveMap<Achievement<Player>> loadAchievements() {

        CaseInsensitiveMap<Achievement<Player>> map = new CaseInsensitiveMap<>();
        AchievementManager manager = RaidCraft.getComponent(AchievementManager.class);
        TAchievementHolder holder = TAchievementHolder.load(this);
        for (TAchievement achievementEntry : holder.getAchievements()) {
            try {
                AchievementTemplate template = manager.getAchievementTemplate(achievementEntry.getTemplate().getIdentifier());
                Achievement<Player> achievement = template.createAchievement(this);
                achievement.setCompletionDate(achievementEntry.getCompleted());
                map.put(achievement.getIdentifier(), achievement);
            } catch (AchievementException ignored) {
            }
        }
        return map;
    }

    @Override
    public boolean hasPermission(String permission) {

        return getType().hasPermission(permission);
    }

    @Override
    public UUID getUniqueIdentifier() {

        return getType().getUniqueId();
    }

    @Override
    public String getDisplayName() {

        return getType().getName();
    }

    @Override
    public void save() {

        TAchievementHolder.save(this);
    }

    @Override
    public void delete() {

        TAchievementHolder.delete(this);
    }
}
