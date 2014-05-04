package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "achievements_holders")
public class TAchievementHolder {

    public static TAchievementHolder load(AchievementHolder holder) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementHolder tableEntry = database.find(TAchievementHolder.class).where()
                .eq("uuid", holder.getUniqueIdentifier().toString()).findUnique();
        if (tableEntry == null) {
            tableEntry = new TAchievementHolder();
            tableEntry.setName(holder.getDisplayName());
            tableEntry.setUuid(holder.getUniqueIdentifier().toString());
            tableEntry.setPoints(holder.getTotalPoints());
            database.save(tableEntry);
        }
        return tableEntry;
    }

    public static <T> void save(AchievementHolder<T> holder) {

        holder.getAchievements().forEach(Achievement::save);
        TAchievementHolder entry = load(holder);
        entry.setName(holder.getDisplayName());
        entry.setPoints(holder.getTotalPoints());
        RaidCraft.getDatabase(AchievementPlugin.class).update(entry);
    }

    public static <T> void delete(AchievementHolder<T> holder) {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(load(holder));
    }

    @Id
    private int id;
    @Column(unique = true)
    private String uuid;
    private String name;
    private int points;
    @JoinColumn(name = "holder_id")
    @OneToMany(cascade = CascadeType.REMOVE)
    private List<TAchievement> achievements = new ArrayList<>();

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getUuid() {

        return uuid;
    }

    public void setUuid(String uuid) {

        this.uuid = uuid;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public int getPoints() {

        return points;
    }

    public void setPoints(int points) {

        this.points = points;
    }

    public List<TAchievement> getAchievements() {

        return achievements;
    }

    public void setAchievements(List<TAchievement> achievements) {

        this.achievements = achievements;
    }
}
