package de.raidcraft.achievements;

import java.io.File;

/**
 * @author mdoering
 */
public class AchievementPlugin {

    public static void main(String[] args) {

        new AchievementPlugin();
    }

    public AchievementPlugin() {

        AchievementManager manager = new AchievementManager(this);

        manager.registerRequirementType(this, "location", (String entity, File file) -> entity.length() > file.length());
    }
}
