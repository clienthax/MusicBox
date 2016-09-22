package uk.co.haxyshideout.musicbox.data.spongedata;

import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.Direction;
import uk.co.haxyshideout.musicbox.data.spongedata.interfaces.IMusicBoxSettings;

import java.util.Optional;

import javax.annotation.Nullable;

public class MusicBoxSettingsData extends AbstractData<MusicBoxSettingsData, ImmutableMusicBoxSettingsData> implements IMusicBoxSettings {

    public static final ValueFactory VALUEFACTORY = Sponge.getRegistry().getValueFactory();

    private MusicBoxKeys.MusicBoxType musicBoxType;
    @Nullable
    private Direction inventoryDirection;
    @Nullable
    private Integer inventorySlot;

    public MusicBoxSettingsData() {
        this(MusicBoxKeys.MusicBoxType.STANDARD, null, null);
    }

    public MusicBoxSettingsData(MusicBoxKeys.MusicBoxType musicBoxType, @Nullable Direction inventoryDirection, @Nullable Integer inventorySlot) {
        this.musicBoxType = musicBoxType;
        this.inventoryDirection = inventoryDirection;
        this.inventorySlot = inventorySlot;
        registerGettersAndSetters();
    }

    @Override
    protected void registerGettersAndSetters() {
        registerFieldGetter(MusicBoxKeys.MUSIC_BOX_TYPE, () -> this.musicBoxType);
        registerFieldSetter(MusicBoxKeys.MUSIC_BOX_TYPE, this::setMusicBoxType);
        registerKeyValue(MusicBoxKeys.MUSIC_BOX_TYPE, this::musicBoxType);

        registerFieldGetter(MusicBoxKeys.INVENTORY_DIRECTION, () -> Optional.ofNullable(this.inventoryDirection));
        registerFieldSetter(MusicBoxKeys.INVENTORY_DIRECTION, this::setInventoryDirection);
        registerKeyValue(MusicBoxKeys.INVENTORY_DIRECTION, this::inventoryDirection);

        registerFieldGetter(MusicBoxKeys.INVENTORY_SLOT, () -> Optional.ofNullable(this.inventorySlot));
        registerFieldSetter(MusicBoxKeys.INVENTORY_SLOT, this::setInventorySlot);
        registerKeyValue(MusicBoxKeys.INVENTORY_SLOT, this::inventorySlot);

    }

    public void setMusicBoxType(MusicBoxKeys.MusicBoxType musicBoxType) {
        this.musicBoxType = musicBoxType;
    }

    @Override
    public Value<MusicBoxKeys.MusicBoxType> musicBoxType() {
        return VALUEFACTORY.createValue(MusicBoxKeys.MUSIC_BOX_TYPE, this.musicBoxType);
    }

    public void setInventoryDirection(Optional<Direction> inventoryDirection) {
        this.inventoryDirection = inventoryDirection.orElse(null);
    }

    @Override
    public OptionalValue<Direction> inventoryDirection() {
        return VALUEFACTORY.createOptionalValue(MusicBoxKeys.INVENTORY_DIRECTION, this.inventoryDirection);
    }

    public void setInventorySlot(Optional<Integer> inventorySlot) {
        this.inventorySlot = inventorySlot.orElse(null);
    }

    @Override
    public OptionalValue<Integer> inventorySlot() {
        return VALUEFACTORY.createOptionalValue(MusicBoxKeys.INVENTORY_SLOT, this.inventorySlot);
    }

    @Override
    public Optional<MusicBoxSettingsData> fill(DataHolder dataHolder, MergeFunction overlap) {
        return Optional.empty();
    }

    @Override
    public Optional<MusicBoxSettingsData> from(DataContainer container) {
        return Optional.empty();
    }

    @Override
    public MusicBoxSettingsData copy() {
        return new MusicBoxSettingsData(this.musicBoxType, this.inventoryDirection, this.inventorySlot);
    }

    @Override
    public ImmutableMusicBoxSettingsData asImmutable() {
        return new ImmutableMusicBoxSettingsData(this.musicBoxType, this.inventoryDirection, this.inventorySlot);
    }

    @Override
    public int getContentVersion() {
        return 0;
    }

    @Override
    public DataContainer toContainer() {
        DataContainer dataContainer = super.toContainer();
        dataContainer = dataContainer.set(MusicBoxKeys.MUSIC_BOX_TYPE.getQuery(), this.musicBoxType.name());
        if(this.inventoryDirection != null) {
            dataContainer = dataContainer.set(MusicBoxKeys.INVENTORY_DIRECTION.getQuery(), this.inventoryDirection.name());
        }
        if(this.inventorySlot != null) {
            dataContainer = dataContainer.set(MusicBoxKeys.INVENTORY_SLOT.getQuery(), this.inventorySlot);
        }
        return dataContainer;
    }

}
