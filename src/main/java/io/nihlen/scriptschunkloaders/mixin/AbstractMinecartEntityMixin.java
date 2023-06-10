package io.nihlen.scriptschunkloaders.mixin;

import net.minecraft.entity.vehicle.*;
import net.minecraft.item.AirBlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.nihlen.scriptschunkloaders.ScriptsChunkLoadersMod;
import io.nihlen.scriptschunkloaders.MinecartEntityExt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

@Mixin(AbstractMinecartEntity.class)
public abstract class AbstractMinecartEntityMixin extends Entity implements MinecartEntityExt {
	@Shadow public abstract AbstractMinecartEntity.Type getMinecartType();

	private boolean isChunkLoader = false;
	private int particleTicker = 0;
	private final int particleInterval = 3;
	private ChunkPos lastChunkPos = null;

	public AbstractMinecartEntityMixin(EntityType<?> type, World world) {
		super(type, world);
	}

	@Inject(method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;DDD)V", at = @At("TAIL"))
	private void injectConstructor(CallbackInfo callbackInfo) {
		if (isChunkLoader) {
			startChunkLoader();
		}
	}

	public boolean isChunkLoader() {
		return this.isChunkLoader;
	}

	public void startChunkLoader() {
		this.isChunkLoader = true;

		ScriptsChunkLoadersMod.LOGGER.info("Starting chunk loader in {}", this.getWorld().getRegistryKey().getValue());
		//ScriptsChunkLoadersMod.CHUNK_LOADER_MANAGER.registerChunkLoader(this);
	}

	public void setChunkLoaderNameFromInventory() {
		var minecartType = this.getMinecartType();
		if (minecartType == AbstractMinecartEntity.Type.CHEST) {
			var entity = (ChestMinecartEntity)(Object)this;
			var firstSlot = entity.getInventory().get(0);

			if (!firstSlot.isEmpty()) {
				var name = firstSlot.getName().getString();
				setChunkLoaderName("To " + name);
				return;
			}
		};

		setChunkLoaderName("Chunk Loader");
	}

	public void setChunkLoaderName(String name) {
		var nameText = Text.literal(name);
//		nameText.formatted(Formatting.GOLD);
		this.setCustomName(nameText);
		this.setCustomNameVisible(true);
	}

	public void stopChunkLoader() {
		this.isChunkLoader = false;

		ScriptsChunkLoadersMod.CHUNK_LOADER_MANAGER.removeChunkLoader(this);

		this.setCustomName(null);
		this.setCustomNameVisible(false);
	}

	@Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putBoolean("chunkLoader", this.isChunkLoader);
	}

	@Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		this.isChunkLoader = nbt.getBoolean("chunkLoader");
	}

	@Inject(method = "tick", at = @At("TAIL"))
	public void tick(CallbackInfo ci) {
		if (!isChunkLoader) return;

		var chunkPos = this.getChunkPos();
		if (lastChunkPos == null || lastChunkPos != chunkPos) {
			lastChunkPos = chunkPos;
			ScriptsChunkLoadersMod.LOGGER.info("Re-registering chunk loader");
			ScriptsChunkLoadersMod.CHUNK_LOADER_MANAGER.registerChunkLoader(this);
		}

		this.tickParticles();
	}

	@Override
	public void remove(Entity.RemovalReason reason) {
		ScriptsChunkLoadersMod.LOGGER.info("Removing thing from world {}", isChunkLoader);
		if (isChunkLoader) {
			this.stopChunkLoader();
		}

		super.remove(reason);
	}

	@Override
	public Entity moveToWorld(ServerWorld destination) {
		ScriptsChunkLoadersMod.LOGGER.info("Moving thing to world");
		var wasChunkLoader = isChunkLoader;
		if (wasChunkLoader) {
			this.stopChunkLoader();
		}

		var newEntity = super.moveToWorld(destination);

		if (wasChunkLoader) {
			((AbstractMinecartEntityMixin)newEntity).startChunkLoader();
		}

		return newEntity;
	}

	private void tickParticles() {
		this.particleTicker += 1;
		if (this.particleTicker >= particleInterval) {
			this.particleTicker = 0;
			this.spawnParticles();
		}
	}

	private void spawnParticles() {
		AbstractMinecartEntity entity = (AbstractMinecartEntity)(Object)this;
		ServerWorld world = (ServerWorld)entity.getWorld();
		world.spawnParticles(ParticleTypes.HAPPY_VILLAGER, entity.getX(), entity.getY(), entity.getZ(), 1, 0.25, 0.25, 0.25, 0.15f);
	}
}
