package uk.co.haxyshideout.musicbox.commands;

import com.xxmicloxx.NoteBlockAPI.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import uk.co.haxyshideout.musicbox.MusicBox;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

public class PlaySongCommand implements CommandExecutor {

    private final HashMap<UUID, RadioSongPlayer> radioSongPlayers = new HashMap<>();

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player))
            return CommandResult.success();
        Player player = (Player) src;
        if(radioSongPlayers.containsKey(player.getUniqueId())) {
            radioSongPlayers.get(player.getUniqueId()).setPlaying(false);
        }
        Optional<String> songName = args.getOne("songName");
        if(songName.isPresent()) {
            Optional<Song> song = MusicBox.getInstance().getSongStore().getSong(songName.get());
            if (song.isPresent()) {
                RadioSongPlayer radioSongPlayer = new RadioSongPlayer(song.get());
                radioSongPlayer.addPlayer(player);
                radioSongPlayer.setAutoDestroy(true);
                radioSongPlayer.setPlaying(true);
                radioSongPlayers.put(player.getUniqueId(), radioSongPlayer);
            }
        }

        return CommandResult.success();
    }
}
