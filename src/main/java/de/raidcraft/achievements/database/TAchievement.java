package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Entity
@Table(name = "achievements_holder_achievements")
public class TAchievement {

    public static TAchievement load(Achievement achievement) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementTemplate templateEntry = TAchievementTemplate.load(achievement.getTemplate());
        TAchievementHolder holderEntry = TAchievementHolder.load(achievement.getHolder());
        TAchievement entry = database.find(TAchievement.class).where()
                .eq("template_id", templateEntry.getId())
                .eq("holder_id", holderEntry.getId()).findUnique();
        if (entry == null) {
            entry = new TAchievement();
            entry.setTemplate(templateEntry);
            entry.setHolder(holderEntry);
            database.save(entry);
        }
        return entry;
    }

    public static void save(Achievement achievement) {

        TAchievement entry = load(achievement);
        entry.setCompleted(achievement.getCompletionDate());
        RaidCraft.getDatabase(AchievementPlugin.class).update(entry);
    }

    public static void delete(Achievement achievement) {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(load(achievement));
    }

    @Id
    private int id;
    @NotNull
    @ManyToOne
    @Column(name = "holder_id")
    private TAchievementHolder holder;
    @NotNull
    @ManyToOne
    @Column(name = "template_id")
    private TAchievementTemplate template;
    private Timestamp completed;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TAchievementHolder getHolder() {

        return holder;
    }

    public void setHolder(TAchievementHolder holder) {

        this.holder = holder;
    }

    public TAchievementTemplate getTemplate() {

        return template;
    }

    public void setTemplate(TAchievementTemplate template) {

        this.template = template;
    }

    public Timestamp getCompleted() {

        return completed;
    }

    public void setCompleted(Timestamp completed) {

        this.completed = completed;
    }
}
