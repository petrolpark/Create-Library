package petrolpark.mc.library.core.world.levelgen.feature.tree;

import java.util.Optional;
import java.util.function.BiConsumer;

import javax.annotation.Nonnull;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import petrolpark.mc.library.registry.PetrolparkFeatureTypes;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelSimulatedReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration;
import net.minecraft.world.level.levelgen.feature.rootplacers.AboveRootPlacement;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacer;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
   
public class ThickBaseRootPlacer extends RootPlacer {
    
    public static final MapCodec<ThickBaseRootPlacer> CODEC = RecordCodecBuilder.mapCodec(instance -> 
        rootPlacerParts(instance)
        .and(FloatProvider.codec(1f, 8f).fieldOf("width").forGetter(ThickBaseRootPlacer::getWidth))
        .and(BuiltInRegistries.BLOCK.byNameCodec().fieldOf("sapling").forGetter(ThickBaseRootPlacer::getSapling))
        .apply(instance, ThickBaseRootPlacer::new)
    );

    protected final FloatProvider width;
    protected final Block sapling;

    public ThickBaseRootPlacer(IntProvider trunkOffset, BlockStateProvider rootProvider, Optional<AboveRootPlacement> aboveRootPlacement, FloatProvider width, Block sapling) {
        super(trunkOffset, rootProvider, aboveRootPlacement);
        this.width = width;
        this.sapling = sapling;
    };

    public FloatProvider getWidth() {
        return width;
    };

    public Block getSapling() {
        return sapling;
    };

    @Override
    public boolean placeRoots(
        @Nonnull LevelSimulatedReader level,
        @Nonnull BiConsumer<BlockPos, BlockState> blockSetter,
        @Nonnull RandomSource random,
        @Nonnull BlockPos origin,
        @Nonnull BlockPos trunkOrigin,
        @Nonnull TreeConfiguration treeConfig
    ) {
        final float width = getWidth().sample(random);
        final int ceilWidth = Mth.ceil(width);
        final float height = (trunkOrigin.getY() - origin.getY()) / (width * width); // Normalize
        if (!(level instanceof LevelReader levelReader)) return false;

        return BlockPos.betweenClosedStream(origin.offset(-ceilWidth, 0, -ceilWidth), origin.offset(ceilWidth, 0, ceilWidth)).allMatch(pos -> {
            float y = Math.min(0, Mth.sqrt((float)pos.distSqr(origin)) - width);
            y = height * y * y; // Quadratic shape of trunk
            final int floorY = (int)y;

            boolean placedRoot = false;

            for (int i = 0; i < floorY; i++) {
                BlockPos rootPos = pos.above(i);
                if (!canPlaceRoot(level, rootPos)) return false; // Don't end up with floating blocks
                placedRoot = true;
                placeRoot(level, blockSetter, random, rootPos, treeConfig, origin);
            };

            if (random.nextFloat() < y - floorY) {
                BlockPos rootPos = pos.above(floorY);
                if (canPlaceRoot(level, rootPos)) {
                    placeRoot(level, blockSetter, random, rootPos, treeConfig, origin);
                    placedRoot = true;
                };
            };

            if (placedRoot) { // Dirt beneath any roots
                if (!sapling.defaultBlockState().canSurvive(levelReader, pos)) return !treeConfig.forceDirt; // If this Block would not have been replaceable, then fail if we insist on placing the dirt
                if (!levelReader.getBlockState(pos.below()).onTreeGrow(levelReader, blockSetter, random, pos, treeConfig)) blockSetter.accept(pos.below(), treeConfig.dirtProvider.getState(random, pos.below())); 
            };
            
            return true;
        });
    };

    @Override
    protected RootPlacerType<ThickBaseRootPlacer> type() {
        return PetrolparkFeatureTypes.THICK_BASE_ROOT_PLACER.get();
    };

    protected void placeRoot(@Nonnull LevelSimulatedReader level, @Nonnull BiConsumer<BlockPos, BlockState> blockSetter, @Nonnull RandomSource random, @Nonnull BlockPos pos, @Nonnull TreeConfiguration treeConfig, BlockPos origin) {
        if (pos.getX() == origin.getX() && pos.getZ() == origin.getZ()) {
            if (canPlaceRoot(level, pos)) blockSetter.accept(pos, getPotentiallyWaterloggedState(level, pos, treeConfig.trunkProvider.getState(random, pos))); // Middle blocks are trunks
        } else {
            placeRoot(level, (p, s) -> blockSetter.accept(p, rotateToFaceOrigin(pos, s, origin)), random, pos, treeConfig);
        };
    };

    public BlockState rotateToFaceOrigin(BlockPos pos, BlockState state, BlockPos origin) {
        final Vec3i diff = origin.subtract(pos);
        Direction facing = Direction.getNearest(diff.getX(), diff.getY(), diff.getZ());;
        if (facing == Direction.DOWN) facing = Direction.UP;
        if (state.hasProperty(BlockStateProperties.FACING)) return state.setValue(BlockStateProperties.FACING, facing);
        else if (state.hasProperty(BlockStateProperties.AXIS)) return state.setValue(BlockStateProperties.AXIS, facing.getAxis()); // Rotate Wood blocks to face the origin
        return state;
    };
    
};
