package com.petrolpark.core.scratch.symbol.expression.nullary;

import java.util.function.Function;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IRegistryAccessScratchContext;
import com.petrolpark.core.scratch.symbol.type.IScratchSymbolType;
import com.petrolpark.core.scratch.symbol.type.SimpleScratchExpressionType;
import com.petrolpark.core.scratch.type.RegistryObjectScratchType;

import net.minecraft.core.Holder;
import net.minecraft.network.codec.ByteBufCodecs;

public abstract class RegistryObjectScratchExpression<TYPE> extends NullaryScratchExpression<IRegistryAccessScratchContext, TYPE> {

    public final Holder<TYPE> holder;

    public RegistryObjectScratchExpression(Holder<TYPE> object) {
        this.holder = object;
    };

    protected final Holder<TYPE> holder() {
        return holder;
    };
    
    @Override
    public final TYPE evaluate(IRegistryAccessScratchContext context, ScratchParameters arguments) {
        return holder().value();
    };

    @Override
    public abstract RegistryObjectScratchType<TYPE> getReturnType();

    public static final <TYPE, EXPRESSION extends RegistryObjectScratchExpression<TYPE>> IScratchSymbolType<EXPRESSION> createExpressionType(RegistryObjectScratchType<TYPE> type, Function<Holder<TYPE>, EXPRESSION> factory) {
        return new SimpleScratchExpressionType<>(
            type.registry().holderByNameCodec().xmap(factory, RegistryObjectScratchExpression::holder),
            ByteBufCodecs.holderRegistry(type.registry().key()).map(factory, RegistryObjectScratchExpression::holder)
        );
    };

};
