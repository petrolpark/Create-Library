package petrolpark.mc.library.compat.create.core.world.block;

import javax.annotation.Nullable;

import petrolpark.mc.library.util.BlockStateAndEntity;
import petrolpark.mc.library.util.Conversion.BlockStateConversion;
import com.simibubi.create.content.decoration.copycat.CopycatBlockEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public record CopycatBlockConversion(BlockStateConversion blockConversion) implements BlockStateConversion {

    @Override
    public ConversionResult<BlockStateAndEntity> convert(Level level, BlockStateAndEntity object, @Nullable Player player) {
        return object.entityOp().map(be -> be instanceof CopycatBlockEntity cbe ? cbe : null)
            .<ConversionResult<BlockStateAndEntity>>map(be -> {
                final BlockStateAndEntity material = BlockStateAndEntity.of(be.getMaterial());
                final BlockStateAndEntity converted = blockConversion().convert(level, material, player).value();
                if (!converted.equals(material)) {
                    be.setMaterial(converted.state());
                    return finish(object);
                } else {
                    return pass(object);
                }
            }).orElseGet(supplyPass(object));
    };
    
};
