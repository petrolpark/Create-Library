package com.petrolpark.recipe.ingredient;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.simibubi.create.foundation.utility.RegisteredObjects;

import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public interface BlockIngredient<T extends BlockIngredient<T>> {

    public static final ImpossibleBlockIngredient IMPOSSIBLE = new ImpossibleBlockIngredient();

    public BlockIngredientType<T> getType();

    public boolean isValid(BlockState state);
    
    /**
     * @return An empty List if there is not a reasonably low number of possible Item Stacks
     */
    public NonNullList<ItemStack> getDisplayedItemStacks();

    void write(FriendlyByteBuf buffer);

    public static interface BlockIngredientType<T extends BlockIngredient<T>> {

        public T read(FriendlyByteBuf buffer);

        public ResourceLocation getId();
    };

    static class Registry {
        private static Map<ResourceLocation, BlockIngredientType<?>> TYPES = new HashMap<>();

        static {
            registerType(ImpossibleBlockIngredient.TYPE);
            registerType(SingleBlockIngredient.TYPE);
            registerType(BlockTagIngredient.TYPE);
            registerType(UnionBlockIngredient.TYPE);
            registerType(IntersectionBlockIngredient.TYPE);
        };
    };

    public static void registerType(BlockIngredientType<?> type) {
        Registry.TYPES.put(type.getId(), type);
    };

    public static void write(BlockIngredient<?> ingredient, FriendlyByteBuf buffer) {
        ResourceLocation typeId = ingredient.getType().getId();
        if (!Registry.TYPES.containsKey(typeId)) throw new IllegalStateException("Block Ingredient Type "+typeId+" is not registered.");
        buffer.writeResourceLocation(typeId);
        ingredient.write(buffer);
    };

    public static BlockIngredient<?> read(FriendlyByteBuf buffer) {
        return Registry.TYPES.get(buffer.readResourceLocation()).read(buffer);
    };

    public static class ImpossibleBlockIngredient implements BlockIngredient<ImpossibleBlockIngredient> {

        public static final Type TYPE = new Type();

        @Override
        public BlockIngredientType<ImpossibleBlockIngredient> getType() {
            return TYPE;
        };

        @Override
        public boolean isValid(BlockState state) {
            return false;
        };

        @Override
        public NonNullList<ItemStack> getDisplayedItemStacks() {
            return NonNullList.of(ItemStack.EMPTY);
        };

        @Override
        public void write(FriendlyByteBuf buffer) {
            //NOOP
        };

        protected static class Type implements BlockIngredientType<ImpossibleBlockIngredient> {

            public static final ResourceLocation ID = new ResourceLocation("petrolpark", "impossible");

            @Override
            public ImpossibleBlockIngredient read(FriendlyByteBuf buffer) {
                return IMPOSSIBLE;
            };

            @Override
            public ResourceLocation getId() {
                return ID;
            };


        };

    };
    
    public static class SingleBlockIngredient implements BlockIngredient<SingleBlockIngredient> {

        public static final Type TYPE = new Type();

        public final Block block;

        public SingleBlockIngredient(Block block) {
            this.block = block;
        };

        @Override
        public BlockIngredientType<SingleBlockIngredient> getType() {
            return TYPE;
        };

        @Override
        public boolean isValid(BlockState state) {
            return state.is(block);
        };

        @Override
        public NonNullList<ItemStack> getDisplayedItemStacks() {
            return NonNullList.of(ItemStack.EMPTY, new ItemStack(block.asItem()));
        };

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(RegisteredObjects.getKeyOrThrow(block));
        };

        protected static class Type implements BlockIngredientType<SingleBlockIngredient> {

            @Override
            public SingleBlockIngredient read(FriendlyByteBuf buffer) {
                return new SingleBlockIngredient(ForgeRegistries.BLOCKS.getValue(buffer.readResourceLocation()));
            };

            @Override
            public ResourceLocation getId() {
                return new ResourceLocation("petrolpark", "single_block");
            };

        };
    };

    public static class BlockTagIngredient implements BlockIngredient<BlockTagIngredient> {

        public static final Type TYPE = new Type();

        public final TagKey<Block> tag;

        public BlockTagIngredient(TagKey<Block> tag) {
            this.tag = tag;
        };

        @Override
        public BlockIngredientType<BlockTagIngredient> getType() {
            return TYPE;
        };

        @Override
        public boolean isValid(BlockState state) {
            return state.is(tag);
        };

        @Override
        public NonNullList<ItemStack> getDisplayedItemStacks() {
            return NonNullList.of(ItemStack.EMPTY, ForgeRegistries.BLOCKS.tags().getTag(tag).stream()
                .map(block -> new ItemStack(block.asItem()))
                .toArray(i -> new ItemStack[i])
            );
        };

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(tag.location());
        };

        protected static class Type implements BlockIngredientType<BlockTagIngredient> {

            public static final ResourceLocation ID = new ResourceLocation("petrolpark", "block_tag");

            @Override
            public BlockTagIngredient read(FriendlyByteBuf buffer) {
                return new BlockTagIngredient(BlockTags.create(buffer.readResourceLocation()));
            };

            @Override
            public ResourceLocation getId() {
                return ID;
            };

        };

    };

    public static abstract class SetBlockIngredient<T extends BlockIngredient<T>> implements BlockIngredient<T> {

        public final BlockIngredient<?>[] values;

        public SetBlockIngredient(BlockIngredient<?>[] values) {
            this.values = values;
        };

        @Override
        public void write(FriendlyByteBuf buffer) {
            buffer.writeVarInt(values.length);
            for (BlockIngredient<?> i : values) i.write(buffer);
        };

        protected static abstract class SetBlockIngredientType<T extends BlockIngredient<T>> implements BlockIngredientType<T> {

            @Override
            public T read(FriendlyByteBuf buffer) {
                int length = buffer.readVarInt();
                BlockIngredient<?>[] values = new BlockIngredient<?>[length];
                for (int i = 0; i < length; i++) {
                    values[i] = BlockIngredient.read(buffer);
                };
                return create(values);
            };

            protected abstract T create(BlockIngredient<?>[] values);
        };

    };

    public static class UnionBlockIngredient extends SetBlockIngredient<UnionBlockIngredient> {

        public static final Type TYPE = new Type();

        public UnionBlockIngredient(BlockIngredient<?>[] values) {
            super(values);
        };

        @Override
        public BlockIngredientType<UnionBlockIngredient> getType() {
            return TYPE;
        };

        @Override
        public boolean isValid(BlockState state) {
            return Stream.of(values).anyMatch(i -> i.isValid(state));
        };

        @Override
        public NonNullList<ItemStack> getDisplayedItemStacks() {
            Set<ItemStack> items = new HashSet<>();
            for (BlockIngredient<?> i : values) items.addAll(i.getDisplayedItemStacks());
            return NonNullList.of(ItemStack.EMPTY, items.toArray(i -> new ItemStack[i]));
        };
    
        protected static class Type extends SetBlockIngredientType<UnionBlockIngredient> {

            public static final ResourceLocation ID = new ResourceLocation("petrolpark", "union");

            @Override
            public ResourceLocation getId() {
                return ID;
            }

            @Override
            protected UnionBlockIngredient create(BlockIngredient<?>[] values) {
                return new UnionBlockIngredient(values);
            };
        };
    };

    public static class IntersectionBlockIngredient extends SetBlockIngredient<IntersectionBlockIngredient> {

        public static final Type TYPE = new Type();

        public IntersectionBlockIngredient(BlockIngredient<?>[] values) {
            super(values);
        };

        @Override
        public BlockIngredientType<IntersectionBlockIngredient> getType() {
            return TYPE;
        };

        @Override
        public boolean isValid(BlockState state) {
            return Stream.of(values).allMatch(i -> i.isValid(state));
        };

        @Override
        public NonNullList<ItemStack> getDisplayedItemStacks() {
            if (values.length == 0) return NonNullList.of(ItemStack.EMPTY);
            Optional<BlockIngredient<?>> ingredient = Stream.of(values).filter(i -> !i.getDisplayedItemStacks().isEmpty()).findFirst();
            if (ingredient.isEmpty()) return NonNullList.of(ItemStack.EMPTY);
            return NonNullList.of(ItemStack.EMPTY, ingredient.get().getDisplayedItemStacks().stream().filter(stack -> 
                Stream.of(values).allMatch(i -> i.getDisplayedItemStacks().isEmpty() || i.getDisplayedItemStacks().contains(stack))
            ).toArray(i -> new ItemStack[i]));
        };

        protected static class Type extends SetBlockIngredientType<IntersectionBlockIngredient> {

            public static final ResourceLocation ID = new ResourceLocation("petrolpark", "intersection");

            @Override
            public ResourceLocation getId() {
                return ID;
            };

            @Override
            protected IntersectionBlockIngredient create(BlockIngredient<?>[] values) {
                return new IntersectionBlockIngredient(values);
            };

        };

    };
};
