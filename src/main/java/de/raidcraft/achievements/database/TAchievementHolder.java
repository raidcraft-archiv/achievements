package de.raidcraft.achievements.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementHolder;
import io.ebean.EbeanServer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
@ToString(of = {"id", "uuid", "displayName", "points"})
@EqualsAndHashCode(of = {"id"})
@Data
@Entity
@Table(name = "rc_achievements_holders")
public class TAchievementHolder {

    public static TAchievementHolder load(AchievementHolder holder) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementHolder tableEntry = database.find(TAchievementHolder.class).where()
                .eq("uuid", holder.getUniqueIdentifier()).findOne();
        if (tableEntry == null) {
            tableEntry = new TAchievementHolder();
            tableEntry.setUuid(holder.getUniqueIdentifier());
            tableEntry.setDisplayName(holder.getDisplayName());
            tableEntry.setPoints(holder.getTotalPoints());
            database.save(tableEntry);
        }
        return tableEntry;
    }

    public static void save(AchievementHolder<?> holder) {

        for (Achievement<?> achievement : holder.getAchievements()) {
            achievement.save();
        }
        TAchievementHolder entry = load(holder);
        entry.setDisplayName(holder.getDisplayName());
        entry.setPoints(holder.getTotalPoints());
        RaidCraft.getDatabase(AchievementPlugin.class).update(entry);
    }

    public static void delete(AchievementHolder holder) {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(load(holder));
    }

    @Id
    private int id;
    @Column(unique = true)
    private UUID uuid;
    private String displayName;
    private int points;
    @JoinColumn(name = "holder_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TAchievement> achievements = new ArrayList<>();
}
