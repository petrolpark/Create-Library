package petrolpark.mc.library.core.data.loot.wish;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;

import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WishGrantedToast implements Toast {

    private final IAdvancedIngredient<? super ItemStack> wish;
    private final List<ItemStack> stacks = new ArrayList<>();

    private long lastChanged;
    private boolean changed;

    public WishGrantedToast(IAdvancedIngredient<? super ItemStack> wish, ItemStack stack) {
        this.wish = wish;
        stacks.add(stack);
    };
    
    @Override
    public IAdvancedIngredient<? super ItemStack> getToken() {
        return wish;
    };

    @Override
    public Toast.Visibility render(@Nonnull GuiGraphics guiGraphics, @Nonnull ToastComponent toastComponent, long timeSinceLastVisible) {
        if (changed) {
            lastChanged = timeSinceLastVisible;
            changed = false;
        };

        if (stacks.isEmpty()) {
            return Toast.Visibility.HIDE;
        } else {
            // guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, width(), height());
            // guiGraphics.drawString(toastComponent.getMinecraft().font, TITLE_TEXT, 30, 7, -11534256, false);
            // guiGraphics.drawString(toastComponent.getMinecraft().font, DESCRIPTION_TEXT, 30, 18, -16777216, false);
            ItemStack stack = stacks.get((int)((double)timeSinceLastVisible / Math.max(1d, 5000d * toastComponent.getNotificationDisplayTimeMultiplier() / (double)stacks.size()) % (double)stacks.size()));
            guiGraphics.pose().pushPose();
            guiGraphics.pose().scale(0.6F, 0.6F, 1.0F);
            guiGraphics.renderFakeItem(stack, 3, 3);
            guiGraphics.pose().popPose();
            //guiGraphics.renderFakeItem(recipeholder.value().getResultItem(toastComponent.getMinecraft().level.registryAccess()), 8, 8);
            return (double)(timeSinceLastVisible - lastChanged) >= 5000d * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
        }
    }

    public void addItem(ItemStack stack) {
        stacks.add(stack);
        changed = true;
    };

    public static void addOrUpdate(ToastComponent toastComponent, IAdvancedIngredient<? super ItemStack> wish, ItemStack stack) {
        WishGrantedToast toast = toastComponent.getToast(WishGrantedToast.class, wish);
        if (toast == null) {
            toastComponent.addToast(new WishGrantedToast(wish, stack));
        } else {
            toast.addItem(stack);
        };
    }
    
};
