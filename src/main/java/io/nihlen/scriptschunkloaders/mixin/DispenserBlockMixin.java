package io.nihlen.scriptschunkloaders.mixin;

// import io.nihlen.scriptschunkloaders.ScriptsChunkLoadersMod;
// import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import io.nihlen.scriptschunkloaders.ScriptsChunkLoadersMod;
import net.minecraft.block.DispenserBlock;
// import net.minecraft.block.entity.DispenserBlockEntity;
// import net.minecraft.entity.vehicle.AbstractMinecartEntity;
// import net.minecraft.item.Item;
// import net.minecraft.item.ItemStack;
// import net.minecraft.item.Items;
// import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.block.entity.DispenserBlockEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPointer;
// import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPointerImpl;
import net.minecraft.util.math.BlockPos;
// import net.minecraft.util.math.Box;

// import java.util.List;

import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
    @Inject(
            at = @At("HEAD"),
            method = "dispense(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/util/math/BlockPos;)V",
            cancellable = true
    )
    private void dispense(ServerWorld world, BlockPos pos, CallbackInfo info) {
        BlockPointerImpl blockPointerImpl = new BlockPointerImpl(world, pos);
        DispenserBlockEntity dispenserBlockEntity = blockPointerImpl.getBlockEntity();

        // This can't be a property because the item registry complains for som reason.
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

        this.createMinecraftChunkLoader(blockPointerImpl);
        info.cancel();
    }

    private void createMinecraftChunkLoader(BlockPointer pointer) {
        BlockPos blockPos = pointer.getPos().offset(pointer.getBlockState().get(DispenserBlock.FACING));
        List<AbstractMinecartEntity> list = pointer.getWorld().getEntitiesByClass(AbstractMinecartEntity.class, new Box(blockPos), EntityPredicates.VALID_ENTITY);

        for (AbstractMinecartEntity entity : list) {
            MinecartEntityExt cart = (MinecartEntityExt)entity;
            cart.startChunkLoader();
            cart.setChunkLoaderNameFromInventory();
        }
    }
}
