package uk.co.haxyshideout.musicbox.data.spongedata;

import com.google.common.reflect.TypeToken;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;

import static org.spongepowered.api.data.DataQuery.of;

public class MusicBoxKeys {

    public enum MusicBoxType {
        STANDARD,
        CHEST
    }

    private static final TypeToken<Value<MusicBoxKeys.MusicBoxType>> VALUE_MUSIC_BOX_TYPE = new TypeToken<Value<MusicBoxType>>() {};
    public static final Key<Value<MusicBoxType>> MUSIC_BOX_TYPE = Key.builder().type(VALUE_MUSIC_BOX_TYPE).id("musicbox:musicboxtype").name("Music Box Type").query(of("musicBoxType")).build();


    private static final TypeToken<OptionalValue<Direction>> VALUE_DIRECTION = new TypeToken<OptionalValue<Direction>>() {};
    public static final Key<OptionalValue<Direction>> INVENTORY_DIRECTION = Key.builder().type(VALUE_DIRECTION).id("musicbox:invdirection").name("Music Box Inv Direction").query(of("invDirection")).build();

    private static final TypeToken<OptionalValue<Integer>> VALUE_INTEGER = new TypeToken<OptionalValue<Integer>>() {};
    public static final Key<OptionalValue<Integer>> INVENTORY_SLOT = Key.builder().type(VALUE_INTEGER).id("musicbox:invslot").name("Music Box Inv Slot").query(of("invSlot")).build();

}
