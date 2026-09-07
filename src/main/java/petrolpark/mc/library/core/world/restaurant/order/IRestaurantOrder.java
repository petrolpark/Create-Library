package petrolpark.mc.library.core.world.restaurant.order;

import java.util.List;
import java.util.function.Function;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import petrolpark.mc.library.core.data.recipe.ingredient.advanced.IAdvancedIngredient;
import petrolpark.mc.library.core.data.reward.info.IRewardInfo;
import petrolpark.mc.library.util.Lang;

public interface IRestaurantOrder {

    public static Codec<IRestaurantOrder> SERVER_CODEC = ServerRestaurantOrder.CODEC.flatComapMap(
        Function.identity(),
        order -> order instanceof ServerRestaurantOrder serverOrder ? DataResult.success(serverOrder) : DataResult.error(() -> "Cannot save order (id " + order.id() + ") on client")
    );
    
    public int id();

    public IAdvancedIngredient<ItemStack> ingredient();

    public List<RestaurantOrderModifier.Info> modifiersInfo();

    public List<IRewardInfo> rewardsInfo();

    public interface Entry {

        public Visibility visibility();

        public boolean persistsToMenu();

        public default boolean everVisible() {
            return visibility() != Visibility.NEVER;
        };
        
        public enum Visibility implements StringRepresentable {

            ALWAYS,
            IN_RECEIPT,
            NEVER,
            ;

            public static final Codec<Visibility> CODEC = StringRepresentable.fromEnum(Visibility::values);

            private final String name;

            Visibility() {
                name = Lang.asId(name());
            };

            @Override
            public String getSerializedName() {
                return name;
            };

        };

        static <ENTRY extends IRestaurantOrder.Entry> P2<RecordCodecBuilder.Mu<ENTRY>, Visibility, Boolean> commonFields(RecordCodecBuilder.Instance<ENTRY> instance) {
            return instance.group(
                Visibility.CODEC.optionalFieldOf("visibility", Visibility.ALWAYS).forGetter(IRestaurantOrder.Entry::visibility),
                Codec.BOOL.optionalFieldOf("persists_to_menu", true).forGetter(IRestaurantOrder.Entry::persistsToMenu)
            );
        }
    };
};
