package yt.szczurek.hyrmur.bedwars;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.builtin.instances.InstancesPlugin;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Color;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldConfig;
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.DisabledWorldMapProvider;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class BedwarsMapManager {
    public static final Color DEFAULT_CHUNK_TINT = new Color((byte) 91, (byte) 158, (byte) 40);

    public static CompletableFuture<Optional<Message>> createNewMap(String name) {
       Optional<AssetPack> optionalPack = AssetModule.get().getAssetPacks().stream().filter(assetPack -> !assetPack.isImmutable()).findFirst();
        if (optionalPack.isEmpty()) {
            return CompletableFuture.completedFuture(Optional.of(Message.translation("server.commands.instances.edit.assetsImmutable")));
        }
        AssetPack pack = optionalPack.get();

        Path path = pack.getRoot().resolve("Server").resolve("Instances").resolve(name);
        WorldConfig config = new WorldConfig();
        config.setSpawningNPC(false);
        config.setPvpEnabled(true);
        config.setGameTimePaused(true);
        config.setWorldGenProvider(new VoidWorldGenProvider(DEFAULT_CHUNK_TINT, null));
        config.setSpawnProvider(new GlobalSpawnProvider(new Transform(new Vector3i(0, 125, 0))));
        config.setWorldMapProvider(new DisabledWorldMapProvider());

        try {
            Files.createDirectories(path);
        } catch (IOException err) {
            return CompletableFuture.completedFuture(Optional.of(Message.translation("server.commands.instances.createDirectory.failed").param("errormsg", err.getMessage())));
        }

        return WorldConfig.save(path.resolve("instance.bson"), config)
                .thenApply(_ -> Optional.empty());

    }

    public static CompletableFuture<World> loadMapForEditing(String name) {
        return InstancesPlugin.loadInstanceAssetForEdit(name);
    }

    public static void initializeMapWorld(World world) {
        world.setBlock(0, 100, 0, "Soil_Grass");
    }
}
