package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetPack
import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig
import com.hypixel.hytale.builtin.instances.removal.IdleTimeoutCondition
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.AssetModule
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.WorldConfig
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.DisabledWorldMapProvider
import java.io.IOException
import java.nio.file.Files
import java.util.*
import java.util.concurrent.CompletableFuture

object BedwarsMapManager {
    val DEFAULT_CHUNK_TINT: Color = Color(91.toByte(), 158.toByte(), 40.toByte())
    val MESSAGE_ALREADY_EXISTS = Message.translation("server.commands.bedwars.map.create.alreadyExists")

    fun createNew(name: String): CompletableFuture<Message?> {
        val optionalPack =
            AssetModule.get().assetPacks.stream().filter { assetPack: AssetPack -> !assetPack.isImmutable }.findFirst()
        if (optionalPack.isEmpty) {
            return CompletableFuture.completedFuture((Message.translation("server.commands.instances.edit.assetsImmutable")))
        }
        val pack = optionalPack.get()

        if (doesMapExist(name)) {
            return CompletableFuture.completedFuture(MESSAGE_ALREADY_EXISTS.param("name", name))
        }

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

    fun loadForEditing(name: String): CompletableFuture<World> {
        val path = InstancesPlugin.getInstanceAssetPath(name)
        val universe = Universe.get()
        return WorldConfig.load(path.resolve("instance.bson")).thenCompose { config: WorldConfig ->
            config.uuid = UUID.randomUUID()
            config.isSavingPlayers = false
            @Suppress("UsePropertyAccessSyntax") // Doesn't compile for some reason
            config.setIsAllNPCFrozen(true)
            config.isTicking = false
            config.gameMode = GameMode.Creative
            config.isDeleteOnRemove = false
            InstanceWorldConfig.ensureAndGet(config).setRemovalConditions(IdleTimeoutCondition())
            config.markChanged()
            val worldName = getWorldNameForEditing(name)
            universe.makeWorld(worldName, path, config)
        }
    }

    fun getWorldNameForEditing(name: String): String {
        return "bedwars-edit-" + InstancesPlugin.safeName(name)
    }

    fun getWorldLoadedForEditing(name: String): World? {
        return Universe.get().getWorld(getWorldNameForEditing(name))
    }

    fun doesMapExist(name: String): Boolean = InstancesPlugin.doesInstanceAssetExist(name)

    fun initializeWorld(world: World) {
        world.setBlock(0, 100, 0, "Soil_Grass")
    }

    fun teleportPlayerToWorld(
        player: PlayerRef, playerWorld: World, targetWorld: World, store: Store<EntityStore?>
    ) {
        playerWorld.execute {
            val spawnTransform = targetWorld.worldConfig.spawnProvider!!.getSpawnPoint(player.reference!!, store)
            val teleportComponent = Teleport.createForPlayer(targetWorld, spawnTransform)
            store.addComponent(player.reference!!, Teleport.getComponentType(), teleportComponent)
        }
    }
}


