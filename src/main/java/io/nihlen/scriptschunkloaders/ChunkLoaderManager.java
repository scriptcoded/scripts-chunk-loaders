package io.nihlen.scriptschunkloaders;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.Ticket;
import net.minecraft.world.entity.Entity;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

import java.util.*;

public class ChunkLoaderManager {
    private long lastTick = 0;
    MinecraftServer server;

    private boolean initialized = false;
    private final Set<Entity> pendingRegistrations = new HashSet<>();
    public HashMap<ResourceKey<Level>, HashMap<Long, List<UUID>>> forceLoadedChunks = new HashMap<>();

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

        var chunkPos = entity.chunkPosition();

        removeChunkLoader(entity);
        ScriptsChunkLoadersMod.LOGGER.info("Adding {} to {}", entity, entity.level().dimension().identifier());

        var worldRegistryKey = entity.level().dimension();
        var worldChunks = forceLoadedChunks.computeIfAbsent(worldRegistryKey, s -> new HashMap<>());
        var list = worldChunks.computeIfAbsent(chunkPos.pack(), s -> new ArrayList<>());
        list.add(entity.getUUID());
    }

    public void removeChunkLoader(Entity entity) {
        ScriptsChunkLoadersMod.LOGGER.info("Removing {} from {}", entity, entity.level().dimension().identifier());
        var uuid = entity.getUUID();

        var worldRegistryKey = entity.level().dimension();
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
        ServerTickEvents.END_LEVEL_TICK.register((world) -> {
            var time = world.getGameTime();
            if (time != lastTick && time % 20 == 0) {
                lastTick = time;
                updateWorlds();
            }
        });
    }

    private void updateWorlds() {
        forceLoadedChunks.keySet().forEach(worldRegistryKey -> {
            ServerLevel world = server.getLevel(worldRegistryKey);
            updateChunkLoaders(world);
        });
    }

    private void updateChunkLoaders(ServerLevel world) {
        var currentChunks = calculateLoadedChunks(world);

        // TODO: This can probably be optimized. We're looping over the same range twice since there usually is an
        //  overlap between the current chunks and the loaded chunks.

        final LongSet loadedChunks = world.getForceLoadedChunks();

        currentChunks.forEach(chunkPos -> {
            long longPos = chunkPos.pack();

            if (!loadedChunks.contains(longPos)) {
                // Load chunk
                var ticket = new Ticket(ScriptsChunkLoadersMod.CUSTOM_TICKETTYPE_FORCED, ChunkMap.FORCED_TICKET_LEVEL);
                world.getChunkSource().addTicket(ticket, chunkPos);
            }
        });

        loadedChunks.forEach(longPos -> {
            var chunkPos = ChunkPos.unpack(longPos);
            if (!currentChunks.contains(chunkPos)) {
                // Unload chunk
                world.getChunkSource().removeTicketWithRadius(ScriptsChunkLoadersMod.CUSTOM_TICKETTYPE_FORCED, chunkPos, 2);
            }
        });
    }

    private Set<ChunkPos> calculateLoadedChunks (ServerLevel world) {
        Set<ChunkPos> chunks = new HashSet<>();

        var worldChunks = forceLoadedChunks.get(world.dimension());

        if (worldChunks != null) {
            worldChunks.keySet().forEach(chunkPosLong -> {
                var chunkPos = ChunkPos.unpack(chunkPosLong);
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
            chunks[i] = new ChunkPos(chunkPos.x() + xOffset, chunkPos.z() + zOffset);
        }

        return chunks;
    }
}
