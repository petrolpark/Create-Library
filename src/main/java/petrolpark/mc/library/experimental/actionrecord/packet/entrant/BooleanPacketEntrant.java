package petrolpark.mc.library.experimental.actionrecord.packet.entrant;

import java.util.function.Predicate;

import net.minecraft.network.chat.Component;

public class BooleanPacketEntrant<PACKET> extends AlwaysEnterPacketEntrant<PACKET> {

    public final String translationKey;
    public final Predicate<PACKET> predicate;

    public BooleanPacketEntrant(String translationKey, Predicate<PACKET> predicate) {
        this.translationKey = translationKey;
        this.predicate = predicate;
    };

    @Override
    public Component getDescription(PACKET packet) {
        return Component.translatable(translationKey + (predicate.test(packet) ? ".true" : ".false"));
    };
    
};
