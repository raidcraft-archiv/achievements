package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.achievements.PlayerAchievement;
import de.raidcraft.achievements.api.*;
import de.raidcraft.achievements.config.PlayerAchievementTemplate;
import de.raidcraft.achievements.config.YAMLAchievementTemplate;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import de.raidcraft.achievements.holder.AchievementPlayer;
import de.raidcraft.api.Component;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.UUIDUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * @author mdoering
 */
public final class AchievementManager implements Component {

    private final AchievementPlugin plugin;
    private final Map<String, AchievementTemplate> registeredTemplates = new CaseInsensitiveMap<>();
    private final Map<Class<?>, Constructor<? extends AchievementHolder<?>>> registeredHolders = new HashMap<>();
    private final Map<Class<?>, Constructor<? extends Achievement<?>>> registeredAchievements = new HashMap<>();
    private final Map<UUID, AchievementHolder<?>> cachedHolders = new HashMap<>();

    protected AchievementManager(AchievementPlugin plugin) {

        this.plugin = plugin;
        registerAchievementHolder(Player.class, AchievementPlayer.class);
        registerAchievement(Player.class, PlayerAchievement.class);
        RaidCraft.registerComponent(AchievementManager.class, this);
        load();
    }

    private void load() {

        loadFiles("", new File(plugin.getDataFolder(), "achievements").listFiles());
        plugin.getLogger().info("Loaded " + registeredTemplates.size() + " achievements...");
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
                .forEach(dir -> loadFiles(base + (base.equals("") ? "" : ".") + dir.getName().toLowerCase(), dir.listFiles()));
    }

    private void loadAchievement(String identifier, File file) {

        try {
            ConfigurationSection config = plugin.configure(new SimpleConfiguration<>(plugin, file));
            config = de.raidcraft.util.ConfigUtil.replacePathReferences(config, identifier);
            if (!identifier.equals("")) identifier += ".";
            identifier += file.getName().toLowerCase();
            identifier = identifier.replace(".yml", "");
            YAMLAchievementTemplate template = new PlayerAchievementTemplate(identifier, config);
            registerAchievementTemplate(template);
            template.registerListeners();
            TAchievementTemplate.save(template);
            plugin.info("loaded template: " + identifier);
        } catch (DuplicateAchievementException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void unload() {

        // first unregister all listeners
        registeredTemplates.values().forEach(AchievementTemplate::unregisterListeners);
        registeredTemplates.clear();
        cachedHolders.clear();
    }

    public void reload() {

        unload();
        load();
    }

    public int getRank(AchievementHolder holder) {

        List<TAchievementHolder> list = plugin.getDatabase().find(TAchievementHolder.class).orderBy("points").findList();
        Collections.reverse(list);
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getUuid().equals(holder.getUniqueIdentifier())) {
                return i + 1;
            }
        }
        return 0;
    }

    public Collection<AchievementTemplate> getAchievements() {

        return new ArrayList<>(registeredTemplates.values());
    }

    public void registerAchievementTemplate(AchievementTemplate template) throws DuplicateAchievementException {

        if (registeredTemplates.containsKey(template.getIdentifier())) {
            throw new DuplicateAchievementException("Template with the displayName " + template.getIdentifier() + " is already registered!");
        }
        registeredTemplates.put(template.getIdentifier(), template);
    }

    public void unregisterAchievementTemplate(AchievementTemplate template) {

        registeredTemplates.remove(template.getIdentifier());
    }

    public AchievementTemplate getAchievementTemplate(String identifier) throws AchievementException {

        if (!registeredTemplates.containsKey(identifier)) {
            throw new AchievementException("No achievement template found: " + identifier);
        }
        return registeredTemplates.get(identifier);
    }

    public AchievementTemplate getAchievementTemplateByName(String displayName) throws AchievementException {

        for (AchievementTemplate template : this.registeredTemplates.values()) {
            if (template.getDisplayName().equalsIgnoreCase(displayName)) {
                return template;
            }
        }
        throw new AchievementException("No achievement template found: " + displayName);
    }

    @SneakyThrows
    public <T> void registerAchievementHolder(Class<T> type, Class<? extends AchievementHolder<T>> clazz) {

        if (registeredHolders.containsKey(type)) {
            throw new AchievementException("Holder type " + clazz.getCanonicalName() + " for " + type.getCanonicalName() + " already exists!");
        }
        Constructor<? extends AchievementHolder<T>> constructor = clazz.getDeclaredConstructor(type);
        registeredHolders.put(type, constructor);
    }

    public void unregisterHolder(Class<?> type) {

        registeredHolders.remove(type);
    }

    public AchievementHolder<Player> getAchievementHolder(Player player) {

        return getAchievementHolder(player.getUniqueId(), player);
    }

    public AchievementHolder<TAchievementHolder> getAchievementHolder(String name) {

        TAchievementHolder holder = plugin.getDatabase().find(TAchievementHolder.class).where().istartsWith("displayName", name).findUnique();
        return getAchievementHolder(holder.getUuid(), holder);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> AchievementHolder<T> getAchievementHolder(@NonNull UUID uuid, @NonNull T type) {

        if (cachedHolders.containsKey(uuid)) {
            return (AchievementHolder<T>) cachedHolders.get(uuid);
        }
        Class<?> aClass = registeredHolders.keySet().stream().filter(clazz -> clazz.isAssignableFrom(type.getClass())).findFirst().get();
        if (aClass == null) {
            throw new AchievementException("No holder for type " + type.getClass().getCanonicalName() + " found!");
        }
        Constructor<? extends AchievementHolder<?>> constructor = registeredHolders.get(aClass);
        constructor.setAccessible(true);
        AchievementHolder<T> holder = (AchievementHolder<T>) constructor.newInstance(type);
        holder.load();
        cachedHolders.put(uuid, holder);
        return holder;
    }

    @SneakyThrows
    public <T> void registerAchievement(Class<T> type, Class<? extends Achievement<T>> clazz) {

        if (registeredAchievements.containsKey(type)) {
            throw new AchievementException("achievement type " + clazz.getCanonicalName() + " for " + type.getCanonicalName() + " already exists!");
        }
        Constructor<? extends Achievement<T>> constructor = clazz.getDeclaredConstructor(AchievementHolder.class, AchievementTemplate.class);
        registeredAchievements.put(type, constructor);
    }

    public void unregisterAchievement(Class<?> type) {

        registeredAchievements.remove(type);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public <T> Achievement<T> getAchievement(@NonNull AchievementHolder<T> holder, @NonNull AchievementTemplate template) {

        if (holder.hasAchievement(template)) {
            return holder.getAchievement(template);
        }
        Class<?> aClass = registeredAchievements.keySet().stream().filter(clazz -> clazz.isAssignableFrom(holder.getType().getClass())).findFirst().get();
        if (aClass == null) {
            throw new AchievementException("No achievement for type " + (holder.getType().getClass().getCanonicalName() + " found!"));
        }
        Constructor<? extends Achievement<?>> constructor = registeredAchievements.get(aClass);
        constructor.setAccessible(true);
        return (Achievement<T>) constructor.newInstance(holder, template);
    }

    @SneakyThrows
    public <T> Achievement<T> getAchievement(@NonNull T type, @NonNull AchievementTemplate template) {

        return getAchievement(getAchievementHolder(UUIDUtil.getUUIDfrom(type), type), template);
    }

    public void clearPlayerCache(Player player) {

        cachedHolders.remove(player.getUniqueId());
    }
}