package de.raidcraft.achievements.database;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.AchievementTemplate;
import io.ebean.EbeanServer;
import io.ebean.annotation.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@EqualsAndHashCode(of = {"id"})
@Data
@Entity
@Table(name = "achievements_templates")
public class TAchievementTemplate {

    public static TAchievementTemplate load(AchievementTemplate template) {

        EbeanServer database = RaidCraft.getDatabase(AchievementPlugin.class);
        TAchievementTemplate entry = database.find(TAchievementTemplate.class).where()
                .eq("identifier", template.getIdentifier()).findOne();
        if (entry == null) {
            entry = new TAchievementTemplate();
            entry.setIdentifier(template.getIdentifier());
            database.save(entry);
            save(template, entry);
        }
        return entry;
    }

    public static void save(AchievementTemplate template) {

        save(template, load(template));
    }

    private static void save(AchievementTemplate template, TAchievementTemplate entry) {

        entry.setDisplayName(template.getDisplayName());
        entry.setDescription(template.getDescription());
        entry.setPoints(template.getPoints());
        entry.setEnabled(template.isEnabled());
        entry.setSecret(template.isSecret());
        entry.setBroadcasting(template.isBroadcasting());
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
    private int points;
    private boolean enabled;
    private boolean secret;
    private boolean broadcasting;
}
