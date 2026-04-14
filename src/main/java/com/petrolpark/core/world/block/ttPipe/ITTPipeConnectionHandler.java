package com.petrolpark.core.world.block.ttPipe;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import com.petrolpark.core.world.block.ttPipe.valve.IValve;
import com.petrolpark.util.BlockFace;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ITTPipeConnectionHandler<VALVE extends IValve<VALVE>, HANDLER extends ITTPipeConnectionHandler<VALVE, HANDLER>> {
    
    public Level getLevel();

    public BlockPos getPos();

    public Collection<Direction> getFaces();

    public ITTPipeType<VALVE, HANDLER> getPipeType();

    public void clearPipeConnections();

    public void addPipeConnection(HANDLER handler, VALVE valve);

    @SuppressWarnings("unchecked")
    public default void updateConnections() throws TTPipeConnectionException {
        clearPipeConnections();
        
        record SearchNode<VALVE extends IValve<VALVE>>(BlockPos pos, Direction inputFace, VALVE valve) {};

        final Queue<SearchNode<VALVE>> queue = new ArrayDeque<>();
        final Map<BlockFace, VALVE> visited = new HashMap<>();
        final Map<HANDLER, VALVE> foundHandlers = new HashMap<>();

        int pipes = 0;

        // Start from all neighboring pipes around the source tank
        for (final Direction direction : getFaces()) {
            final BlockPos neighborPos = getPos().relative(direction);
            final BlockState neighborState = getLevel().getBlockState(neighborPos);

            if (neighborState.getBlock() instanceof ITTPipeBlock pipeBlock && pipeBlock.getPipeType() == getPipeType()) {
                queue.add(new SearchNode<>(
                    neighborPos,
                    direction.getOpposite(),
                    getPipeType().getNoValve()
                ));
            };
        };

        while (!queue.isEmpty()) {
            SearchNode<VALVE> node = queue.poll();
            final BlockFace key = new BlockFace(node.pos(), node.inputFace());

            final VALVE existingValve = visited.get(key);

            if (existingValve != null) {
                VALVE merged = existingValve.or(node.valve());

                if (merged.equals(existingValve)) continue;

                visited.put(key, merged);
                node = new SearchNode<>(node.pos(), node.inputFace(), merged);
            } else {
                visited.put(key, node.valve());

                pipes++;
                getPipeType().checkPipeCount(pipes);
            };

            final SearchNode<VALVE> finalizedNode = node;
            final BlockState state = getLevel().getBlockState(finalizedNode.pos());

            if (!(state.getBlock() instanceof ITTPipeBlock pipeBlock && pipeBlock.getPipeType() == getPipeType())) continue;
            
            final ITTPipeBlock<VALVE> typedPipeBlock = (ITTPipeBlock<VALVE>)pipeBlock;

            try {
                typedPipeBlock.getPipeConnections(getLevel(), finalizedNode.pos(), state, finalizedNode.inputFace(), connection -> {

                    final BlockPos nextPos = finalizedNode.pos().offset(connection.relativePos());
                    final BlockState nextState = getLevel().getBlockState(nextPos);
                    final Direction nextInputFace = connection.face();

                    final VALVE edgeValve = connection.valve() == null
                        ? getPipeType().getNoValve()
                        : connection.valve();

                    final VALVE nextValve = finalizedNode.valve().then(edgeValve);

                    final Optional<HANDLER> handler = Optional.ofNullable(getLevel().getCapability(getPipeType().getCapability(), nextPos, nextInputFace))
                        .or(() -> Optional.ofNullable(getPipeType().getMissingCapabilityFallback(getLevel(), nextPos, state, nextInputFace)));
                    if (handler.isPresent()) {
                        foundHandlers.merge(handler.get(), existingValve, IValve::or);
                    };

                    if (nextState.getBlock() instanceof ITTPipeBlock nextPipeBlock && nextPipeBlock.getPipeType() == getPipeType()) {
                        queue.add(new SearchNode<>(
                            nextPos,
                            nextInputFace,
                            nextValve
                        ));
                    };
                });
            } catch (TTPipeConnectionException e) {
                throw e;
            };
            
        };

        for (Map.Entry<HANDLER, VALVE> entry : foundHandlers.entrySet()) {
            addPipeConnection(entry.getKey(), entry.getValue());
        };
    };
};
