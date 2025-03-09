package com.petrolpark.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;

import com.petrolpark.PetrolparkRegistries;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class NBTHelper {

    public static boolean equalIgnoring(CompoundTag tag1, CompoundTag tag2, String ...ignoredKeys) {
        if (tag1 == tag2) return true;
        Set<String> checkedKeys = new HashSet<>();
        if (tag1 != null) eachKey: for (String key : tag1.getAllKeys()) {
            checkedKeys.add(key);
            for (String ignoredKey : ignoredKeys) if (key.equals(ignoredKey)) continue eachKey;
            if (tag2 == null || !Objects.equals(tag1.get(key), tag2.get(key))) return false;
        };
        if (tag2 != null) eachKey: for (String key : tag2.getAllKeys()) {
            if (checkedKeys.contains(key)) continue eachKey;
            for (String ignoredKey : ignoredKeys) if (key.equals(ignoredKey)) continue eachKey;
            if (tag1 == null || !Objects.equals(tag1.get(key), tag2.get(key))) return false;
        };
        return true;
    };

    public static <T> List<T> readCompoundList(ListTag tag, Function<CompoundTag, T> reader) {
        return tag.stream().map(CompoundTag.class::cast).map(reader).collect(Collectors.toList());
    };

    public static <T> ListTag writeCompoundList(List<T> list, Function<T, CompoundTag> writer) {
        ListTag tag = new ListTag();
        list.stream().map(writer).forEach(tag::add);
        return tag;
    };

    public static <E extends Enum<E>> E readEnum(CompoundTag tag, String key, Class<E> enumClass) {
        return enumClass.getEnumConstants()[tag.getInt(key)];
    };

    public static <E extends Enum<E>> void writeEnum(CompoundTag tag, String key, E enumValue) {
        tag.putInt(key, enumValue.ordinal());
    };
    
    public static Vec3 readVec3(ListTag tag, BlockPos origin) {
        return Vec3.atLowerCornerOf(origin).add(tag.getDouble(0), tag.getDouble(1), tag.getDouble(2));
    };

    public static ListTag writeVec3(Vec3 vec, BlockPos origin) {
        vec = vec.subtract(Vec3.atLowerCornerOf(origin));
        ListTag tag = new ListTag();
        tag.add(DoubleTag.valueOf(vec.x()));
        tag.add(DoubleTag.valueOf(vec.y()));
        tag.add(DoubleTag.valueOf(vec.z()));
        return tag;
    };

    public static int readBinaryMatrix4x4(CompoundTag tag, String key) {
        if (!tag.contains(key, Tag.TAG_SHORT)) return 0;
        return BinaryMatrix4x4.fromShort(tag.getShort(key));
    };

    public static void writeBinaryMatrix4x4(CompoundTag tag, String key, int binaryMatrix) {
        tag.putShort(key, BinaryMatrix4x4.asShort(binaryMatrix));
    };

    /**
     * Read a registered object (datapack or global).
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @param level
     * @return {@code null} if the ResourceLocation is invalid
     */
    public static <OBJECT> OBJECT readRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey, Level level) {
        return readRegistryObject(tag, key, registryKey, level.registryAccess());
    };

    /**
     * Write a registered object (datapack or global).
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @param level
     * @param object
     */
    public static <OBJECT> void writeRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey, Level level, OBJECT object) {
        writeRegistryObject(tag, key, registryKey, level.registryAccess(), object);
    };

    /**
     * Read a registered object (datapack or global).
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @param registryAccess
     * @return {@code null} if the ResourceLocation is invalid
     */
    public static <OBJECT> OBJECT readRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey, RegistryAccess registryAccess) {
        return registryAccess.registryOrThrow(registryKey).get(new ResourceLocation(tag.getString(key)));
    };

    /**
     * Write a registered object (datapack or global).
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @param registryAccess
     * @param object
     */
    public static <OBJECT> void writeRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey, RegistryAccess registryAccess, OBJECT object) {
        ResourceLocation rl = registryAccess.registryOrThrow(registryKey).getKey(object);
        if (rl != null) tag.putString(key, rl.toString());
    };

    /**
     * Read a global (non-datapack sensitive) registered object.
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @return {@code null} if the ResourceLocation is invalid
     */
    public static <OBJECT> OBJECT readRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey) {
        return PetrolparkRegistries.getRegistry(registryKey).getValue(new ResourceLocation(tag.getString(key)));
    };

    /**
     * Write a global (non-datapack sensitive) registered object.
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param registryKey
     * @param object
     */
    public static <OBJECT> void writeRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> registryKey, OBJECT object) {
        ResourceLocation rl = PetrolparkRegistries.getRegistry(registryKey).getKey(object);
        if (rl != null) tag.putString(key, rl.toString());
    };

    /**
     * Read a datapack-sensitive registered object.
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param dataRegistryKey
     * @return {@code null} if the ResourceLocation is invalid
     */
    public static <OBJECT> OBJECT readDataRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> dataRegistryKey) {
        return PetrolparkRegistries.getDataRegistry(dataRegistryKey).get(new ResourceLocation(tag.getString(key)));
    };

    /**
     * Write a datapack-sensitive registered object.
     * @param <OBJECT> Class of the object/registry
     * @param tag
     * @param key
     * @param dataRegistryKey
     * @param dataObject
     */
    public static <OBJECT> void writeDataRegistryObject(CompoundTag tag, String key, ResourceKey<Registry<OBJECT>> dataRegistryKey, OBJECT dataObject) {
        ResourceLocation rl = PetrolparkRegistries.getDataRegistry(dataRegistryKey).getKey(dataObject);
        if (rl != null) tag.putString(key, rl.toString());
    };
};
