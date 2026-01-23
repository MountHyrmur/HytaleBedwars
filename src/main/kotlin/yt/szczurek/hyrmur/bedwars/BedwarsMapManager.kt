package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetPack
import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.AssetModule
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.WorldConfig
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.DisabledWorldMapProvider
import java.io.IOException
import java.nio.file.Files
import java.util.concurrent.CompletableFuture

object BedwarsMapManager {
    val DEFAULT_CHUNK_TINT: Color = Color(91.toByte(), 158.toByte(), 40.toByte())

    fun createNewMap(name: String): CompletableFuture<Message?> {
        val optionalPack =
            AssetModule.get().assetPacks.stream().filter { assetPack: AssetPack -> !assetPack.isImmutable }.findFirst()
        if (optionalPack.isEmpty) {
            return CompletableFuture.completedFuture((Message.translation("server.commands.instances.edit.assetsImmutable")))
        }
        val pack = optionalPack.get()

        val path = pack.root.resolve("Server").resolve("Instances").resolve(name)
        val config = WorldConfig()
        config.isSpawningNPC = false
        config.isPvpEnabled = true
        config.isGameTimePaused = true
        config.worldGenProvider = VoidWorldGenProvider(DEFAULT_CHUNK_TINT, null)
        config.spawnProvider = GlobalSpawnProvider(Transform(Vector3i(0, 125, 0)))
        config.worldMapProvider = DisabledWorldMapProvider()

        try {
            Files.createDirectories(path)
        } catch (err: IOException) {
            return CompletableFuture.completedFuture(
                Message.translation("server.commands.instances.createDirectory.failed").param("errormsg", err.message!!)
            )
        }

        return WorldConfig.save(path.resolve("instance.bson"), config).thenApply { null }
    }

    fun loadMapForEditing(name: String): CompletableFuture<World> {
        return InstancesPlugin.loadInstanceAssetForEdit(name)
    }

    fun initializeMapWorld(world: World) {
        world.setBlock(0, 100, 0, "Soil_Grass")
    }
}
