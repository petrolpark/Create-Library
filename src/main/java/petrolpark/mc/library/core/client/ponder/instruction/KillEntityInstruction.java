package petrolpark.mc.library.core.client.ponder.instruction;

import net.createmod.ponder.api.element.ElementLink;
import net.createmod.ponder.api.element.EntityElement;
import net.createmod.ponder.foundation.PonderScene;
import net.createmod.ponder.foundation.instruction.TickingInstruction;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.LivingEntity;

public class KillEntityInstruction extends TickingInstruction {

    protected final ElementLink<EntityElement> link;
    protected EntityElement element;

    public KillEntityInstruction(ElementLink<EntityElement> link) {
        super(false, 20);
        this.link = link;
    };

    @Override
    protected void firstTick(PonderScene scene) {
        super.firstTick(scene);
        element = scene.resolve(link);
    };

    @Override
    public void tick(PonderScene scene) {
        super.tick(scene);
        if (element == null) return;
        element.ifPresent(entity -> {
            if (!(entity instanceof LivingEntity livingEntity)) return;
            livingEntity.deathTime++;
            if (livingEntity.deathTime == 20) {
                entity.handleEntityEvent((byte)60);
                entity.remove(RemovalReason.KILLED);
            };
        });
    };
    
};
