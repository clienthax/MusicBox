package uk.co.haxyshideout.musicbox.data.spongedata.interfaces;

import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Direction;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxKeys;

public interface IImmutableMusicBoxSettings {

    ImmutableValue<MusicBoxKeys.MusicBoxType> musicBoxType();
    ImmutableOptionalValue<Direction> inventoryDirection();
    ImmutableOptionalValue<Integer> inventorySlot();

}
