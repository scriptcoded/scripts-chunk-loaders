package io.nihlen.scriptschunkloaders;

import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;

/// Class containing all the game rules of this mod
@SuppressWarnings("unused")
public class ScriptsChunkLoadersGameRules {
    private static GameRule<Boolean> ALWAYS_SHOW_LOADER_NAME;
    private static GameRule<Boolean> EMIT_VIBRATION_ON_LOADER_CHANGE;

    /// Registers all the rules of this mod
    /// @implNote Work around for not instantiating it
    public static void registerAllRules() {
        ALWAYS_SHOW_LOADER_NAME = Registry.register(BuiltInRegistries.GAME_RULE, "always_show_loader_name", GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MISC).build());
        EMIT_VIBRATION_ON_LOADER_CHANGE = Registry.register(BuiltInRegistries.GAME_RULE, "emit_vibration_on_loader_change", GameRuleBuilder.forBoolean(true).category(GameRuleCategory.MISC).build());
    }

    /// Defines if the given entity should always show its custom name
    public static boolean shouldAlwaysShowCustomName(Entity entity) {
        MinecraftServer server = entity.level().getServer();

        if (server == null)
            return true;

        return server.getGameRules().get(ALWAYS_SHOW_LOADER_NAME);
    }
    
    /// Defines if the given entity should emit a vibration
    public static boolean shouldEmitVibration(Entity entity) {
        MinecraftServer server = entity.level().getServer();

        if (server == null)
            return true;

        return server.getGameRules().get(EMIT_VIBRATION_ON_LOADER_CHANGE);
    }
}