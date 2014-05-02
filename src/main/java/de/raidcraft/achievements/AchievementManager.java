package de.raidcraft.achievements;

import de.raidcraft.achievements.api.achievement.AchievementTemplate;
import de.raidcraft.achievements.api.achievement.DuplicateAchievementException;
import de.raidcraft.achievements.api.requirement.Requirement;

import java.util.HashMap;
import java.util.Map;

/**
 * @author mdoering
 */
public class AchievementManager {

    private final AchievementPlugin plugin;
    private final Map<String, AchievementTemplate> registeredTemplates = new HashMap<>(); // TODO: make case insenstive

    protected AchievementManager(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    private void load() {

        // TODO: load from YAML files
    }

    private void unload() {

        registeredTemplates.clear();
    }

    public void reload() {

        unload();
        load();
    }

    public <T> void registerRequirementType(AchievementPlugin plugin, String identifier, Requirement<T> requirement) {


    }

    public void registerAchievementTemplate(AchievementTemplate template) throws DuplicateAchievementException {

        if (registeredTemplates.containsKey(template.getName())) {
            throw new DuplicateAchievementException("Template with the name " + template.getName() + " is already registered!");
        }
        registeredTemplates.put(template.getName(), template);
    }
}
