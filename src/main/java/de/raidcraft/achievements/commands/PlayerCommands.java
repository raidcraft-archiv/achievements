package de.raidcraft.achievements.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.api.Achievement;
import de.raidcraft.achievements.api.AchievementException;
import de.raidcraft.achievements.api.AchievementHolder;
import de.raidcraft.achievements.api.AchievementTemplate;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class PlayerCommands {

    private final AchievementPlugin plugin;

    public PlayerCommands(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"top", "toplist"},
            desc = "Shows the top players",
            flags = "sp:"
    )
    @CommandPermissions("rcachievements.cmd.top")
    public void toplist(CommandContext args, CommandSender sender) throws CommandException {

        List<TAchievementHolder> list = plugin.getRcDatabase().find(TAchievementHolder.class).orderBy("points").findList();
        Collections.reverse(list);
        new PaginatedResult<TAchievementHolder>("Platz. Spieler (Punkte)") {

            @Override
            public String format(TAchievementHolder entry) {

                return String.valueOf(ChatColor.AQUA) + getCount() + ". "
                        + ChatColor.YELLOW + entry.getDisplayName() + ChatColor.AQUA
                        + " (" + ChatColor.GREEN + entry.getPoints() + ChatColor.AQUA + ")";
            }
        }.display(sender, list, args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"list"},
            desc = "Lists gained achievements",
            flags = "p:h:"
    )
    @CommandPermissions("rcachievements.cmd.list")
    public void list(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can execute this command.");
        }
        if (args.hasFlag('h') && !sender.hasPermission("rcachievements.cmd.list.other")) {
            throw new CommandException("You dont have the Permission to show achievements of other players.");
        }
        // TODO: make fancy with custom inventory
        AchievementHolder<?> holder;
        if (args.hasFlag('h')) {
            holder = plugin.getAchievementManager().getAchievementHolder(args.getFlag('h'));
        } else {
            holder = plugin.getAchievementManager().getAchievementHolder((Player) sender);
        }
        new PaginatedResult<Achievement<?>>("Datum: Achievement") {
            @Override
            public String format(Achievement<?> entry) {

                return ChatColor.YELLOW + "" + entry.getCompletionDate().toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        + ": " + ChatColor.AQUA + entry.getDisplayName();
            }
        }.display(sender, holder.getCompletedAchievements().stream()
                        .sorted(Comparator.comparing(Achievement::getCompletionDate)).collect(Collectors.toList()),
                args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"info"},
            desc = "Gives the descirption of a achievement",
            min = 1,
            usage = "<achievement_name>"
    )
    @CommandPermissions("rcachievements.cmd.list")
    public void info(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can execute this command.");
        }
        try {
            String name = args.getJoinedStrings(0);
            AchievementTemplate template = plugin.getAchievementManager().getAchievementTemplateByName(name);
            if (template.isSecret()) {
                throw new AchievementException("secret");
            }
            sender.sendMessage(template.getDescription());
        } catch (AchievementException e) {
            sender.sendMessage("Kein gültiger Erfolg");
        }
    }
}
