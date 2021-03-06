package uk.co.haxyshideout.musicbox.commands;

import com.xxmicloxx.NoteBlockAPI.decoders.nbs.Song;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import uk.co.haxyshideout.musicbox.MusicBox;

import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class GiveSongCommand implements CommandExecutor {

    @Override public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(!(src instanceof Player))
            return CommandResult.success();

        Player player = (Player) src;
        Optional<String> songName = args.getOne("songName");
        if(songName.isPresent()) {
            Optional<Song> song = MusicBox.getInstance().getSongStore().getSong(songName.get());
            if(song.isPresent()) {
                ItemStack record = ItemStack.builder().itemType(ItemTypes.RECORD_CAT).quantity(1).build();
                Text name = Text.of(songName.get());
                record.offer(Keys.DISPLAY_NAME, name);//Set the item name to match the song name
                record.offer(Keys.HIDE_MISCELLANEOUS, true);//Hide the original item name
                player.getInventory().offer(record);
            } else {
                player.sendMessage(Text.of("No song by the name of "+songName.get()+" was found."));
            }
        }

        return CommandResult.success();
    }
}
