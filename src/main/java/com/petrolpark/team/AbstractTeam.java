package com.petrolpark.team;

import java.util.Map;

import java.util.stream.Stream;

import java.util.function.Predicate;

import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;
import com.petrolpark.team.data.ITeamDataType;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;

import java.util.HashMap;

public abstract class AbstractTeam<T extends ITeam<? super T>> implements ITeam<T> {
    
    protected final Map<ITeamDataType<?>, Object> data = new HashMap<>();

    @Override
    public final boolean isNone() {
        return false;
    };

    @Override
    @SuppressWarnings("unchecked")
    public <DATA> DATA getTeamData(ITeamDataType<? super DATA> dataType) {
        return (DATA)data.computeIfAbsent(dataType, ITeamDataType::getBlankInstance);
    };

    public Stream<ITeamDataType<?>> streamNonBlankTeamData() {
        return data.keySet().stream().dropWhile(this::isBlank);
    };

    public <DT> boolean isBlank(ITeamDataType<DT> dataType) {
        return dataType.isBlank(getTeamData(dataType));
    };

    public CompoundTag saveTeamData(Level level) {
        CompoundTag tag = new CompoundTag();
        for (ITeamDataType<?> dataType : data.keySet()) saveTeamData(level, dataType, tag);
        return tag;
    };

    protected <DT> void saveTeamData(Level level, ITeamDataType<DT> dataType, CompoundTag tag) {
        DT data = getTeamData(dataType);
        if (dataType.isBlank(data)) return;
        tag.put(PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.TEAM_DATA_TYPE).getKey(dataType).toString(), dataType.save(level, data));
    };

    public void loadTeamData(Level level, CompoundTag tag) {
        for (String key : tag.getAllKeys()) {
            if (!tag.contains(key, Tag.TAG_COMPOUND)) continue;
            ITeamDataType<?> dataType = PetrolparkRegistries.getRegistry(PetrolparkRegistries.Keys.TEAM_DATA_TYPE).getValue(ResourceLocation.fromNamespaceAndPath(key));
            if (dataType != null) loadTeamData(level, tag.getCompound(key), dataType); else Petrolpark.LOGGER.warn("Unknown Team Data Type: "+key);
        };
    };

    public <DT> void loadTeamData(Level level, CompoundTag dataTag, ITeamDataType<DT> dataType) {
        data.put(dataType, dataType.load(level, dataTag));
    };

    public void copyTeamData(Level level, AbstractTeam<?> other) {
        copyTeamData(level, other, td -> true);
    };

    public void copyTeamData(Level level, AbstractTeam<?> other, Predicate<ITeamDataType<?>> exclude) {
        data.clear();
        for (ITeamDataType<?> dataType : other.data.keySet()) {
            if (exclude.test(dataType)) continue;
            data.put(dataType, other.getTeamData(dataType));
            setChanged(level, dataType);
        };
    };

};
