package com.petrolpark.core.contamination;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.petrolpark.Petrolpark;
import com.petrolpark.PetrolparkRegistries;

import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ContaminateHeldItemCommand {
    
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(name -> Component.translatable("commands.petrolpark.contaminate.failed.entity", name));
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(name -> Component.translatable("commands.petrolpark.contaminate.failed.itemless", name));
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.petrolpark.contaminate.failed"));
 
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(Commands.literal(Petrolpark.MOD_ID).then(Commands.literal("contaminate").requires(source -> {
         return source.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("contaminant", ResourceArgument.resource(context, PetrolparkRegistries.Keys.CONTAMINANT)).executes(ctx -> 
         contaminate(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), ResourceArgument.getResource(ctx, "contaminant", PetrolparkRegistries.Keys.CONTAMINANT))
      )))));
   };
 
   private static int contaminate(CommandSourceStack source, Collection<? extends Entity> targets, Holder<Contaminant> contaminantHolder) throws CommandSyntaxException {
      int i = 0;
      for(Entity entity : targets) {
         if (entity instanceof LivingEntity livingEntity) {
            ItemStack itemStack = livingEntity.getMainHandItem();
            if (!itemStack.isEmpty()) {
               try {
                  if (ItemContamination.get(itemStack).contaminate(contaminantHolder)) i++;
               } catch (Throwable e) {};
               
            } else if (targets.size() == 1) {
               throw ERROR_NO_ITEM.create(livingEntity.getName().getString());
            };
         } else if (targets.size() == 1) {
            throw ERROR_NOT_LIVING_ENTITY.create(entity.getName().getString());
         };
      };

      if (i == 0) {
         throw ERROR_NOTHING_HAPPENED.create();
      } else {
         if (targets.size() == 1) {
            source.sendSuccess(() -> {
               return Component.translatable("commands.petrolpark.contaminate.success.single", Contaminant.getName(contaminantHolder), targets.iterator().next().getDisplayName());
            }, true);
         } else {
            source.sendSuccess(() -> {
               return Component.translatable("commands.petrolpark.contaminate.success.multiple", Contaminant.getName(contaminantHolder), targets.size());
            }, true);
         };

         return i;
      }
   };
};