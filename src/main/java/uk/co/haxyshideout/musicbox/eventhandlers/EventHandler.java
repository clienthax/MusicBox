package uk.co.haxyshideout.musicbox.eventhandlers;

import com.xxmicloxx.NoteBlockAPI.NoteBlockSongPlayer;
import com.xxmicloxx.NoteBlockAPI.Song;
import net.minecraft.block.BlockJukebox;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.mutable.DisplayNameData;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.InteractBlockEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import uk.co.haxyshideout.musicbox.MusicBox;

import java.util.HashMap;
import java.util.Optional;

public class EventHandler {

    private final HashMap<Location<World>, NoteBlockSongPlayer> noteBlockPlayers = new HashMap<>();

    @Listener
    public void onPlayerLogout(ClientConnectionEvent.Disconnect event) {
        if(noteBlockPlayers.containsKey(event.getTargetEntity().getUniqueId()))
            noteBlockPlayers.get(event.getTargetEntity().getUniqueId()).setPlaying(false);
    }

    @Listener
    public void onItemInteract(InteractBlockEvent.Secondary event) {
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            Optional<ItemStack> itemInHand = player.getItemInHand();
            //noinspection ConstantConditions
            if(itemInHand.isPresent() && itemInHand.get().getItem() == ItemTypes
                    .JUKEBOX && itemInHand.get().get(DisplayNameData.class).isPresent ()) {
                String itemName = Texts.legacy().to(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                if(itemName.equals("Radio")) {
                    event.setCancelled(true);
                    MusicBox.getInstance().getSongStore().sendPlaySongList(player);
                }
            }
        }
    }

    @Listener
    public void onInteractWithJukebox(InteractBlockEvent.Secondary event) {
        //If the block interacted with is not a jukebox, return
        //noinspection ConstantConditions
        if (event.getTargetBlock().getState().getType() != BlockTypes.JUKEBOX) {
            return;
        }
        //Check that a player caused the event
        if (event.getCause().first(Player.class).isPresent()) {
            Player player = event.getCause().first(Player.class).get();
            //Check that the item in the players hand is a record and that it has a custom display name
            Optional<ItemStack> itemInHand = player.getItemInHand();
            Location<World> jukeboxLocation = event.getTargetBlock().getLocation().get();
            BlockJukebox.TileEntityJukebox tileEntityJukebox =
                    (BlockJukebox.TileEntityJukebox) jukeboxLocation.getTileEntity().get();
            //Only insert the disc if the jukebox doesn't have a disc inside it
            if (tileEntityJukebox.getRecord() == null) {
                if (itemInHand.isPresent() && itemInHand.get().getItem() == ItemTypes.RECORD_CAT && itemInHand.get().get(DisplayNameData.class)
                        .isPresent()) {
                    event.setCancelled(true);

                    //Set the record into the tile entity, need to do it this way as the normal insert removes the name tag
                    tileEntityJukebox.setRecord((net.minecraft.item.ItemStack) itemInHand.get());

                    //Set the state of the block to having a record
                    BlockPos jukeboxPos = new BlockPos(jukeboxLocation.getBlockX(), jukeboxLocation.getBlockY(), jukeboxLocation.getBlockZ());
                    ((net.minecraft.world.World) event.getTargetBlock().getLocation().get().getExtent())
                            .setBlockState(jukeboxPos, ((IBlockState) event.getTargetBlock().getState())
                                    .withProperty(BlockJukebox.HAS_RECORD, true), 2);


                    //Remove the disc from the players hand
                    player.setItemInHand(null);

                    //Turn off any players at the blocks location first
                    if(noteBlockPlayers.containsKey(jukeboxLocation)) {
                        noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
                    }

                    //Check that the song exists for the disc name
                    String songName = Texts.legacy().to(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                    Optional<Song> song = MusicBox.getInstance().getSongStore().getSong(songName);
                    if(song.isPresent()) {
                        //Set up the player and play the song
                        NoteBlockSongPlayer noteBlockSongPlayer = new NoteBlockSongPlayer(song.get());
                        noteBlockSongPlayer.setNoteBlockLocation(jukeboxLocation);
                        noteBlockSongPlayer.setAreaMusic(true);
                        noteBlockSongPlayer.setPlaying(true);
                        noteBlockPlayers.put(jukeboxLocation, noteBlockSongPlayer);
                        player.sendMessage(ChatTypes.ACTION_BAR, Texts.of("Now Playing: "+songName));
                    }
                }
            } else {
                if(noteBlockPlayers.containsKey(jukeboxLocation))
                    noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
            }
        }
    }

}
