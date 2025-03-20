package com.petrolpark.core.data;

import java.util.HashSet;
import java.util.List;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.Codec;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.attachment.IAttachmentHolder;
import net.neoforged.neoforge.attachment.IAttachmentSerializer;

public class ResourceLocationSet extends HashSet<ResourceLocation> {

    public static final Codec<ResourceLocationSet> CODEC = Codec.list(ResourceLocation.CODEC).xmap(ResourceLocationSet::new, List::copyOf);

    public ResourceLocationSet(IAttachmentHolder holder) {
        super();
    };

    public ResourceLocationSet(List<ResourceLocation> rls) {
        super(rls);
    };
    
    public static final IAttachmentSerializer<Tag, ResourceLocationSet> ATTACHMENT_SERIALIZER = new IAttachmentSerializer<Tag, ResourceLocationSet>() {

        @Override
        public ResourceLocationSet read(@Nonnull IAttachmentHolder holder, @Nonnull Tag tag, @Nonnull HolderLookup.Provider provider) {
            return CODEC.parse(NbtOps.INSTANCE, tag).getOrThrow();
        };

        @Override
        public @Nullable Tag write(@Nonnull ResourceLocationSet attachment, @Nonnull HolderLookup.Provider provider) {
            return CODEC.encodeStart(NbtOps.INSTANCE, attachment).getOrThrow();
        };
        
    };
};
