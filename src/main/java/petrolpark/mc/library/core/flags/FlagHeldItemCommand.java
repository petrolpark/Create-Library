package petrolpark.mc.library.core.flags;

import java.util.Collection;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import petrolpark.mc.library.Petrolpark;
import petrolpark.mc.library.registry.PetrolparkRegistries;

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

public class FlagHeldItemCommand {
    
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType(name -> Component.translatable("commands.petrolpark.flag.failed.entity", name));
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType(name -> Component.translatable("commands.petrolpark.flag.failed.itemless", name));
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(Component.translatable("commands.petrolpark.flag.failed"));
 
   public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
      dispatcher.register(Commands.literal(Petrolpark.MOD_ID).then(Commands.literal("flag").requires(source -> {
         return source.hasPermission(2);
      }).then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("flag", ResourceArgument.resource(context, PetrolparkRegistries.Keys.FLAG)).executes(ctx -> 
         flag(ctx.getSource(), EntityArgument.getEntities(ctx, "targets"), ResourceArgument.getResource(ctx, "flag", PetrolparkRegistries.Keys.FLAG))
      )))));
   };
 
   private static int flag(CommandSourceStack source, Collection<? extends Entity> targets, Holder<Flag> flagHolder) throws CommandSyntaxException {
      int i = 0;
      for(Entity entity : targets) {
         if (entity instanceof LivingEntity livingEntity) {
            ItemStack itemStack = livingEntity.getMainHandItem();
            if (!itemStack.isEmpty()) {
               try {
                  if (ItemFlagPole.get(itemStack).flag(flagHolder)) i++;
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
               return Component.translatable("commands.petrolpark.flag.success.single", Flag.getName(flagHolder), targets.iterator().next().getDisplayName());
            }, true);
         } else {
            source.sendSuccess(() -> {
               return Component.translatable("commands.petrolpark.flag.success.multiple", Flag.getName(flagHolder), targets.size());
            }, true);
         };

         return i;
      }
   };
};