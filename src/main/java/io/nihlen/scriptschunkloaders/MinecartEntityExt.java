package io.nihlen.scriptschunkloaders;

public interface MinecartEntityExt {
    void scripts_chunk_loaders$startChunkLoader();
    void scripts_chunk_loaders$stopChunkLoader();
    boolean scripts_chunk_loaders$isChunkLoader();
    void scripts_chunk_loaders$setChunkLoaderNameFromInventory();
    void scripts_chunk_loaders$setChunkLoaderName(String name);
}
