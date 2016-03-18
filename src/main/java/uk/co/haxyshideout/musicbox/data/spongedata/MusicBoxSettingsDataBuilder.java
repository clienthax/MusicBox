package uk.co.haxyshideout.musicbox.data.spongedata;

import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.util.Direction;

import java.util.Optional;

public class MusicBoxSettingsDataBuilder extends AbstractDataBuilder<MusicBoxSettingsData> implements DataManipulatorBuilder<MusicBoxSettingsData,ImmutableMusicBoxSettingsData> {

    public MusicBoxSettingsDataBuilder() {
        super(MusicBoxSettingsData.class, 0);
    }

    @Override
    public MusicBoxSettingsData create() {
        return new MusicBoxSettingsData();
    }

    @Override
    public Optional<MusicBoxSettingsData> createFrom(DataHolder dataHolder) {
        return create().fill(dataHolder);
    }

    @Override
    protected Optional<MusicBoxSettingsData> buildContent(DataView container) throws InvalidDataException {
        MusicBoxSettingsData musicBoxSettingsData = create();

        if(container.contains(MusicBoxKeys.MUSIC_BOX_TYPE.getQuery())) {
            MusicBoxKeys.MusicBoxType musicBoxType = MusicBoxKeys.MusicBoxType.valueOf(container.getString(MusicBoxKeys.MUSIC_BOX_TYPE.getQuery()).get());
            musicBoxSettingsData = musicBoxSettingsData.set(MusicBoxKeys.MUSIC_BOX_TYPE, musicBoxType);//TODO Might explode
        }
        if(container.contains(MusicBoxKeys.INVENTORY_DIRECTION.getQuery())) {
            Direction direction = Direction.valueOf(container.getString(MusicBoxKeys.INVENTORY_DIRECTION.getQuery()).get());
            musicBoxSettingsData = musicBoxSettingsData.set(MusicBoxKeys.INVENTORY_DIRECTION, Optional.of(direction));//TODO Might explode
        }
        if(container.contains(MusicBoxKeys.INVENTORY_SLOT.getQuery())) {
            musicBoxSettingsData = musicBoxSettingsData.set(MusicBoxKeys.INVENTORY_SLOT, Optional.of(container.getInt(MusicBoxKeys.INVENTORY_SLOT.getQuery()).get()));//TODO Might explode
        }

        return Optional.of(musicBoxSettingsData);
    }



}
