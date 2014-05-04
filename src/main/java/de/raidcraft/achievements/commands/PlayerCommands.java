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

import java.util.Collections;
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
}
