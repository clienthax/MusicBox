package uk.co.haxyshideout.musicbox.data.spongedata;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;

import static org.spongepowered.api.data.DataQuery.of;

public class MusicBoxKeys {

    public enum MusicBoxType {
        STANDARD,
        CHEST
    }

    public static final Key<Value<MusicBoxType>> MUSIC_BOX_TYPE = KeyFactory.makeSingleKey(MusicBoxKeys.MusicBoxType.class, Value.class, of("musicBoxType"));
    public static final Key<OptionalValue<Direction>> INVENTORY_DIRECTION = KeyFactory.makeOptionalKey(Direction.class, of("invDirection"));
    public static final Key<OptionalValue<Integer>> INVENTORY_SLOT = KeyFactory.makeOptionalKey(Integer.class, of("invSlot"));

}
