package uk.co.haxyshideout.musicbox.store;

import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xxmicloxx.NoteBlockAPI.decoders.nbs.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.decoders.nbs.Song;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.service.pagination.PaginationBuilder;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import uk.co.haxyshideout.musicbox.MusicBox;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("ConstantConditions")
public class SongStore {

    //Map of Song names to loaded Songs
    private final HashMap<String, Song> songs = Maps.newHashMap();
    private PaginationBuilder giveSongPaginationBuilder;
    private PaginationBuilder playerRadioPaginationBuilder;

    public SongStore() {
        loadSongs();
    }

    public void loadSongs() {
        MusicBox musicBox = MusicBox.getInstance();
        songs.clear();
        musicBox.getGame().getScheduler().createTaskBuilder().async().execute(() -> {
            File songFolder = new File(musicBox.getConfigFolder(), "NoteBlockSongs");
            if (!songFolder.exists() && !songFolder.mkdirs()) {
                musicBox.getLogger().error("Failed to create NoteBlockSongs folder at "+songFolder.getAbsolutePath());
                return;
            }
            File[] fileList  = songFolder.listFiles();
            musicBox.getLogger().info("Loading "+fileList.length+" songs from folder "+songFolder.getAbsolutePath());
            for (File file : fileList) {
                Song song = NBSDecoder.parse(file);
                String songName = file.getName().substring(0, file.getName().length() - 4);
                songs.put(songName, song);
                musicBox.getLogger().debug("Loaded song "+songName);
            }
            buildPaginatedLists();
        }).submit(musicBox);
    }

    private void buildPaginatedLists() {
        Collection<String> songNames = getSongNames();
        PaginationService paginationService = MusicBox.getInstance().getGame().getServiceManager().provide(PaginationService.class).get();

        List<Text> giveSongCommandTexts = Lists.newArrayList();
        for(String songName : songNames) {
            Text item = Text.builder(songName)
                    .onClick(TextActions.runCommand("/givesong \""+songName+"\""))
                    .color(TextColors.DARK_AQUA)
                    .build();
            giveSongCommandTexts.add(item);
        }

        List<Text> playSongCommandTexts = Lists.newArrayList();
        for(String songName : songNames) {
            Text item = Text.builder(songName)
                    .onClick(TextActions.runCommand("/playsong \""+songName+"\""))
                    .color(TextColors.DARK_AQUA)
                    .build();
            playSongCommandTexts.add(item);
        }

        giveSongPaginationBuilder = paginationService.builder().title(Text.of("Songs")).contents(giveSongCommandTexts);
        playerRadioPaginationBuilder = paginationService.builder().title(Text.of("Songs")).contents(playSongCommandTexts);
    }

    public Optional<Song> getSong(String songName) {
        if(songs.containsKey(songName))
            return Optional.of(songs.get(songName));
        return Optional.empty();
    }

    public Collection<String> getSongNames() {
        List<String> names = new ArrayList<>(songs.keySet());
        Collections.sort(names, (str1, str2) -> ComparisonChain.start()
                .compare(str1, str2, String.CASE_INSENSITIVE_ORDER)
                .compare(str1, str2)
                .result());
        return names;
    }

    //Sends the clickable list to the player
    public void sendGiveDiscList(CommandSource player) {
        giveSongPaginationBuilder.sendTo(player);
    }

    public void sendPlaySongList(CommandSource player) {
        playerRadioPaginationBuilder.sendTo(player);
    }

}
