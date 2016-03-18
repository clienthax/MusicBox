package uk.co.haxyshideout.musicbox.data.spongedata.interfaces;

import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;
import uk.co.haxyshideout.musicbox.data.spongedata.MusicBoxKeys;

public interface IMusicBoxSettings {

    Value<MusicBoxKeys.MusicBoxType> musicBoxType();
    OptionalValue<Direction> inventoryDirection();
    OptionalValue<Integer> inventorySlot();

}
