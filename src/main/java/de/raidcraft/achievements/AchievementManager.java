package de.raidcraft.achievements;

import de.raidcraft.RaidCraft;
import de.raidcraft.achievements.achievements.PlayerAchievement;
import de.raidcraft.achievements.config.YAMLAchievementTemplate;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.achievements.database.TAchievementTemplate;
import de.raidcraft.achievements.holder.AchievementPlayer;
import de.raidcraft.api.Component;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementException;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.AchievementTemplate;
import de.raidcraft.api.achievement.DuplicateAchievementException;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.UUIDUtil;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        // lets check all online players and reregister their listeners
        for(Player player : Bukkit.getOnlinePlayers()) {
            AchievementHolder<Player> holder = getAchievementHolder(player);
            // this will trigger all achievements to start listening
            plugin.getAchievementManager().getAchievements().forEach(holder::addAchievement);
            holder.getAchievements().forEach(Achievement::registerListeners);
        }
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
            if (!identifier.equals("")) identifier += ".";
            identifier += file.getName().toLowerCase();
            identifier = identifier.replace(".yml", "");
            YAMLAchievementTemplate template = new YAMLAchievementTemplate(identifier, plugin.configure(new SimpleConfiguration<>(plugin, file)));
            registerAchievementTemplate(template);
            TAchievementTemplate.save(template);
            plugin.getLogger().info("loaded template: " + identifier);
        } catch (DuplicateAchievementException e) {
            plugin.getLogger().warning(e.getMessage());
        }
    }

    public void unload() {

        // first unregister all listeners
        for(Player player : Bukkit.getOnlinePlayers()) {
            for(Achievement achievement : getAchievementHolder(player).getAchievements()) {
                achievement.unregisterListeners();;
            }
        }
        registeredTemplates.clear();
        cachedHolders.clear();
    }

    public void reload() {

        unload();
        load();
    }

    public Collection<AchievementTemplate> getAchievements() {

        return new ArrayList<>(registeredTemplates.values());
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

    public AchievementTemplate getAchievementTemplate(String identifier) throws AchievementException {

        if (!registeredTemplates.containsKey(identifier)) {
            throw new AchievementException("No achievement template found: " + identifier);
        }
        return registeredTemplates.get(identifier);
    }

    @SneakyThrows
    public <T> void registerAchievementHolder(Class<T> type, Class<? extends AchievementHolder<T>> clazz) {

        if (registeredHolders.containsKey(type)) {
            throw new AchievementException("Holder type " + clazz.getCanonicalName() + " for " + type.getCanonicalName() + " already exists!");
        }
        try {
            Constructor<? extends AchievementHolder<T>> constructor = clazz.getDeclaredConstructor(type);
            registeredHolders.put(type, constructor);
        } catch (NoSuchMethodException e) {
            throw new AchievementException(e);
        }
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
        try {
            Constructor<? extends AchievementHolder<?>> constructor = registeredHolders.get(aClass);
            constructor.setAccessible(true);
            AchievementHolder<T> holder = (AchievementHolder<T>) constructor.newInstance(type);
            cachedHolders.put(uuid, holder);
            return holder;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AchievementException(e);
        }
    }

    @SneakyThrows
    public <T> void registerAchievement(Class<T> type, Class<? extends Achievement<T>> clazz) {

        if (registeredAchievements.containsKey(type)) {
            throw new AchievementException("achievement type " + clazz.getCanonicalName() + " for " + type.getCanonicalName() + " already exists!");
        }
        try {
            Constructor<? extends Achievement<T>> constructor = clazz.getDeclaredConstructor(AchievementHolder.class, AchievementTemplate.class);
            registeredAchievements.put(type, constructor);
        } catch (NoSuchMethodException e) {
            throw new AchievementException(e);
        }
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
        try {
            Constructor<? extends Achievement<?>> constructor = registeredAchievements.get(aClass);
            constructor.setAccessible(true);
            return (Achievement<T>) constructor.newInstance(holder, template);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AchievementException(e);
        }
    }

    @SneakyThrows
    public <T> Achievement<T> getAchievement(@NonNull T type, @NonNull AchievementTemplate template) {

        return getAchievement(getAchievementHolder(UUIDUtil.getUUIDfrom(type), type), template);
    }
}