package io.nihlen.scriptschunkloaders;

import net.minecraft.util.math.ChunkPos;

import java.util.UUID;

public interface MinecartEntityExt {
    public void startChunkLoader();
    public void stopChunkLoader();
    public boolean isChunkLoader();
    public void setChunkLoaderNameFromInventory();
    public void setChunkLoaderName(String name);
}
