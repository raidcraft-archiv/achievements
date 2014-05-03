package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import lombok.Data;

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
@Data
@Entity
@Table(name = "achievements_holders")
public class TAchievementHolder {

    public static TAchievementHolder load(UUID uuid, AchievementHolder holder) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementHolder tableEntry = database.find(TAchievementHolder.class, uuid);
        if (tableEntry == null) {
            tableEntry = new TAchievementHolder();
            tableEntry.setName(holder.getDisplayName());
            tableEntry.setUuid(holder.getUniqueIdentifier());
            database.save(tableEntry);
        }
        return tableEntry;
    }

    public static <T> void save(AchievementHolder<T> holder) {

        holder.getAchievements().forEach(Achievement::save);
        TAchievementHolder entry = load(holder.getUniqueIdentifier(), holder);
        entry.setName(holder.getDisplayName());
        RaidCraft.getDatabase(AchievementPlugin.class).update(entry);
    }

    public static <T> void delete(AchievementHolder<T> holder) {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(load(holder.getUniqueIdentifier(), holder));
    }

    @Id
    private int id;
    @Column(unique = true)
    private UUID uuid;
    @Column(unique = true)
    private String name;
    @JoinColumn(name = "holder_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TAchievement> achievements = new ArrayList<>();
}
