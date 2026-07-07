package petrolpark.mc.library.core.world.item.restaurant.order;

import com.mojang.datafixers.Products.P2;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.StringRepresentable;
import petrolpark.mc.library.util.Lang;

public interface IRestaurantOrderThing {

    public Visibility visibility();

    public boolean persistsToMenu();
    
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

    static <THING extends IRestaurantOrderThing> P2<RecordCodecBuilder.Mu<THING>, Visibility, Boolean> commonFields(RecordCodecBuilder.Instance<THING> instance) {
        return instance.group(
            Visibility.CODEC.optionalFieldOf("visibility", Visibility.ALWAYS).forGetter(IRestaurantOrderThing::visibility),
            Codec.BOOL.optionalFieldOf("persists_to_menu", true).forGetter(IRestaurantOrderThing::persistsToMenu)
        );
    }
};
