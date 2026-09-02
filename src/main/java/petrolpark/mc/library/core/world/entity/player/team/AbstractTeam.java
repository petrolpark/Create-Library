package petrolpark.mc.library.core.world.entity.player.team;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.include.com.google.common.base.Objects;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

/**
 * Implementation of {@link ITeam} which implements {@link MutableDataComponentHolder Data Component} manipulation,
 * but not anything to do with {@link ITeam#isMember(net.minecraft.world.entity.player.Player) membership} of the Team itself.
 */
public abstract class AbstractTeam implements ITeam {
    
    protected final PatchedDataComponentMap components;

    protected AbstractTeam(DataComponentPatch components) {
        this.components = PatchedDataComponentMap.fromPatch(DataComponentMap.EMPTY, components);
    };

    protected AbstractTeam(PatchedDataComponentMap components) {
        this.components = components;
    };

    public DataComponentPatch getDataComponentPatch() {
        return components.asPatch();
    };

    @Nullable
    public Tag writeDataComponentsTag(HolderLookup.Provider registries) {
        return DataComponentPatch.CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, registries), getDataComponentPatch()).getOrThrow();
    };

    @Override
    public final boolean isNone() {
        return false;
    };

    /**
     * Called if any {@link AbstractTeam#set(DataComponentType, Object) Data Components} <em>actually</em> change.
     * Use this to sync changes to the clients of {@link ITeam#isMember(net.minecraft.world.entity.player.Player) members} of this Team.
     * @param patch
     */
    public abstract void setChanged(DataComponentPatch patch);

    @Override
    public final <T> @Nullable T set(@Nonnull DataComponentType<? super T> componentType, @Nonnull T value) {
        T oldValue = components.set(componentType, value);
        if (!Objects.equal(oldValue, value)) setChanged(DataComponentPatch.builder().set(componentType, value).build());
        return oldValue;
    };

    @Override
    public final <T> @Nullable T remove(@Nonnull DataComponentType<? extends T> componentType) {
        T oldValue = components.remove(componentType);
        if (!Objects.equal(oldValue, null)) setChanged(DataComponentPatch.builder().remove(componentType).build());
        return oldValue;
    };

    @Override
    public final void applyComponents(@Nonnull DataComponentPatch patch) {
        components.applyPatch(patch);
        setChanged(patch);
    };

    @Override
    public final void applyComponents(@Nonnull DataComponentMap components) {
        this.components.setAll(components);
        DataComponentPatch.Builder builder = DataComponentPatch.builder();
        components.forEach(builder::set);
        setChanged(builder.build());
    };

    @Override
    public final DataComponentMap getComponents() {
        return components;
    };
};
