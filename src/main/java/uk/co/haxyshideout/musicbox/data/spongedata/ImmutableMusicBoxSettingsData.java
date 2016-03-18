package uk.co.haxyshideout.musicbox.data.spongedata;


import com.google.common.collect.ComparisonChain;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.util.Direction;
import uk.co.haxyshideout.musicbox.data.spongedata.interfaces.IImmutableMusicBoxSettings;

import javax.annotation.Nullable;

public class ImmutableMusicBoxSettingsData extends AbstractImmutableData<ImmutableMusicBoxSettingsData, MusicBoxSettingsData> implements IImmutableMusicBoxSettings {

    public static final ValueFactory VALUEFACTORY = Sponge.getRegistry().getValueFactory();

    private final MusicBoxKeys.MusicBoxType musicBoxType;
    @Nullable
    private final Direction inventoryDirection;
    @Nullable
    private final Integer inventorySlot;

    final ImmutableValue<MusicBoxKeys.MusicBoxType> musicBoxTypeImmutableValue;
    final ImmutableOptionalValue<Direction> inventoryDirectionImmutableValue;
    final ImmutableOptionalValue<Integer> inventorySlotImmutableValue;


    public ImmutableMusicBoxSettingsData(MusicBoxKeys.MusicBoxType musicBoxType, @Nullable Direction inventoryDirection, @Nullable Integer inventorySlot) {
        this.musicBoxType = musicBoxType;
        this.inventoryDirection = inventoryDirection;
        this.inventorySlot = inventorySlot;
        this.musicBoxTypeImmutableValue = VALUEFACTORY.createValue(MusicBoxKeys.MUSIC_BOX_TYPE, this.musicBoxType).asImmutable();
        this.inventoryDirectionImmutableValue = (ImmutableOptionalValue<Direction>) VALUEFACTORY.createOptionalValue(MusicBoxKeys.INVENTORY_DIRECTION, this.inventoryDirection).asImmutable();
        this.inventorySlotImmutableValue = (ImmutableOptionalValue<Integer>) VALUEFACTORY.createOptionalValue(MusicBoxKeys.INVENTORY_SLOT, this.inventorySlot).asImmutable();
        registerGetters();
    }

    @Override
    protected void registerGetters() {
        registerFieldGetter(MusicBoxKeys.MUSIC_BOX_TYPE, () -> this.musicBoxType);
        registerKeyValue(MusicBoxKeys.MUSIC_BOX_TYPE, this::musicBoxType);

        registerFieldGetter(MusicBoxKeys.INVENTORY_DIRECTION, () -> this.inventoryDirection);
        registerKeyValue(MusicBoxKeys.INVENTORY_DIRECTION, this::inventorySlot);

        registerFieldGetter(MusicBoxKeys.INVENTORY_SLOT, () -> this.inventorySlot);
        registerKeyValue(MusicBoxKeys.INVENTORY_SLOT, this::inventorySlot);

    }

    @Override
    public ImmutableValue<MusicBoxKeys.MusicBoxType> musicBoxType() {
        return this.musicBoxTypeImmutableValue;
    }

    @Override
    public ImmutableOptionalValue<Direction> inventoryDirection() {
        return this.inventoryDirectionImmutableValue;
    }

    @Override
    public ImmutableOptionalValue<Integer> inventorySlot() {
        return this.inventorySlotImmutableValue;
    }

    @Override
    public MusicBoxSettingsData asMutable() {
        return new MusicBoxSettingsData(this.musicBoxType, this.inventoryDirection, this.inventorySlot);
    }

    @Override
    public int compareTo(ImmutableMusicBoxSettingsData o) {
        ComparisonChain comparisonChain = ComparisonChain.start()
                .compare(this.musicBoxType, o.musicBoxType);
        if(this.inventoryDirection != null && o.inventoryDirection != null) {
            comparisonChain = comparisonChain.compare(this.inventoryDirection, o.inventoryDirection);
        }
        if(this.inventorySlot != null && o.inventorySlot != null) {
            comparisonChain = comparisonChain.compare(this.inventorySlot, o.inventorySlot);
        }
        return comparisonChain.result();
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
