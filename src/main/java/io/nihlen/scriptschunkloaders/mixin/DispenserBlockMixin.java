package io.nihlen.scriptschunkloaders.mixin;

import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Shadow @Final private static Logger LOGGER;

    @Inject(
            at = @At("HEAD"),
            method = "dispenseFrom(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
            cancellable = true
    )

    private void dispense(ServerLevel world, BlockState state, BlockPos pos, CallbackInfo info) {
        if (world.isClientSide()) return;

        DispenserBlockEntity dispenserBlockEntity = world.getBlockEntity(pos, BlockEntityType.DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) return;

        String action = this.getAction(dispenserBlockEntity);
        if (action == null) return;

        this.applyChunkLoaderAction(world, state, pos, action);

        info.cancel();
    }

    @Unique
    private String getAction(DispenserBlockEntity dispenserBlockEntity) {
        // This can't be a property because the items aren't registered when we try to access them. The constructor is
        // also too early because it's called before Minecraft is even fully loaded. I don't know if there is a good
        // "on block created in world" or "after item registrations" event or similar that we can hook into. So for now
        // it will have to be defined here.
        Item toggleItem = Items.GLOWSTONE;
        Item startItem = Items.SHROOMLIGHT;
        Item stopItem = Items.MAGMA_BLOCK;

        if (this.patternMatches(dispenserBlockEntity, this.getPattern(toggleItem))) {
            return "toggle";
        }

        if (this.patternMatches(dispenserBlockEntity, this.getPattern(startItem))) {
            return "start";
        }

        if (this.patternMatches(dispenserBlockEntity, this.getPattern(stopItem))) {
            return "stop";
        }

        return null;
    }

    @Unique
    private Item[] getPattern(Item centerItem) {
        return new Item[]{
                Items.AIR,            Items.AMETHYST_SHARD, Items.AIR,
                Items.AMETHYST_SHARD, centerItem,           Items.AMETHYST_SHARD,
                Items.AIR,            Items.AMETHYST_SHARD, Items.AIR
        };
    }

    @Unique
    private boolean patternMatches(DispenserBlockEntity dispenserBlockEntity, Item[] pattern) {
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = dispenserBlockEntity.getItem(i);
            if (!itemStack.is(pattern[i])) {
                return false;
            }
        }

        return true;
    }

    @Unique
    private void applyChunkLoaderAction(ServerLevel world, BlockState state, BlockPos pos, String action) {
        BlockPos blockPos = pos.relative(state.getValue(DispenserBlock.FACING));
        List<AbstractMinecart> list = world.getEntitiesOfClass(AbstractMinecart.class, new AABB(blockPos), EntitySelector.ENTITY_STILL_ALIVE);

        for (AbstractMinecart entity : list) {
            MinecartEntityExt cart = (MinecartEntityExt)entity;

            switch (action) {
                case "toggle" -> this.toggleCart(cart);
                case "start" -> this.startCart(cart);
                case "stop" -> this.stopCart(cart);
            }
        }
    }

    @Unique
    private void toggleCart(MinecartEntityExt cart) {
        if (cart.scripts_chunk_loaders$isChunkLoader()) {
            this.stopCart(cart);
        } else {
            this.startCart(cart);
        }
    }

    @Unique
    private void startCart(MinecartEntityExt cart) {
        cart.scripts_chunk_loaders$startChunkLoader();
        cart.scripts_chunk_loaders$setChunkLoaderNameFromInventory();
    }

    @Unique
    private void stopCart(MinecartEntityExt cart) {
        cart.scripts_chunk_loaders$stopChunkLoader();
    }
}
