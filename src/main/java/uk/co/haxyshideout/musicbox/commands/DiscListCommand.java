package uk.co.haxyshideout.musicbox.commands;

import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import uk.co.haxyshideout.musicbox.MusicBox;

public class DiscListCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        MusicBox.getInstance().getSongStore().sendGiveDiscList(src);
        return CommandResult.success();
    }
}
