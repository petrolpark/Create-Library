package petrolpark.mc.library.compat.create.core.world.item.tooltip;

import java.util.ArrayList;
import java.util.List;

import com.simibubi.create.content.equipment.goggles.GogglesItem;
import com.simibubi.create.content.kinetics.base.IRotate.StressImpact;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.foundation.utility.CreateLang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import com.simibubi.create.infrastructure.config.CKinetics;

import net.createmod.catnip.lang.LangBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import petrolpark.mc.library.compat.create.core.world.item.ItemStressValues;
import petrolpark.mc.library.util.Lang;

/**
 * Item equivalent for Block-based {@link KineticStats}
 */
public class ItemKineticStats implements TooltipModifier {

    protected final Item item;

    public ItemKineticStats(Item item) {
        this.item = item;
    };

    @Override
    public void modify(ItemTooltipEvent context) {
        final List<Component> kineticStats = getKineticStats(item, context.getEntity());
		if (!kineticStats.isEmpty()) {
			List<Component> tooltip = context.getToolTip();
			tooltip.add(CommonComponents.EMPTY);
			tooltip.addAll(kineticStats);
		};
    };

    public static List<Component> getKineticStats(Item item, Player player) {
		final List<Component> list = new ArrayList<>();

        if (!StressImpact.isEnabled()) return list;

		final CKinetics kineticsConfig = AllConfigs.server().kinetics;
		final LangBuilder rpmUnit = CreateLang.translate("generic.unit.rpm");

		final boolean hasGoggles = GogglesItem.isWearingGoggles(player);

        final double impact = ItemStressValues.getImpact(item);
        final double capacity = ItemStressValues.getCapacity(item);

		if (impact > 0d) {
			CreateLang.translate("tooltip.stressImpact")
				.style(ChatFormatting.GRAY)
				.addTo(list);

			final StressImpact impactLevel = impact >= kineticsConfig.highStressImpact.get() ? StressImpact.HIGH
				: (impact >= kineticsConfig.mediumStressImpact.get() ? StressImpact.MEDIUM : StressImpact.LOW);
			final LangBuilder builder = CreateLang.builder()
				.add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactLevel.ordinal() + 1))
					.style(impactLevel.getAbsoluteColor())
                );

			if (hasGoggles) {
				builder.add(CreateLang.number(impact))
					.text("x ")
					.add(rpmUnit)
					.addTo(list);
			} else {
                builder.translate("tooltip.stressImpact." + Lang.asId(impactLevel.name())).addTo(list);
            };
		};

		if (capacity > 0d) {
			CreateLang.translate("tooltip.capacityProvided")
				.style(ChatFormatting.GRAY)
				.addTo(list);

			// GeneratedRpm generatedRPM = BlockStressValues.RPM.get(item);

			final StressImpact impactLevel = capacity >= kineticsConfig.highCapacity.get() ? StressImpact.HIGH
				: (capacity >= kineticsConfig.mediumCapacity.get() ? StressImpact.MEDIUM : StressImpact.LOW);
			final StressImpact opposite = StressImpact.values()[StressImpact.values().length - 2 - impactLevel.ordinal()];
			final LangBuilder builder = CreateLang.builder()
				.add(CreateLang.text(TooltipHelper.makeProgressBar(3, impactLevel.ordinal() + 1))
					.style(opposite.getAbsoluteColor())
                );

			if (hasGoggles) {
				builder.add(CreateLang.number(capacity))
					.text("x ")
					.add(rpmUnit)
					.addTo(list);

				// if (generatedRPM != null) {
				// 	LangBuilder amount = CreateLang.number(capacity * generatedRPM.value())
				// 		.add(suUnit);
				// 	CreateLang.text(" -> ")
				// 		.add(generatedRPM.mayGenerateLess() ? CreateLang.translate("tooltip.up_to", amount) : amount)
				// 		.style(DARK_GRAY)
				// 		.addTo(list);
				// }
			} else {
				builder.translate("tooltip.capacityProvided." + Lang.asId(impactLevel.name())).addTo(list);
            };
		};

		return list;
	};
    
};
