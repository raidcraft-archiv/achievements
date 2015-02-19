package de.raidcraft.achievements.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.config.YAMLAchievementTemplate;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.api.achievement.AchievementTemplate;
import de.raidcraft.api.config.SimpleConfiguration;
import de.raidcraft.util.CommandUtil;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.function.Predicate;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final AchievementPlugin plugin;
    private final File folder;

    public AdminCommands(AchievementPlugin plugin) {

        this.plugin = plugin;
        this.folder = new File(plugin.getDataFolder(), "command-templates");
    }

    @Command(
            aliases = {"reload"},
            desc = "Reloads all achievements from disk."
    )
    @CommandPermissions("rcachievements.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Achievements were reloaded. See console for details...");
    }

    @Command(
            aliases = {"create"},
            desc = "Creates an achievement at the given location.",
            min = 2,
            flags = "m:r:p:sbe"
    )
    @CommandPermissions("rcachievements.achievement.create")
    public void create(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Must be executed as player");
        }
        File file;
        if (args.hasFlag('m')) {
            String path = args.getFlag('m');
            path = path.replace('.', '/');
            file = new File(new File(plugin.getDataFolder(), "achievements/" + path), args.getString(0) + ".yml");
        } else {
            file = new File(folder, args.getString(0) + ".yml");
        }
        SimpleConfiguration<AchievementPlugin> config = new SimpleConfiguration<>(plugin, file);
        config.set("name", args.getJoinedStrings(1));
        config.set("points", args.getFlagInteger('p', 10));

        config.set("enabled", args.hasFlag('e'));
        config.set("broadcasting", !args.hasFlag('b'));
        config.set("secret", args.hasFlag('s'));
        config.set("trigger.0.type", "player.move");
        Location location = ((Player) sender).getLocation();
        config.set("meta.creator", sender.getName());
        config.set("meta.location.world", location.getWorld().getName());
        config.set("meta.location.x", location.getBlockX());
        config.set("meta.location.y", location.getBlockY());
        config.set("meta.location.z", location.getBlockZ());
        config.set("trigger.0.args.world", location.getWorld().getName());
        config.set("trigger.0.args.x", location.getBlockX());
        config.set("trigger.0.args.y", location.getBlockY());
        config.set("trigger.0.args.z", location.getBlockZ());
        config.set("trigger.0.args.radius", args.getFlagInteger('r', 0));
        config.save();

        sender.sendMessage(ChatColor.GREEN + "Created achievement config in " + file.getPath());
        if (args.hasFlag('e') && args.hasFlag('m')) {
            sender.sendMessage(ChatColor.RED + "The achievement will be loaded on the next /rcaa reload!");
        }
    }

    @Command(
            aliases = {"enable"},
            desc = "Enables the given achievement",
            min = 1
    )
    @CommandPermissions("rcachievements.achievement.enable")
    public void enable(CommandContext args, CommandSender sender) throws CommandException {

        AchievementTemplate template = getMatchingTemplate(args, achievement -> !achievement.isEnabled());
        template.setEnabled(true);
        sender.sendMessage(ChatColor.GREEN + "Enabled the achievement: " + ChatColor.UNDERLINE + template.getDisplayName()
                + ChatColor.RESET + "(" + ChatColor.YELLOW + template.getIdentifier() + ChatColor.RESET + ")");
    }

    @Command(
            aliases = {"disable"},
            desc = "Disable the given achievement",
            min = 1
    )
    @CommandPermissions("rcachievements.achievement.disable")
    public void disable(CommandContext args, CommandSender sender) throws CommandException {

        AchievementTemplate template = getMatchingTemplate(args, AchievementTemplate::isEnabled);
        template.setEnabled(false);
        sender.sendMessage(ChatColor.RED + "Disabled the achievement: " + ChatColor.UNDERLINE + template.getDisplayName()
                + ChatColor.RESET + "(" + ChatColor.YELLOW + template.getIdentifier() + ChatColor.RESET + ")");
    }

    private AchievementTemplate getMatchingTemplate(CommandContext args, Predicate<AchievementTemplate> predicate) throws CommandException {

        return plugin.getAchievementManager().getAchievements().stream()
                .filter(predicate)
                .filter(achievement -> achievement.getIdentifier().startsWith(args.getString(0).toLowerCase())
                        || achievement.getDisplayName().toLowerCase().startsWith(args.getJoinedStrings(0).toLowerCase()))
                .findFirst().orElseThrow(() -> new CommandException("No machting achievement with the name " + args.getJoinedStrings(0) + " found!"));
    }

    @Command(
            aliases = {"give", "add"},
            desc = "Gives the achievement to the player.",
            min = 1,
            flags = "p:"
    )
    @CommandPermissions("rcachievements.achievement.give")
    public void give(CommandContext args, CommandSender sender) throws CommandException {

        AchievementTemplate template = getMatchingTemplate(args, achievementTemplate -> true);
        Player player = CommandUtil.grabPlayer(args.getFlag('p'));
        AchievementHolder<Player> holder = plugin.getAchievementManager().getAchievementHolder(player.getUniqueId(), player);
        Achievement<Player> achievement = holder.addAchievement(template);
        achievement.unlock();
        sender.sendMessage(ChatColor.GREEN + player.getName() + " was given the achievement: " + achievement.getDisplayName());
    }

    @Command(
            aliases = {"remove", "take"},
            desc = "Takes the achievement from the player.",
            min = 1,
            flags = "p:"
    )
    @CommandPermissions("rcachievements.achievement.remove")
    public void remove(CommandContext args, CommandSender sender) throws CommandException {

        AchievementTemplate template = getMatchingTemplate(args, achievementTemplate -> true);
        Player player = CommandUtil.grabPlayer(args.getFlag('p'));
        AchievementHolder<Player> holder = plugin.getAchievementManager().getAchievementHolder(player.getUniqueId(), player);
        Achievement<Player> achievement = holder.removeAchievement(template);
        sender.sendMessage(ChatColor.RED + " The achievement '" + achievement.getDisplayName() + "' has been removed from: " + player.getName());
    }

    @Command(
            aliases = {"list"},
            desc = "Lists all available achievements",
            flags = "p:"
    )
    @CommandPermissions("rcachievements.cmd.listall")
    public void list(CommandContext args, CommandSender sender) throws CommandException {

        new PaginatedResult<AchievementTemplate>("Achievement Name [ID]") {
            @Override
            public String format(AchievementTemplate entry) {

                return ChatColor.YELLOW + entry.getDisplayName() + ChatColor.GRAY + " [" + entry.getIdentifier() + "]";
            }
        }.display(sender, plugin.getAchievementManager().getAchievements(), args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"warp", "tp", "teleport"},
            desc = "Warps to the achievement creation location",
            min = 1
    )
    @CommandPermissions("rcachievement.achievement.teleport")
    public void warp(CommandContext args, CommandSender sender) throws CommandException {

        AchievementTemplate template = getMatchingTemplate(args, achievementTemplate -> true);
        if (template instanceof YAMLAchievementTemplate) {
            ((YAMLAchievementTemplate) template).teleport((Player) sender);
        }
    }
}