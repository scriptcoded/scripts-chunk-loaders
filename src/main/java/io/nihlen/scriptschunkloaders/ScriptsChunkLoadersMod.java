package io.nihlen.scriptschunkloaders;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.BlockView;
import net.minecraft.world.ChunkRegion;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScriptsChunkLoadersMod implements ModInitializer {
	public static final String MODID = "scripts-chunk-loaders";

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
	public static final ChunkLoaderManager CHUNK_LOADER_MANAGER = new ChunkLoaderManager();

    public static final GameRules.Key<GameRules.BooleanRule> ALWAYS_SHOW_LOADER_NAME = GameRuleRegistry.register("alwaysShowLoaderName", GameRules.Category.MISC, GameRuleFactory.createBooleanRule(true));

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		ServerLifecycleEvents.SERVER_STARTED.register(CHUNK_LOADER_MANAGER::initialize);
	}

    public static boolean isCustomNameVisible(BlockView world) {
        GameRules rules = null;

        if (world instanceof ServerWorld serverWorld)
            rules = serverWorld.getGameRules();
        else if (world instanceof ChunkRegion chunkRegion)
        {
            MinecraftServer server = chunkRegion.getServer();

            if (server != null) {
                rules = server.getGameRules();
            }
        }

        if (rules != null) {
            return rules.getBoolean(ALWAYS_SHOW_LOADER_NAME);
        }

        return true;
    }
}
