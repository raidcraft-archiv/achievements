package de.raidcraft.achievements.holder;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementManager;
import de.raidcraft.achievements.achievements.PlayerAchievement;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.api.achievement.AbstractAchievementHolder;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementException;
import de.raidcraft.api.achievement.AchievementTemplate;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class AchievementPlayer extends AbstractAchievementHolder<Player> {

    public AchievementPlayer(Player type) {

        super(type);
    }

    @Override
    protected CaseInsensitiveMap<Achievement<Player>> loadAchievements() {

        AchievementManager manager = RaidCraft.getComponent(AchievementManager.class);
        TAchievementHolder holder = TAchievementHolder.load(this);
        return new CaseInsensitiveMap<>(holder.getAchievements().stream()
                .map(entry -> {
                    try {
                        return manager.getAchievementTemplate(entry.getTemplate().getIdentifier());
                    } catch (AchievementException e) {
                        RaidCraft.LOGGER.warning("loading player (" + getDisplayName() + ":" + getUniqueIdentifier() + "): " + e.getMessage());
                    }
                    return null;
                }).filter(entry -> entry != null)
                .collect(Collectors.toMap(AchievementTemplate::getIdentifier, template -> new PlayerAchievement(this, template))));
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
