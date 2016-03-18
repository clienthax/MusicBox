package uk.co.haxyshideout.musicbox;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;
import uk.co.haxyshideout.musicbox.commands.DiscListCommand;
import uk.co.haxyshideout.musicbox.commands.GiveRadioCommand;
import uk.co.haxyshideout.musicbox.commands.GiveSongCommand;
import uk.co.haxyshideout.musicbox.commands.PlaySongCommand;
import uk.co.haxyshideout.musicbox.commands.ReloadSongsCommand;
import uk.co.haxyshideout.musicbox.data.spongedata.ImmutableMusicBoxSettingsData;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxSettingsData;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxSettingsDataBuilder;
import uk.co.haxyshideout.musicbox.eventhandlers.EventHandler;
import uk.co.haxyshideout.musicbox.store.SongStore;

import java.io.File;

@Plugin(name = "MusicBox", id = "uk.co.haxyshideout.musicbox", dependencies = @Dependency(id = "com.xxmicloxx.noteblockapi"))
public class MusicBox {

    @Inject
    public Logger logger;
    @Inject
    @ConfigDir(sharedRoot = false)
    private File configFolder;

    private static MusicBox instance;
    private SongStore songStore;

    public static MusicBox getInstance() {
        return instance;
    }

    public Game getGame() {
        return Sponge.getGame();
    }

    public File getConfigFolder() {
        return configFolder;
    }

    /*
    TODO / IDEAS
    add discs to dung gen?

     */

    @Listener
    public void init(GameInitializationEvent event) {
        Sponge.getDataManager().register(MusicBoxSettingsData.class, ImmutableMusicBoxSettingsData.class, new MusicBoxSettingsDataBuilder());
    }

    @Listener
    public void onStarted(GamePostInitializationEvent event) {
        instance = this;
        songStore = new SongStore();

        getGame().getEventManager().registerListeners(this, new EventHandler());

        registerCommands();
        registerDrops();
    }

    private void registerDrops() {

    }

    private void registerCommands() {
        CommandManager commandDispatcher = getGame().getCommandManager();
        CommandSpec songListSpec = CommandSpec.builder()
                .permission("musicbox.songlist")
                .executor(new DiscListCommand())
                .build();
        commandDispatcher.register(this, songListSpec, "disclist");

        CommandSpec giveSongSpec = CommandSpec.builder().arguments(
                GenericArguments.onlyOne(GenericArguments.string(Text.of("songName"))))
                .permission("musicbox.songlist")
                .executor(new GiveSongCommand())
                .build();
        commandDispatcher.register(this, giveSongSpec, "givesong");

        CommandSpec playSongSpec = CommandSpec.builder().arguments(
                GenericArguments.onlyOne(GenericArguments.string(Text.of("songName"))))
                .permission("musicbox.radio")
                .executor(new PlaySongCommand())
                .build();
        commandDispatcher.register(this, playSongSpec, "playsong");

        CommandSpec radioSongsSpec = CommandSpec.builder()
                .permission("musicbox.radio")
                .executor(new GiveRadioCommand())
                .build();
        commandDispatcher.register(this, radioSongsSpec, "giveradio");

        CommandSpec reloadSongsSpec = CommandSpec.builder()
                .permission("musicbox.admin")
                .executor(new ReloadSongsCommand())
                .build();
        commandDispatcher.register(this, reloadSongsSpec, "reloadsongs");
    }

    public Logger getLogger() {
        return logger;
    }

    public SongStore getSongStore() {
        return songStore;
    }

}
