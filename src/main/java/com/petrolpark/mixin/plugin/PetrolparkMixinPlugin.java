package com.petrolpark.mixin.plugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import net.neoforged.fml.loading.FMLLoader;

public class PetrolparkMixinPlugin implements IMixinConfigPlugin {

    private int mixinPackagePathLength = 1;

    private final Map<String, Supplier<Boolean>> shouldLoad = new HashMap<>();

    protected String getMixinPackage() {
        return "com.petrolpark.mixin";
    };

    private int getMixinPackagePathLength() {
        if (mixinPackagePathLength == -1) mixinPackagePathLength = getMixinPackage().split(".").length;
        return mixinPackagePathLength;
    };

    @Override
    public void onLoad(String mixinPackage) {
        requireMultipleMods("client.ForgePluginFinderMixin", "jei", "create");
    };

    /**
     * Tells Mixin to only apply a Mixin if a given Mod is present.
     * @param mixinClassName Fully-qualified class name. <strong>Don't use {@code SomeMixin.getClass().getSimpleName()} for this</strong>,
     * as this calls the class, which will crash as it can't find the class into which its mixing
     * @param requiredModIds Mods upon which this Mixin depends
     */
    protected void requireMultipleMods(String mixinClassName, String ...requiredModIds) {
        String className = getMixinPackage()+".compat."+requiredModIds[0]+"."+mixinClassName;
        shouldLoad.put(className, () -> {
            for (String modId : requiredModIds) if (FMLLoader.getLoadingModList().getModFileById(modId) == null) return false;
            return true;
        });
    };

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        // Compat mixins
        String[] mixinPath = mixinClassName.split(".");
        if (mixinPath.length >= getMixinPackagePathLength() && mixinPath[getMixinPackagePathLength()].equals("compat")) return FMLLoader.getLoadingModList().getModFileById(mixinPath[getMixinPackagePathLength() + 1]) != null;

        // Custom predicates
        Supplier<Boolean> predicate = shouldLoad.get(mixinClassName);
        if (predicate == null) return true; // Always load by default
        return predicate.get();
    };

    @Override
    public String getRefMapperConfig() {
        return null; // Use the default refmap
    };

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {};

    @Override
    public List<String> getMixins() {
        return Collections.emptyList();
    };

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {}

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {};
    
};
