package de.raidcraft.achievements;

import de.raidcraft.achievements.api.achievement.AchievementTemplate;
import de.raidcraft.achievements.api.achievement.DuplicateAchievementException;
import de.raidcraft.api.config.SimpleConfiguration;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

        loadFiles("", new File(plugin.getDataFolder(), "achievements").listFiles());
    }

    private void loadFiles(String base, File[] files) {

        if (files == null) return;
        List<File> fileList = Arrays.asList(files);
        // lets load all achievements
        fileList.stream()
                .filter(File::isFile)
                .forEach(file -> loadAchievement(base, file));
        // recurse over all sub directories
        fileList.stream()
                .filter(File::isDirectory)
                .forEach(dir -> loadFiles((base.equals("") ? "" : ".") + dir.getName().toLowerCase(), dir.listFiles()));
    }

    private void loadAchievement(String identifier, File file) {

        try {
            if (!identifier.equals("")) identifier += ".";
            identifier += file.getName().toLowerCase();
            registerAchievementTemplate(new YAMLAchievementTemplate(identifier, new SimpleConfiguration<>(plugin, file)));
            plugin.getLogger().info("loaded achievement: " + identifier);
        } catch (DuplicateAchievementException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    private void unload() {

        registeredTemplates.clear();
    }

    public void reload() {

        unload();
        load();
    }

    public void registerAchievementTemplate(AchievementTemplate template) throws DuplicateAchievementException {

        if (registeredTemplates.containsKey(template.getIdentifier())) {
            throw new DuplicateAchievementException("Template with the name " + template.getIdentifier() + " is already registered!");
        }
        registeredTemplates.put(template.getIdentifier(), template);
    }

    public void unregisterAchievementTemplate(AchievementTemplate template) {

        registeredTemplates.remove(template.getIdentifier());
    }
}
