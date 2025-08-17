package com.petrolpark.core.scratch.symbol.expression.nullary;

import com.petrolpark.core.scratch.ScratchParameters;
import com.petrolpark.core.scratch.context.IRegistryAccessScratchContext;
import com.petrolpark.core.scratch.symbol.expression.IScratchExpression;
import com.petrolpark.core.scratch.symbol.type.IScratchExpressionType;
import com.petrolpark.core.scratch.type.RegistryObjectScratchType;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;

public final class ItemScratchExpression extends RegistryObjectScratchExpression<Item> {

    public ItemScratchExpression(Holder<Item> item) {
        super(item);
    };

    @Override
    public IScratchExpressionType<? extends IScratchExpression<IRegistryAccessScratchContext, Item, ScratchParameters>> getSymbolType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getSymbolType'");
    };

    @Override
    public RegistryObjectScratchType<Item> getReturnType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getReturnType'");
    };

    
};
