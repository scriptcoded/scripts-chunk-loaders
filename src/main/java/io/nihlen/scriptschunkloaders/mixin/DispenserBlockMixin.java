package io.nihlen.scriptschunkloaders.mixin;

import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import io.nihlen.scriptschunkloaders.ScriptsChunkLoadersGameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.BlockEntityTypes;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {

    @Inject(
            at = @At("HEAD"),
            method = "dispenseFrom(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)V",
            cancellable = true
    )

    private void dispense(ServerLevel level, BlockState state, BlockPos pos, CallbackInfo info) {
        if (level.isClientSide()) return;

        DispenserBlockEntity dispenserBlockEntity = level.getBlockEntity(pos, BlockEntityTypes.DISPENSER).orElse(null);
        if (dispenserBlockEntity == null) return;

        String action = this.getAction(dispenserBlockEntity);
        if (action == null) return;

        this.applyChunkLoaderAction(level, state, pos, action);

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
                Items.AIR, Items.AMETHYST_SHARD, Items.AIR,
                Items.AMETHYST_SHARD, centerItem, Items.AMETHYST_SHARD,
                Items.AIR, Items.AMETHYST_SHARD, Items.AIR
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
    private void applyChunkLoaderAction(ServerLevel level, BlockState state, BlockPos pos, String action) {
        BlockPos blockPos = pos.relative(state.getValue(DispenserBlock.FACING));
        List<AbstractMinecart> list = level.getEntitiesOfClass(AbstractMinecart.class, new AABB(blockPos), EntitySelector.ENTITY_STILL_ALIVE);

        for (AbstractMinecart entity : list) {
            MinecartEntityExt cart = (MinecartEntityExt) entity;

            switch (action) {
                case "toggle" -> this.toggleCart(level, entity, cart);
                case "start" -> this.startCart(level, entity, cart);
                case "stop" -> this.stopCart(level, entity, cart);
            }
        }
    }

    @Unique
    private void toggleCart(ServerLevel level, AbstractMinecart entity, MinecartEntityExt cart) {
        if (cart.scripts_chunk_loaders$isChunkLoader()) {
            this.stopCart(level, entity, cart);
        } else {
            this.startCart(level, entity, cart);
        }
    }

    @Unique
    private void startCart(ServerLevel level, AbstractMinecart entity, MinecartEntityExt cart) {
        cart.scripts_chunk_loaders$startChunkLoader();
        cart.scripts_chunk_loaders$setChunkLoaderNameFromInventory();

        if (ScriptsChunkLoadersGameRules.shouldEmitVibration(entity)) {
            level.gameEvent(entity, GameEvent.RESONATE_6, entity.position());
        }
    }

    @Unique
    private void stopCart(ServerLevel level, AbstractMinecart entity, MinecartEntityExt cart) {
        cart.scripts_chunk_loaders$stopChunkLoader();

        if (ScriptsChunkLoadersGameRules.shouldEmitVibration(entity)) {
            level.gameEvent(entity, GameEvent.RESONATE_5, entity.position());
        }
    }
}
