package com.petrolpark.core.puter.disc;

import com.petrolpark.core.puter.file.IPuterFile;
import com.petrolpark.core.puter.file.PuterDataStack;
import com.petrolpark.core.puter.file.PuterFile;

import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.items.IItemHandler;

/**
 * Storage for {@link IPuterFile}s. Somewhat analagous to {@link IItemHandler} or {@link IFluidHandler}.
 */
public interface IPuterDisc {
    
    /**
     * 
     * @param <FILE>
     * @param file
     * @param simulate
     * @return The remaining {@link IPuterFile} that was not added. For discrete {@link PuterFile}s this may be the whole File (if it didn't fit); for {@link PuterDataStack}s this will be the remaining Stack.
     */
    public <FILE extends IPuterFile> FILE add(FILE file, boolean simulate);
};
