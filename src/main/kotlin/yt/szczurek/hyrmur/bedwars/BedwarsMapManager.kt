package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetPack
import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig
import com.hypixel.hytale.builtin.instances.removal.IdleTimeoutCondition
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.AssetModule
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
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
import kotlin.io.path.div

object BedwarsMapManager {
    val DEFAULT_CHUNK_TINT: Color = Color(91.toByte(), 158.toByte(), 40.toByte())
    val MESSAGE_ALREADY_EXISTS = Message.translation("server.commands.bedwars.map.create.alreadyExists")

    fun createNew(name: String): Message? {
        val optionalPack =
            AssetModule.get().assetPacks.stream().filter { assetPack: AssetPack -> !assetPack.isImmutable }.findFirst()
        if (optionalPack.isEmpty) {
            return Message.translation("server.commands.instances.edit.assetsImmutable")
        }
        val pack = optionalPack.get()

        if (doesMapExist(name)) {
            return MESSAGE_ALREADY_EXISTS.param("name", name)
        }

        val configPath = pack.root / "Server" / "Instances" / name
        val config = WorldConfig()
        config.isSpawningNPC = false
        config.isPvpEnabled = true
        config.isGameTimePaused = true
        config.worldGenProvider = VoidWorldGenProvider(DEFAULT_CHUNK_TINT, "Env_Zone1_Plains")
        config.spawnProvider = GlobalSpawnProvider(Transform(Vector3i(0, 125, 0)))
        config.worldMapProvider = DisabledWorldMapProvider()

        try {
            Files.createDirectories(configPath)
        } catch (err: IOException) {
            return Message.translation("server.commands.instances.createDirectory.failed").param("errormsg", err.message!!)
        }

        WorldConfig.save(configPath.resolve("instance.bson"), config).join()

        return null
    }

    fun loadForEditing(name: String): World {
        val path = InstancesPlugin.getInstanceAssetPath(name)
        val universe = Universe.get()
        val config = WorldConfig.load(path.resolve("instance.bson")).join()
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
        return universe.makeWorld(worldName, path, config).join()
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
        player: Ref<EntityStore?>, targetWorld: World, store: Store<EntityStore?>
    ) {
        val transformComponent = store.getComponent(player, TransformComponent.getComponentType())
        val transform = transformComponent?.transform?.clone()
        InstancesPlugin.teleportPlayerToInstance(player, store, targetWorld, transform)
    }
}


