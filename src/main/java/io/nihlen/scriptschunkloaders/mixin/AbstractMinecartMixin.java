package io.nihlen.scriptschunkloaders.mixin;

import net.minecraft.core.component.DataComponents;
//import net.minecraft.entity.vehicle.*;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.portal.TeleportTransition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.nihlen.scriptschunkloaders.ScriptsChunkLoadersMod;
import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

@Mixin(AbstractMinecart.class)
public abstract class AbstractMinecartMixin extends Entity implements MinecartEntityExt {

	@Unique
	private boolean isChunkLoader = false;
	@Unique
	private int particleTicker = 0;
	@Unique
	private final int particleInterval = 3;
	@Unique
	private ChunkPos lastChunkPos = null;

	public AbstractMinecartMixin(EntityType<?> type, Level world) {
		super(type, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/world/entity/EntityType;Lnet/minecraft/world/level/Level;DDD)V", at = @At("TAIL"))
	private void injectConstructor(CallbackInfo callbackInfo) {
		if (isChunkLoader) {
			scripts_chunk_loaders$startChunkLoader();
		}
	}

	public boolean scripts_chunk_loaders$isChunkLoader() {
		return this.isChunkLoader;
	}

	public void scripts_chunk_loaders$startChunkLoader() {
		if (this.level().isClientSide()) return;

		this.isChunkLoader = true;

		ScriptsChunkLoadersMod.LOGGER.info("Starting chunk loader (ID: {}) at ({}, {}, {}) in {}", this.getId(), (int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level().dimension().identifier());
	}

	public void scripts_chunk_loaders$setChunkLoaderNameFromInventory() {
		EntityType<?> minecartType = this.getType();

		if (minecartType == EntityType.CHEST_MINECART) {
			//noinspection DataFlowIssue - We're sure this is a chest because of the if statement.
			var entity = (MinecartChest)(Object)this;
			var firstSlot = entity.getItemStacks().get(0);

			var hasCustomName = firstSlot.get(DataComponents.CUSTOM_NAME) != null;
			
			if (!firstSlot.isEmpty() && hasCustomName) {
				var name = firstSlot.getHoverName().getString();
				scripts_chunk_loaders$setChunkLoaderName(name);
				return;
			}
		};

		scripts_chunk_loaders$setChunkLoaderName("Chunk Loader");
	}

	public void scripts_chunk_loaders$setChunkLoaderName(String name) {
		var nameText = Component.literal(name);
		this.setCustomName(nameText);
		this.setCustomNameVisible(true);
	}

	public void scripts_chunk_loaders$stopChunkLoader() {
		scripts_chunk_loaders$stopChunkLoader(false);
		this.lastChunkPos = null;
	}
	@Unique
	public void scripts_chunk_loaders$stopChunkLoader(Boolean keepName) {
		ScriptsChunkLoadersMod.LOGGER.info("Stopping chunk loader '{}' (ID: {}) at ({}, {}, {}) in {}", this.getName().getString(), this.getId(), (int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level().dimension().identifier());
		this.isChunkLoader = false;

		ScriptsChunkLoadersMod.CHUNK_LOADER_MANAGER.removeChunkLoader(this);

		if (!keepName) {
			this.setCustomName(null);
			this.setCustomNameVisible(false);
		}
	}

	@Inject(method = "addAdditionalSaveData", at = @At("RETURN"))
	public void writeCustomData(ValueOutput view, CallbackInfo ci) {
		view.putBoolean("chunkLoader", this.isChunkLoader);
	}

	@Inject(method = "readAdditionalSaveData", at = @At("RETURN"))
	public void readCustomData(ValueInput view, CallbackInfo ci) {
		this.isChunkLoader = view.getBooleanOr("chunkLoader", false);
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (!isChunkLoader) return;

		var chunkPos = this.chunkPosition();
		if (lastChunkPos == null || lastChunkPos != chunkPos) {
			lastChunkPos = chunkPos;
			ScriptsChunkLoadersMod.LOGGER.debug("Re-registering chunk loader");
			ScriptsChunkLoadersMod.CHUNK_LOADER_MANAGER.registerChunkLoader(this);
		}

		this.tickParticles();
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		if (isChunkLoader) {
			this.scripts_chunk_loaders$stopChunkLoader();
		}

		super.remove(reason);
	}

	@Override
	public Entity teleport(TeleportTransition teleportTarget) {
		var wasChunkLoader = isChunkLoader;
		if (wasChunkLoader)
			this.scripts_chunk_loaders$stopChunkLoader(true);

		var newEntity = super.teleport(teleportTarget);

		if (wasChunkLoader && newEntity != null)
			((AbstractMinecartMixin)newEntity).scripts_chunk_loaders$startChunkLoader();

		return newEntity;
	}

	@Unique
	private void tickParticles() {
		this.particleTicker += 1;
		if (this.particleTicker >= particleInterval) {
			this.particleTicker = 0;
			this.spawnParticles();
		}
	}

	@Unique
	private void spawnParticles() {
		AbstractMinecart entity = (AbstractMinecart)(Object)this;
		ServerLevel world = (ServerLevel)entity.level();
		world.sendParticles(ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY(), entity.getZ(), 1, 0.25, 0.25, 0.25, 0.15f);
	}
}
