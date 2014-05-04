package de.raidcraft.achievements.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.api.achievement.Achievement;
import de.raidcraft.api.achievement.AchievementHolder;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class BaseCommands {

    private final AchievementPlugin plugin;

    public BaseCommands(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"achievements", "rca"},
            desc = "Base player command for achievements"
    )
    @NestedCommand(value = PlayerCommands.class, executeBody = true)
    public void achievements(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Only players can execute this command.");
        }
        // TODO: make fancy with custom inventory
        AchievementHolder<Player> holder = plugin.getAchievementManager().getAchievementHolder((Player) sender);
        new PaginatedResult<Achievement<Player>>("Datum: Achievement") {
            @Override
            public String format(Achievement<Player> entry) {

                return ChatColor.YELLOW + "" + entry.getCompletionDate().toLocalDateTime()
                        .format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"))
                        + ": " + ChatColor.AQUA + entry.getDisplayName();
            }
        }.display(sender, holder.getCompletedAchievements().stream()
                .sorted((el, o) -> el.getCompletionDate().compareTo(o.getCompletionDate())).collect(Collectors.toList()),
                args.getFlagInteger('p', 1));
    }

    @Command(
            aliases = {"rcaa"},
            desc = "Admin Commands"
    )
    @NestedCommand(AdminCommands.class)
    public void admin(CommandContext args, CommandSender sender) {


    }
}
