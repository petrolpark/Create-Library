package petrolpark.mc.library.compat.jade;

import org.jetbrains.annotations.Nullable;

import petrolpark.mc.library.compat.Mods;
import petrolpark.mc.library.shared.ISharedFeature;
import petrolpark.mc.library.shared.SharedFeatureFlag;
import petrolpark.mc.library.util.Lang;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.callback.JadeItemModNameCallback;

public class SharedFeatureItemModNameCallback implements JadeItemModNameCallback {

    @Override
    public @Nullable String gatherItemModName(ItemStack stack) {
        ISharedFeature sharedFeature = null;
        if (stack.getItem() instanceof ISharedFeature itemSharedFeature) sharedFeature = itemSharedFeature;
        else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof ISharedFeature blockSharedFeature) sharedFeature = blockSharedFeature;

        if (sharedFeature != null) {
            SharedFeatureFlag featureFlag = sharedFeature.getSharedFeatureFlag();
            if (featureFlag.enabled()) return Lang.shortList(featureFlag.streamUsers().map(Mods::getName).toArray(i -> new String[i]));
        };
        return null;
    };
    
};
