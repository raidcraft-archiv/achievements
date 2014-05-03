package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.AchievementTemplate;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Data
@Entity
@Table(name = "achievements_templates")
public class TAchievementTemplate {

    public static TAchievementTemplate load(AchievementTemplate template) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementTemplate entry = database.find(TAchievementTemplate.class).where()
                .eq("identifier", template.getIdentifier()).findUnique();
        if (entry == null) {
            entry = new TAchievementTemplate();
            entry.setIdentifier(template.getIdentifier());
            entry.setDisplayName(template.getDisplayName());
            entry.setDescription(template.getDescription());
            database.save(entry);
        }
        return entry;
    }

    public static void save(AchievementTemplate template) {

        TAchievementTemplate entry = load(template);
        entry.setDisplayName(template.getDisplayName());
        entry.setDescription(template.getDescription());
        RaidCraft.getDatabase(AchievementPlugin.class).update(entry);
    }

    public static void delete(AchievementTemplate template) {

        RaidCraft.getDatabase(AchievementPlugin.class).delete(load(template));
    }

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String identifier;
    private String displayName;
    private String description;
}
