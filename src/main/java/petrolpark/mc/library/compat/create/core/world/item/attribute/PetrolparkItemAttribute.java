package petrolpark.mc.library.compat.create.core.world.item.attribute;

import petrolpark.mc.library.util.Lang;
import com.simibubi.create.content.logistics.item.filter.attribute.ItemAttribute;

import net.minecraft.network.chat.MutableComponent;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

public interface PetrolparkItemAttribute extends ItemAttribute {
    
    @OnlyIn(value = Dist.CLIENT)
	default MutableComponent format(boolean inverted) {
		return Lang.translate("item_attributes." + getTranslationKey() + (inverted ? ".inverted" : ""), getTranslationParameters());
	};
};
