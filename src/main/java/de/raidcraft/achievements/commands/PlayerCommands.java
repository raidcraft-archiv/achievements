package de.raidcraft.achievements.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.achievements.AchievementPlugin;
import de.raidcraft.achievements.database.TAchievementHolder;
import de.raidcraft.util.PaginatedResult;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.List;

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

        List<TAchievementHolder> list = plugin.getDatabase().find(TAchievementHolder.class).orderBy("points").findList();
        new PaginatedResult<TAchievementHolder>("Player\t\t|\t\tPoints") {

            @Override
            public String format(TAchievementHolder entry) {

                return String.valueOf(ChatColor.AQUA) + getCount() + ". "
                        + ChatColor.YELLOW + entry.getName()
                        + "\t\t\t\t" + ChatColor.GREEN + entry.getPoints();
            }
        }.display(sender, list, args.getFlagInteger('p', 1));
    }
}
