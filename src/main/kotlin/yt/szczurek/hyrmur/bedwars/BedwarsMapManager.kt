package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetPack
import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig
import com.hypixel.hytale.builtin.instances.removal.IdleTimeoutCondition
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3i
import com.hypixel.hytale.protocol.Color
import com.hypixel.hytale.protocol.GameMode
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.asset.AssetModule
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.Universe
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.WorldConfig
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent
import com.hypixel.hytale.server.core.universe.world.spawn.GlobalSpawnProvider
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.VoidWorldGenProvider
import com.hypixel.hytale.server.core.universe.world.worldmap.provider.DisabledWorldMapProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import yt.szczurek.hyrmur.bedwars.component.QueueSpawnpoint
import yt.szczurek.hyrmur.bedwars.component.TeamSpawnpoint
import java.io.IOException
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path
import kotlin.io.path.div

object BedwarsMapManager {
    val DEFAULT_CHUNK_TINT: Color = Color(91.toByte(), 158.toByte(), 40.toByte())
    val MESSAGE_NO_MUTABLE_PACK = Message.translation("server.commands.bedwars.map.create.noMutablePack")
    val MESSAGE_IO_CREATION_FAILED = Message.translation("server.commands.bedwars.map.createIoFail")
    val MESSAGE_NO_INSTANCE = Message.translation("server.commands.bedwars.map.edit.fail.noInstance")

    private val mapsLoadedForEditing: HashMap<UUID, String> = HashMap()

    suspend fun createNew(name: String) {
        val optionalPack =
            AssetModule.get().assetPacks.stream().filter { assetPack: AssetPack -> !assetPack.isImmutable }.findFirst()
        if (optionalPack.isEmpty) {
            throw ExceptionWithMessage(MESSAGE_NO_MUTABLE_PACK)
        }
        val pack = optionalPack.get()

        createNewMapInstance(name)

        val map = BedwarsMap(name)
        val mapDirectoryPath = pack.root / "Server" / "Bedwars" / "Maps"

        try {
            withContext(Dispatchers.IO) {
                Files.createDirectories(mapDirectoryPath)
            }
        } catch (err: IOException) {
            throw ExceptionWithMessage(MESSAGE_IO_CREATION_FAILED, err)
        }

        try {
            withContext(Dispatchers.IO) {
                Files.createDirectories(mapDirectoryPath)
                BedwarsMap.assetStore.writeAssetToDisk(pack, mapOf(Path("$name.json") to map))
            }
        } catch (err: IOException) {
            throw ExceptionWithMessage(
                Message.translation("server.commands.instances.createDirectory.failed")
                    .param("errormsg", err.message!!)
            )
        }
    }

    suspend fun createNewMapInstance(name: String) {
        if (InstancesPlugin.doesInstanceAssetExist(name)) {
            return
        }

        val optionalPack =
            AssetModule.get().assetPacks.stream().filter { assetPack: AssetPack -> !assetPack.isImmutable }.findFirst()
        if (optionalPack.isEmpty) {
            throw ExceptionWithMessage(Message.translation("server.commands.instances.edit.assetsImmutable"))
        }
        val pack = optionalPack.get()

        val configPath = pack.root / "Server" / "Instances" / name
        val config = WorldConfig()
        config.isSpawningNPC = false
        config.isPvpEnabled = true
        config.isGameTimePaused = true
        config.worldGenProvider = VoidWorldGenProvider(DEFAULT_CHUNK_TINT, "Env_Zone1_Plains")
        config.spawnProvider = GlobalSpawnProvider(Transform(Vector3i(0, 125, 0)))
        config.worldMapProvider = DisabledWorldMapProvider()

        try {
            withContext(Dispatchers.IO) {
                Files.createDirectories(configPath)
            }
        } catch (err: IOException) {
            throw ExceptionWithMessage(Message.translation("server.commands.instances.createDirectory.failed").param("errormsg", err.message!!))
        }

        WorldConfig.save(configPath.resolve("instance.bson"), config).await()
    }

    suspend fun loadForEditing(name: String): World {
        val map = BedwarsMap.assetMap.getAsset(name)!!
        val instance = map.instance!!
        val path = InstancesPlugin.getInstanceAssetPath(instance)

        if (!Files.exists(path.resolve("instance.bson"))) {
            throw ExceptionWithMessage(MESSAGE_NO_INSTANCE.param("name", name).param("instance", instance))
        }

        val universe = Universe.get()
        val config = WorldConfig.load(path.resolve("instance.bson")).await()
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
        val world = universe.makeWorld(worldName, path, config).await()
        mapsLoadedForEditing[world.worldConfig.uuid] = name

        return  world
    }

    fun getWorldNameForEditing(name: String): String {
        return "bedwars-edit-" + InstancesPlugin.safeName(name)
    }

    fun getWorldLoadedForEditing(mapName: String): World? {
        try {
            val uuid = mapsLoadedForEditing.entries.first { entry -> entry.value == mapName }.key
            return Universe.get().getWorld(uuid)
        } catch (_: NoSuchElementException) {
            return null
        }

    }

    fun doesMapExist(name: String): Boolean = BedwarsMap.assetMap.getAsset(name) != null

    fun isABedwarsMapBeingEdited(world: World): Boolean {
        return mapsLoadedForEditing.containsKey(world.worldConfig.uuid)
    }

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

    fun onWorldRemoveEvent(event: RemoveWorldEvent) {
        updateMapMetadata(event.world)
        mapsLoadedForEditing.remove(event.world.worldConfig.uuid)
    }

    fun validateWorld(store: Store<EntityStore>): ValidationResult {
        val reports = ArrayList<ValidationReport>()

        val teamSpawnpointQuery = Query.and(TeamSpawnpoint.componentType)
        val teamSpawnpoints = store.getEntityCountFor(teamSpawnpointQuery)
        val teamSpawnpointReport = ValidationReport("Found $teamSpawnpoints team spawnpoints")
        if (teamSpawnpoints < 2) {
            teamSpawnpointReport.addTextError("Map has less then 2 team spawnpoints")
        }
        reports.add(teamSpawnpointReport)

        val queueSpawnpointQuery = Query.and(QueueSpawnpoint.componentType)
        val queueSpawnpoints = store.getEntityCountFor(queueSpawnpointQuery)
        val queueSpawnpointReport = ValidationReport("Found $queueSpawnpoints queue spawnpoints")
        if (queueSpawnpoints == 0) {
            queueSpawnpointReport.addTextError("Map needs at least one queue spawnpoint")
        }
        reports.add(queueSpawnpointReport)

        return ValidationResult(reports)
    }

    fun updateMapMetadata(world: World) {
        val assetName = mapsLoadedForEditing[world.worldConfig.uuid]
        if (assetName == null) {
            BedwarsPlugin.LOGGER.atWarning().log("Called updateMapMetadata for world ${world.name} which isn't being edited")
            return
        }
        val map = BedwarsMap.assetMap.getAsset(assetName) ?: return
        map.teamCount = world.entityStore.store.getEntityCountFor(Query.and(TeamSpawnpoint.componentType))
        map.saveToDisk()
    }
}


