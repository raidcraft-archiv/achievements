package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"id"})
@Data
@Entity
@Table(name = "achievements_holders")
public class TAchievementHolder {

    public static TAchievementHolder load(AchievementHolder holder) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementHolder tableEntry = database.find(TAchievementHolder.class).where()
                .eq("uuid", holder.getUniqueIdentifier()).findUnique();
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
