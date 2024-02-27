package io.nihlen.scriptschunkloaders;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;

import java.util.*;

public class ChunkLoaderManager {
    private long lastTick = 0;
    MinecraftServer server;

    private boolean initialized = false;
    private final Set<Entity> pendingRegistrations = new HashSet<>();
    public HashMap<RegistryKey<World>, HashMap<Long, List<UUID>>> forceLoadedChunks = new HashMap<>();

    public void initialize(MinecraftServer _server) {
        server = _server;

        setupTimer();

        initialized = true;

        handlePendingRegistrations();
    }

    private void handlePendingRegistrations () {
        ScriptsChunkLoadersMod.LOGGER.info("Handling pending registrations");
        pendingRegistrations.forEach(this::registerChunkLoader);
        pendingRegistrations.clear();
    }

    public void registerChunkLoader(Entity entity) {
        if (!initialized) {
            pendingRegistrations.add(entity);
            return;
        }

        var chunkPos = entity.getChunkPos();

        removeChunkLoader(entity);
        ScriptsChunkLoadersMod.LOGGER.info("Adding {} to {}", entity, entity.getWorld().getRegistryKey().getValue());

        var worldRegistryKey = entity.getWorld().getRegistryKey();
        var worldChunks = forceLoadedChunks.computeIfAbsent(worldRegistryKey, s -> new HashMap<>());
        var list = worldChunks.computeIfAbsent(chunkPos.toLong(), s -> new ArrayList<>());
        list.add(entity.getUuid());
    }

    public void removeChunkLoader(Entity entity) {
        ScriptsChunkLoadersMod.LOGGER.info("Removing {} from {}", entity, entity.getWorld().getRegistryKey().getValue());
        var uuid = entity.getUuid();

        var worldRegistryKey = entity.getWorld().getRegistryKey();
        var worldChunks = forceLoadedChunks.get(worldRegistryKey);

        ScriptsChunkLoadersMod.LOGGER.info("worldChunks {}", worldChunks);
        if (worldChunks == null) return;

        var iterator = worldChunks.entrySet().iterator();
        while (iterator.hasNext()) {
            var entry = iterator.next();
            var uuids = entry.getValue();

            var didRemove = uuids.remove(uuid);
            if (didRemove && uuids.isEmpty()) {
                iterator.remove();
            }
        }
    }

    private void setupTimer() {
        ServerTickEvents.END_WORLD_TICK.register((world) -> {
            var time = world.getTime();
            if (time != lastTick && time % 20 == 0) {
                lastTick = time;
                updateWorlds();
            }
        });
    }

    private void updateWorlds() {
        forceLoadedChunks.keySet().forEach(worldRegistryKey -> {
            ServerWorld world = server.getWorld(worldRegistryKey);
            updateChunkLoaders(world);
        });
    }

    private void updateChunkLoaders(ServerWorld world) {
        var currentChunks = calculateLoadedChunks(world);

        // TODO: This can probably be optimized. We're looping over the same range twice since there usually is an
        //  overlap between the current chunks and the loaded chunks.

        final LongSet loadedChunks = world.getForcedChunks();

        currentChunks.forEach(chunkPos -> {
            long longPos = chunkPos.toLong();

            if (!loadedChunks.contains(longPos)) {
                // Load chunk
                world.setChunkForced(chunkPos.x, chunkPos.z, true);
            }
        });

        loadedChunks.forEach(longPos -> {
            var chunkPos = new ChunkPos(longPos);
            if (!currentChunks.contains(chunkPos)) {
                // Unload chunk
                world.setChunkForced(chunkPos.x, chunkPos.z, false);
            }
        });
    }

    private Set<ChunkPos> calculateLoadedChunks (ServerWorld world) {
        Set<ChunkPos> chunks = new HashSet<>();

        var worldChunks = forceLoadedChunks.get(world.getRegistryKey());

        if (worldChunks != null) {
            worldChunks.keySet().forEach(chunkPosLong -> {
                var chunkPos = new ChunkPos(chunkPosLong);
                var surroundingChunks = buildChunkSquare(chunkPos);
                chunks.addAll(Arrays.asList(surroundingChunks));
            });
        }

        return chunks;
    }

    private ChunkPos[] buildChunkSquare(ChunkPos chunkPos) {
        ChunkPos[] chunks = new ChunkPos[9];

        for (int i = 0; i < 9; i++) {
            var xOffset = i % 3 - 1;
            var zOffset = i / 3 - 1;
            chunks[i] = new ChunkPos(chunkPos.x + xOffset, chunkPos.z + zOffset);
        }

        return chunks;
    }
}
