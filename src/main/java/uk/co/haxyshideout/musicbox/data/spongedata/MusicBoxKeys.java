package uk.co.haxyshideout.musicbox.data.spongedata;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;

import static org.spongepowered.api.data.DataQuery.of;

import java.util.Optional;

public class MusicBoxKeys {

    public enum MusicBoxType {
        STANDARD,
        CHEST
    }

    private static final TypeToken<MusicBoxKeys.MusicBoxType> MUSIC_BOX_TYPE_TOKEN = new TypeToken<MusicBoxType>() {};
    private static final TypeToken<Value<MusicBoxKeys.MusicBoxType>> VALUE_MUSIC_BOX_TYPE = new TypeToken<Value<MusicBoxType>>() {};
    public static final Key<Value<MusicBoxType>> MUSIC_BOX_TYPE = KeyFactory.makeSingleKey(MUSIC_BOX_TYPE_TOKEN, VALUE_MUSIC_BOX_TYPE, of("musicBoxType"), "musicbox:musicboxtype", "Music Box Type");

    private static final TypeToken<Optional<Direction>> DIRECTION_TYPE_TOKEN = new TypeToken<Optional<Direction>>() {};
    private static final TypeToken<OptionalValue<Direction>> VALUE_DIRECTION = new TypeToken<OptionalValue<Direction>>() {};
    public static final Key<OptionalValue<Direction>> INVENTORY_DIRECTION = KeyFactory.makeOptionalKey(DIRECTION_TYPE_TOKEN, VALUE_DIRECTION, of("invDirection"), "musicbox:invdirection", "Music Box Inv Direction");

    private static final TypeToken<Optional<Integer>> INTEGER_TYPE_TOKEN = new TypeToken<Optional<Integer>>() {};
    private static final TypeToken<OptionalValue<Integer>> VALUE_INTEGER = new TypeToken<OptionalValue<Integer>>() {};
    public static final Key<OptionalValue<Integer>> INVENTORY_SLOT = KeyFactory.makeOptionalKey(INTEGER_TYPE_TOKEN, VALUE_INTEGER, of("invSlot"), "musicbox:invslot", "Music Box Inv Slot");


}
