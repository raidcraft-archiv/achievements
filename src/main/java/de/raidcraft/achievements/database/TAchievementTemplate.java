package de.raidcraft.achievements.database;

import com.avaje.ebean.EbeanServer;
import com.avaje.ebean.validation.NotNull;
import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.AchievementTemplate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Silthus
 */
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
        RaidCraft.getDatabase(AchievementPlugin.class).save(entry);
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

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getIdentifier() {

        return identifier;
    }

    public void setIdentifier(String identifier) {

        this.identifier = identifier;
    }

    public String getDisplayName() {

        return displayName;
    }

    public void setDisplayName(String displayName) {

        this.displayName = displayName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public int getPoints() {

        return points;
    }

    public void setPoints(int points) {

        this.points = points;
    }

    public boolean isEnabled() {

        return enabled;
    }

    public void setEnabled(boolean enabled) {

        this.enabled = enabled;
    }

    public boolean isSecret() {

        return secret;
    }

    public void setSecret(boolean secret) {

        this.secret = secret;
    }

    public boolean isBroadcasting() {

        return broadcasting;
    }

    public void setBroadcasting(boolean broadcasting) {

        this.broadcasting = broadcasting;
    }
}
