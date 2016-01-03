package uk.co.haxyshideout.musicbox.commands;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.text.Text;
import uk.co.haxyshideout.musicbox.MusicBox;
import uk.co.haxyshideout.musicbox.store.SongStore;

public class ReloadSongsCommand implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        SongStore songStore = MusicBox.getInstance().getSongStore();
        songStore.loadSongs();
        src.sendMessage(Text.of("Songs reloading"));
        return CommandResult.success();
    }

}
