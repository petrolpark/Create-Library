package com.petrolpark.core.puter.file;

import javax.annotation.Nonnull;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.neoforged.neoforge.common.MutableDataComponentHolder;

/**
 * {@link IPuterFile} analogue of {@link ItemStack}.<ul>
 * <li>A Puter file of a certain {@link PuterData} with variable size
 * <li>Can be split up arbitrarily
 * <li>Conserved (can't be copied, in general)
 * </ul>
 */
public final class PuterDataStack implements IPuterFile, MutableDataComponentHolder {

    public static final PuterDataStack EMPTY = new PuterDataStack(null, 0);

    private final PuterData puterData;
    private long size;

    public PuterDataStack(PuterData type, long size) {
        this.puterData = type;
        this.size = size;
    };

    public PuterData getData() {
        return puterData;
    };

    @Override
    public long getSize() {
        return size;
    };

    @Override
    public DataComponentMap getComponents() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getComponents'");
    };

    @Override
    public <T> @Nullable T set(@Nonnull DataComponentType<? super T> componentType, @Nonnull T value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'set'");
    };

    @Override
    public <T> @Nullable T remove(@Nonnull DataComponentType<? extends T> componentType) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    };

    @Override
    public void applyComponents(@Nonnull DataComponentPatch patch) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyComponents'");
    };

    @Override
    public void applyComponents(@Nonnull DataComponentMap components) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'applyComponents'");
    };
    
};
