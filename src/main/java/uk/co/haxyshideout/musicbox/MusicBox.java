package uk.co.haxyshideout.musicbox;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.args.GenericArguments;
import org.spongepowered.api.util.command.spec.CommandSpec;
import uk.co.haxyshideout.musicbox.commands.GiveRadioCommand;
import uk.co.haxyshideout.musicbox.commands.GiveSongCommand;
import uk.co.haxyshideout.musicbox.commands.PlaySongCommand;
import uk.co.haxyshideout.musicbox.commands.ReloadSongsCommand;
import uk.co.haxyshideout.musicbox.commands.DiscListCommand;
import uk.co.haxyshideout.musicbox.eventhandlers.EventHandler;
import uk.co.haxyshideout.musicbox.store.SongStore;

@Plugin(name = "MusicBox", id = "musicbox", version = "0.1")
public class MusicBox {

    @Inject
    public Game game;
    @Inject
    public Logger logger;
    private static MusicBox instance;
    private SongStore songStore;

    public static MusicBox getInstance() {
        return instance;
    }

    public Game getGame() {
        return game;
    }

    @Listener
    public void onStarted(GamePostInitializationEvent event) {
        instance = this;
        songStore = new SongStore();

        game.getEventManager().registerListeners(this, new EventHandler());

        CommandService commandDispatcher = game.getCommandDispatcher();
        CommandSpec songListSpec = CommandSpec.builder().executor(new DiscListCommand()).build();
        commandDispatcher.register(this, songListSpec, "disclist");

        CommandSpec giveSongSpec = CommandSpec.builder().arguments(
                GenericArguments.onlyOne(GenericArguments.string(Texts.of("songName"))))
                .executor(new GiveSongCommand())
                .build();
        commandDispatcher.register(this, giveSongSpec, "givesong");

        CommandSpec playSongSpec = CommandSpec.builder().arguments(
                GenericArguments.onlyOne(GenericArguments.string(Texts.of("songName"))))
                .executor(new PlaySongCommand())
                .build();
        commandDispatcher.register(this, playSongSpec, "playsong");

        CommandSpec reloadSongsSpec = CommandSpec.builder().permission("musicbox.admin").executor(new ReloadSongsCommand()).build();
        commandDispatcher.register(this, reloadSongsSpec, "reloadsongs");

        CommandSpec radioSongsSpec = CommandSpec.builder().permission("musicbox.radio").executor(new GiveRadioCommand()).build();
        commandDispatcher.register(this, radioSongsSpec, "giveradio");
    }

    public Logger getLogger() {
        return logger;
    }

    public SongStore getSongStore() {
        return songStore;
    }

}
