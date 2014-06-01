package de.raidcraft.achievements.holder;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.database.TAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.api.achievement.AbstractAchievementHolder;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementException;
import de.raidcraft.api.achievement.AchievementTemplate;
import de.raidcraft.util.CaseInsensitiveMap;

import java.util.UUID;

/**
 * @author Silthus
 */
public class OfflineAchievementHolder extends AbstractAchievementHolder<TAchievementHolder> {

    public OfflineAchievementHolder(TAchievementHolder type) {

        super(type);
    }

    @Override
    protected CaseInsensitiveMap<Achievement<TAchievementHolder>> loadAchievements() {

        CaseInsensitiveMap<Achievement<TAchievementHolder>> map = new CaseInsensitiveMap<>();
        AchievementManager manager = RaidCraft.getComponent(AchievementManager.class);

        for (TAchievement achievementEntry : getType().getAchievements()) {
            try {
                AchievementTemplate template = manager.getAchievementTemplate(achievementEntry.getTemplate().getIdentifier());
                Achievement<TAchievementHolder> achievement = template.createAchievement(this);
                achievement.setCompletionDate(achievementEntry.getCompleted());
                map.put(achievement.getIdentifier(), achievement);
            } catch (AchievementException ignored) {
            }
        }
        return map;
    }

    @Override
    public UUID getUniqueIdentifier() {

        return getType().getUuid();
    }

    @Override
    public String getDisplayName() {

        return getType().getDisplayName();
    }

    @Override
    public boolean hasPermission(String permission) {

        return false;
    }

    @Override
    public void save() {

        RaidCraft.getDatabase(AchievementPlugin.class).save(getType());
    }

    @Override
    public void delete() {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(getType());
    }
}
