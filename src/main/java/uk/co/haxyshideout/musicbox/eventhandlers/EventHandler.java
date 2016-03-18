package uk.co.haxyshideout.musicbox.eventhandlers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.xxmicloxx.NoteBlockAPI.decoders.nbs.Song;
import com.xxmicloxx.NoteBlockAPI.events.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.players.NoteBlockSongPlayer;
import net.minecraft.inventory.IInventory;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.Jukebox;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.block.tileentity.carrier.TileEntityCarrier;
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
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.chat.ChatTypes;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.Direction;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import uk.co.haxyshideout.musicbox.MusicBox;
import uk.co.haxyshideout.musicbox.commands.PlaySongCommand;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxKeys;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxSettingsData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@SuppressWarnings("deprecation")
public class EventHandler {

    //BiMap lets us look up keys+values in reverse
    private final BiMap<Location<World>, NoteBlockSongPlayer> noteBlockPlayers = HashBiMap.create();

    private static final Direction[] CARDINAL_SET = {
            Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST
    };

    @Listener
    public void onSongEnd(SongEndEvent event) {
        if(event.getSongPlayer() instanceof NoteBlockSongPlayer) {
            Optional<Location<World>> worldLocation = Optional.ofNullable(noteBlockPlayers.inverse().get(event.getSongPlayer()));
            worldLocation.ifPresent(this::playNextSong);
        }
    }

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
    public void onItemInteract(InteractBlockEvent.Secondary event, @First Player player) {
        Optional<ItemStack> itemInHand = player.getItemInHand();
        //noinspection ConstantConditions
        if (itemInHand.isPresent() && itemInHand.get().getItem() == ItemTypes
                .JUKEBOX && itemInHand.get().get(Keys.DISPLAY_NAME).isPresent()) {
            String itemName = TextSerializers.PLAIN.serialize(itemInHand.get().get(Keys.DISPLAY_NAME).get());
            if (itemName.equals("Radio")) {
                event.setCancelled(true);
                MusicBox.getInstance().getSongStore().sendPlaySongList(player);
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

        //Check if the player is sneaking (used to change jukebox modes)
        if (player.get(Keys.IS_SNEAKING).get()) {
            event.setCancelled(true);
            Location<World> worldLocation = event.getTargetBlock().getLocation().get();
            Optional<TileEntity> tileEntityOptional = worldLocation.getTileEntity();
            if (tileEntityOptional.isPresent()) {
                TileEntity tileEntity = tileEntityOptional.get();
                if (tileEntity instanceof Jukebox) {

                    if (noteBlockPlayers.containsKey(worldLocation)) {
                        noteBlockPlayers.get(worldLocation).setPlaying(false);
                        noteBlockPlayers.get(worldLocation).destroy();
                    }

                    MusicBoxSettingsData setting = tileEntity.get(MusicBoxSettingsData.class).orElse(new MusicBoxSettingsData());
                    MusicBoxKeys.MusicBoxType musicBoxType = setting.get(MusicBoxKeys.MUSIC_BOX_TYPE).get();
                    if(musicBoxType == MusicBoxKeys.MusicBoxType.STANDARD) {
                        //Find inventories next to the block
                        boolean foundChest = false;
                        for (Direction direction : CARDINAL_SET) {
                            Optional<TileEntity> teNextToJukeboxOptional = worldLocation.add(direction.toVector3d()).getTileEntity();
                            if(teNextToJukeboxOptional.isPresent()) {

                                //TODO One day. i will be able to do this with the api -.-
                                net.minecraft.tileentity.TileEntity teNextToJukeBox = (net.minecraft.tileentity.TileEntity) teNextToJukeboxOptional.get();
                                if (teNextToJukeBox instanceof IInventory) {
                                    IInventory iInventory = (IInventory) teNextToJukeBox;
                                    TileEntityCarrier tileEntityCarrier = (TileEntityCarrier) teNextToJukeBox;
                                    if(iInventory.getSizeInventory() >= 8) {
                                        player.sendMessage(Text.of(TextColors.AQUA, "Found a ", tileEntityCarrier.getType().getName(), " to the ", direction.name().toLowerCase(), " slots: ", iInventory.getSizeInventory()));

                                        musicBoxType = MusicBoxKeys.MusicBoxType.CHEST;
                                        setting.set(MusicBoxKeys.INVENTORY_DIRECTION, Optional.of(direction));
                                        setting.set(MusicBoxKeys.INVENTORY_SLOT, Optional.of(0));
                                        foundChest = true;
                                        break;
                                    }
                                }
                            }
                        }
                        if(!foundChest) {
                            player.sendMessage(Text.of(TextColors.RED, "No chests detected on sides of jukebox"));
                        }

                    } else if(musicBoxType == MusicBoxKeys.MusicBoxType.CHEST) {
                        musicBoxType = MusicBoxKeys.MusicBoxType.STANDARD;
                    }

                    player.sendMessage(Text.of(TextColors.AQUA, "Set mode to "+musicBoxType.name().toLowerCase()));

                    setting.set(MusicBoxKeys.MUSIC_BOX_TYPE, musicBoxType);
                    boolean successful = tileEntity.offer(setting).isSuccessful();

                }
            }
            return;
        }

        //Check that the item in the players hand is a record and that it has a custom display name
        Optional<ItemStack> stackInHand = player.getItemInHand();
        Location<World> jukeboxLocation = event.getTargetBlock().getLocation().get();
        //Only insert the disc if the jukebox doesn't have a disc inside it
        TileEntity tileEntity = jukeboxLocation.getTileEntity().get();
        MusicBoxSettingsData setting = tileEntity.get(MusicBoxSettingsData.class).orElse(new MusicBoxSettingsData());
        MusicBoxKeys.MusicBoxType musicBoxType = setting.get(MusicBoxKeys.MUSIC_BOX_TYPE).get();
        if (!jukeboxLocation.get(Keys.REPRESENTED_ITEM).isPresent()) {
            if(musicBoxType == MusicBoxKeys.MusicBoxType.STANDARD) {
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
                        noteBlockPlayers.get(jukeboxLocation).destroy();
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
            } else if (musicBoxType == MusicBoxKeys.MusicBoxType.CHEST) {
                player.sendMessage(Text.of(TextColors.AQUA, "This jukebox is currently in chest loading mode!"));
                event.setCancelled(true);

                //Turn off any players at the blocks location first
                if (noteBlockPlayers.containsKey(jukeboxLocation)) {
                    noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
                    noteBlockPlayers.get(jukeboxLocation).destroy();
                }

                playNextSong(jukeboxLocation);
                //TODO start playing in here
            }
        } else {//If the disc is already in the box just turn off music for that block when ejected
            if (noteBlockPlayers.containsKey(jukeboxLocation)) {
                noteBlockPlayers.get(jukeboxLocation).setPlaying(false);
                noteBlockPlayers.get(jukeboxLocation).destroy();
            }
        }

    }

    /**
     * Plays the next song from a chest linked jukebox
     */
    private void playNextSong(Location<World> jukeBoxLocation) {
        Optional<TileEntity> tileEntityOptional = jukeBoxLocation.getTileEntity();
        if(tileEntityOptional.isPresent()) {
            TileEntity tileEntity = tileEntityOptional.get();
            if(tileEntity instanceof Jukebox) {
                MusicBoxSettingsData setting = tileEntity.get(MusicBoxSettingsData.class).orElse(new MusicBoxSettingsData());
                MusicBoxKeys.MusicBoxType musicBoxType = setting.get(MusicBoxKeys.MUSIC_BOX_TYPE).get();
                if (musicBoxType != MusicBoxKeys.MusicBoxType.CHEST) {
                    return;
                }

                Direction invDirection = setting.get(MusicBoxKeys.INVENTORY_DIRECTION).get().get();
                Integer currentPlayingSlot = setting.get(MusicBoxKeys.INVENTORY_SLOT).get().get();

                Optional<TileEntity> inventoryTileEntityOptional = jukeBoxLocation.add(invDirection.toVector3d()).getTileEntity();
                if(inventoryTileEntityOptional.isPresent()) {
                    net.minecraft.tileentity.TileEntity teNextToJukeBox = (net.minecraft.tileentity.TileEntity) inventoryTileEntityOptional.get();
                    if (teNextToJukeBox instanceof IInventory) {
                        IInventory iInventory = (IInventory) teNextToJukeBox;
                        //Make sure we don't npe
                        if(currentPlayingSlot >= iInventory.getSizeInventory()) {
                            currentPlayingSlot = 0;
                        }

                        int newSlot;
                        for (newSlot = currentPlayingSlot; newSlot < iInventory.getSizeInventory(); newSlot++) {
                            ItemStack stackInSlot = (ItemStack) (Object) iInventory.getStackInSlot(newSlot);
                            if(stackInSlot != null) {
                                if (stackInSlot.getItem() == ItemTypes.RECORD_CAT && stackInSlot.get(Keys.DISPLAY_NAME)
                                        .isPresent()) {
                                    //Check that the song exists for the disc name
                                    String songName = TextSerializers.PLAIN.serialize(stackInSlot.get(Keys.DISPLAY_NAME).get());
                                    //String songName = Texts.legacy().to(itemInHand.get().get(Keys.DISPLAY_NAME).get());
                                    Optional<Song> song = MusicBox.getInstance().getSongStore().getSong(songName);
                                    if(song.isPresent()) {

                                        NoteBlockSongPlayer noteBlockSongPlayer = new NoteBlockSongPlayer(song.get());
                                        noteBlockSongPlayer.setNoteBlockLocation(jukeBoxLocation);
                                        noteBlockSongPlayer.setAreaMusic(true);
                                        noteBlockSongPlayer.setAutoDestroy(true);
                                        noteBlockSongPlayer.setPlaying(true);
                                        noteBlockPlayers.put(jukeBoxLocation, noteBlockSongPlayer);

                                        //Send the Now Playing message to everyone within 16 blocks
                                        Collection<Entity> playersInRange = jukeBoxLocation.getExtent().getEntities(entity -> entity instanceof Player
                                                && entity.getLocation().getPosition().distance(jukeBoxLocation.getPosition()) < 16);
                                        for (Entity entity : playersInRange) {
                                            ((Player) entity).sendMessage(ChatTypes.ACTION_BAR, Text.of(TextColors.AQUA, "Now Playing: " + songName));
                                        }


                                        currentPlayingSlot = ++newSlot;
                                        break;
                                    }
                                }
                            } else {
                                //Failed to find a new song, reset to 0
                                if(newSlot == iInventory.getSizeInventory() - 1) {
                                    currentPlayingSlot = 0;
                                }
                            }

                        }


                    }
                }
                //Set new slot back to the jukebox
                setting.set(MusicBoxKeys.INVENTORY_SLOT, Optional.of(currentPlayingSlot));
                tileEntity.offer(setting);

            }
        }

    }

}
