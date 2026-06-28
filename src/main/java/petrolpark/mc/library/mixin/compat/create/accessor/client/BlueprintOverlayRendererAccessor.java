package petrolpark.mc.library.mixin.compat.create.accessor.client;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.simibubi.create.content.equipment.blueprint.BlueprintOverlayRenderer;

import net.createmod.catnip.data.Pair;
import net.minecraft.world.item.ItemStack;

@Mixin(BlueprintOverlayRenderer.class)
public interface BlueprintOverlayRendererAccessor {
    
    @Accessor("active")
    public static boolean isActive() {
        throw new AssertionError();
    };

    @Accessor("ingredients")
    public static List<Pair<ItemStack, Boolean>> getIngredients() {
        throw new AssertionError();
    };

    @Invoker("prepareCustomOverlay")
    public static void invokePrepareCustomOverlay() {
        throw new AssertionError();  
    };
};
