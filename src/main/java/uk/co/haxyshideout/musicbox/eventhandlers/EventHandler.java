package uk.co.haxyshideout.musicbox.eventhandlers;

import com.xxmicloxx.NoteBlockAPI.decoders.nbs.Song;
import com.xxmicloxx.NoteBlockAPI.players.NoteBlockSongPlayer;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import uk.co.haxyshideout.musicbox.MusicBox;
import uk.co.haxyshideout.musicbox.commands.PlaySongCommand;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class EventHandler {

    private final HashMap<Location<World>, NoteBlockSongPlayer> noteBlockPlayers = new HashMap<>();

    @Listener
    public void onPlayerLogout(ClientConnectionEvent.Disconnect event) {
        if (PlaySongCommand.radioSongPlayers.containsKey(event.getTargetEntity().getUniqueId())) {
            PlaySongCommand.radioSongPlayers.get(event.getTargetEntity().getUniqueId()).setPlaying(false);
        }
    }

    //If the jukebox is broke, stop the song playing
    @Listener
    public void onJukeboxBroke(ChangeBlockEvent.Break event) {
        for (Transaction<BlockSnapshot> blockSnapshotTransaction : event.getTransactions()) {
            if (blockSnapshotTransaction.getOriginal().getState().getType() != BlockTypes.JUKEBOX) {
                return;
            }
            Location<World> jukeboxLocation = blockSnapshotTransaction.getOriginal().getLocation().get();
            if (noteBlockPlayers.containsKey(jukeboxLocation)) {
                noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
                noteBlockPlayers.remove(jukeboxLocation);
            }
        }
    }

    @Listener
    public void onItemInteract(InteractBlockEvent.Secondary event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            Optional<ItemStack> itemInHand = player.getItemInHand();
            //noinspection ConstantConditions
            if (itemInHand.isPresent() && itemInHand.get().getItem() == ItemTypes
                    .JUKEBOX && itemInHand.get().get(Keys.DISPLAY_NAME).isPresent()) {
                String itemName = TextSerializers.PLAIN.serialize(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                //String itemName = Text.legacy().to(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                if (itemName.equals("Radio")) {
                    event.setCancelled(true);
                    MusicBox.getInstance().getSongStore().sendPlaySongList(player);
                }
            }
        }
    }

    @Listener
    public void onInteractWithJukebox(InteractBlockEvent.Secondary event, @First Player player) {
        //If the block interacted with is not a jukebox, return
        //noinspection ConstantConditions
        if (event.getTargetBlock().getState().getType() != BlockTypes.JUKEBOX) {
            return;
        }
        //Check that the item in the players hand is a record and that it has a custom display name
        Optional<ItemStack> stackInHand = player.getItemInHand();
        Location<World> jukeboxLocation = event.getTargetBlock().getLocation().get();
        //Only insert the disc if the jukebox doesn't have a disc inside it
        if (!jukeboxLocation.get(Keys.REPRESENTED_ITEM).isPresent()) {
            if (stackInHand.isPresent() && stackInHand.get().getItem() == ItemTypes.RECORD_CAT && stackInHand.get().get(Keys.DISPLAY_NAME)
                    .isPresent()) {
                event.setCancelled(true);

                //Set the record into the tile entity, need to do it this way as the normal insert removes the name tag
                jukeboxLocation.offer(Keys.REPRESENTED_ITEM, stackInHand.get().createSnapshot());

                //Remove the disc from the players hand
                player.setItemInHand(null);

                //Turn off any players at the blocks location first
                if (noteBlockPlayers.containsKey(jukeboxLocation)) {
                    noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
                }

                //Check that the song exists for the disc name
                String songName = TextSerializers.PLAIN.serialize(stackInHand.get().get(Keys.DISPLAY_NAME).get());
                //String songName = Texts.legacy().to(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                Optional<Song> song = MusicBox.getInstance().getSongStore().getSong(songName);
                if (song.isPresent()) {
                    //Set up the player and play the song
                    NoteBlockSongPlayer noteBlockSongPlayer = new NoteBlockSongPlayer(song.get());
                    noteBlockSongPlayer.setNoteBlockLocation(jukeboxLocation);
                    noteBlockSongPlayer.setAreaMusic(true);
                    noteBlockSongPlayer.setLooped(true);
                    noteBlockSongPlayer.setPlaying(true);
                    noteBlockPlayers.put(jukeboxLocation, noteBlockSongPlayer);

                    //Send the Now Playing message to everyone within 16 blocks
                    Collection<Entity> playersInRange = player.getWorld().getEntities(entity -> entity instanceof Player
                            && entity.getLocation().getPosition().distance(jukeboxLocation.getPosition()) < 16);
                    for (Entity entity : playersInRange) {
                        ((Player) entity).sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.AQUA, "Now Playing: " + songName));
                    }

                }
            }
        } else {//If the disc is already in the box just turn off music for that block when ejected
            if (noteBlockPlayers.containsKey(jukeboxLocation)) {
                noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
            }
        }

    }

}
