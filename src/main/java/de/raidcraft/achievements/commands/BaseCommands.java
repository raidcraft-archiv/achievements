package de.raidcraft.achievements.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.achievements.AchievementPlugin;
import org.bukkit.command.CommandSender;

/**
 * @author Silthus
 */
public class BaseCommands {

    private final AchievementPlugin plugin;

    public BaseCommands(AchievementPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"achievements", "rca", "erfolge"},
            desc = "Base player command for achievements"
    )
    @NestedCommand(value = PlayerCommands.class, executeBody = false)
    public void achievements(CommandContext args, CommandSender sender) throws CommandException {


    }

    @Command(
            aliases = {"rcaa"},
            desc = "Admin Commands"
    )
    @NestedCommand(AdminCommands.class)
    public void admin(CommandContext args, CommandSender sender) {


    }
}