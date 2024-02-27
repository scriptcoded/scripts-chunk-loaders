package io.nihlen.scriptschunkloaders;

public interface MinecartEntityExt {
    public void scripts_chunk_loaders$startChunkLoader();
    public void scripts_chunk_loaders$stopChunkLoader();
    public boolean scripts_chunk_loaders$isChunkLoader();
    public void scripts_chunk_loaders$setChunkLoaderNameFromInventory();
    public void scripts_chunk_loaders$setChunkLoaderName(String name);
}
