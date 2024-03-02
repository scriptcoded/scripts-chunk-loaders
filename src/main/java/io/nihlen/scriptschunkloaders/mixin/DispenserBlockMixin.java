package io.nihlen.scriptschunkloaders.mixin;

import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(
            at = @At("HEAD"),
            method = "dispense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/block/BlockState;Lnet/minecraft/util/math/BlockPos;)V",
            cancellable = true
    )

    private void dispense(ServerWorld world, BlockState state, BlockPos pos, CallbackInfo info) {
        if (world.isClient) return;

        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) return;

        // This can't be a property because the items aren't registered when we try to access them. The constructor is
        // also too early because it's called before Minecraft is even fully loaded. I don't know if there is a good
        // "on block created in world" or "after item registrations" event or similar that we can hook into. So for now
        // it will have to be defined here.
        Item[] pattern = {
                Items.AIR,            Items.AMETHYST_SHARD, Items.AIR,
                Items.AMETHYST_SHARD, Items.GLOWSTONE,      Items.AMETHYST_SHARD,
                Items.AIR,            Items.AMETHYST_SHARD, Items.AIR
        };

        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = dispenserBlockEntity.getStack(i);
            if (!itemStack.isOf(pattern[i])) {
                return ;
            }
        }

        this.toggleMinecraftChunkLoader(world, state, pos);
        info.cancel();
    }

    @Unique
    private void toggleMinecraftChunkLoader(ServerWorld world, BlockState state, BlockPos pos) {
        BlockPos blockPos = pos.offset(state.get(DispenserBlock.FACING));
        List<AbstractMinecartEntity> list = world.getEntitiesByClass(AbstractMinecartEntity.class, new Box(blockPos), EntityPredicates.VALID_ENTITY);

        for (AbstractMinecartEntity entity : list) {
            MinecartEntityExt cart = (MinecartEntityExt)entity;

            if (cart.scripts_chunk_loaders$isChunkLoader()) {
                cart.scripts_chunk_loaders$stopChunkLoader();
            } else {
                cart.scripts_chunk_loaders$startChunkLoader();
                cart.scripts_chunk_loaders$setChunkLoaderNameFromInventory();
            }
        }
    }
}
