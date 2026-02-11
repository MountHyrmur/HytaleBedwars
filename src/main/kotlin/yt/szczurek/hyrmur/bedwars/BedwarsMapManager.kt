package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetPack
import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig
import com.hypixel.hytale.builtin.instances.removal.InstanceDataResource
import com.hypixel.hytale.builtin.instances.removal.WorldEmptyCondition
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.util.ChunkUtil
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3d
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
import java.util.concurrent.CompletableFuture
import kotlin.io.path.Path
import kotlin.io.path.div

private val DEFAULT_CHUNK_TINT: Color = Color(91.toByte(), 158.toByte(), 40.toByte())
private val MESSAGE_NO_MUTABLE_PACK = Message.translation("server.commands.bedwars.map.create.noMutablePack")
private val MESSAGE_IO_CREATION_FAILED = Message.translation("server.commands.bedwars.map.createIoFail")
private val MESSAGE_NO_INSTANCE = Message.translation("server.commands.bedwars.map.edit.fail.noInstance")
val MESSAGE_NOT_BW_MAP = Message.translation("server.commands.bedwars.map.common.notBwMap")

object BedwarsMapManager {
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
        InstanceWorldConfig.ensureAndGet(config).setRemovalConditions(WorldEmptyCondition())
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

    suspend fun loadForPlaying(map: BedwarsMap, returnTransform: Transform, returnWorld: World): World {
        val instance = map.instance!!
        return InstancesPlugin.get().spawnInstance(instance, returnWorld, returnTransform).await()
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

    fun loadChunks(radius: Int, world: World) {
        for (x in -radius..radius) {
            for (z in -radius..radius) {
                world.getChunk(ChunkUtil.indexChunk(x, z))
            }
        }
    }

    fun onWorldRemoveEvent(event: RemoveWorldEvent) {
        val world = event.world
        if (!isABedwarsMapBeingEdited(world) || event.removalReason == RemoveWorldEvent.RemovalReason.EXCEPTIONAL) {
            return
        }
        // Instance data needs to be set before the event finishes
        // so that it gets saved
        val threadBlockade = CompletableFuture<Unit>()
        world.execute {
            validateAndUpdateMetadata(world)
            val instanceData = world.chunkStore.store.getResource(InstanceDataResource.getResourceType())
            instanceData.worldTimeoutTimer = null
            instanceData.setHadPlayer(false)
            mapsLoadedForEditing.remove(world.worldConfig.uuid)
            threadBlockade.complete(Unit)
        }
        threadBlockade.get()
    }

    fun validateAndUpdateMetadata(world: World): ValidationResult {
        val mapName = mapsLoadedForEditing[world.worldConfig.uuid]!!
        val map = BedwarsMap.assetMap.getAsset(mapName)!!
        loadChunks(map.chunkLoadRadius, world)
        val result = validateWorld(world.entityStore.store)
        updateMapMetadata(world, result.isOk(), map)
        return result
    }

    fun validateWorld(store: Store<EntityStore>): ValidationResult {
        val reports = ArrayList<ValidationReport>()

        val teamSpawnpointCount = store.getEntityCountFor(TeamSpawnpoint.componentType)
        val teamSpawnpointReport = ValidationReport("Found $teamSpawnpointCount team spawnpoints")
        if (teamSpawnpointCount < 2) {
            teamSpawnpointReport.addTextError("Map has less then 2 team spawnpoints")
        }
        val teams = HashMap<String, Vector3d>()
        store.forEachEntityParallel(TeamSpawnpoint.componentType) { i, archetype, _ ->
            val spawnpoint = archetype.getComponent(i, TeamSpawnpoint.componentType)!!
            val position = archetype.getComponent(i, TransformComponent.getComponentType())!!.position
            val formattedPosition = Vector3d.formatShortString(position.clone().floor())
            val team = spawnpoint.team

            val otherTeamPos = teams[team]
            if (otherTeamPos != null) {
                val formattedOtherTeamPos = Vector3d.formatShortString(otherTeamPos.clone().floor())
                teamSpawnpointReport.addTextError("There are two $team teams: at $formattedPosition and at $formattedOtherTeamPos")
            }
            if (team.isEmpty()) {
                teamSpawnpointReport.addTextError("Team spawnpoint at $formattedPosition has no team set")
            } else {
                teams[team] = position
            }
        }
        reports.add(teamSpawnpointReport)

        val queueSpawnpoints = store.getEntityCountFor(QueueSpawnpoint.componentType)
        val queueSpawnpointReport = ValidationReport("Found $queueSpawnpoints queue spawnpoints")
        if (queueSpawnpoints == 0) {
            queueSpawnpointReport.addTextError("Map needs at least one queue spawnpoint")
        }
        reports.add(queueSpawnpointReport)

        return ValidationResult(reports)
    }

    fun updateMapMetadata(world: World, playable: Boolean, map: BedwarsMap) {
        map.teamCount = world.entityStore.store.getEntityCountFor(TeamSpawnpoint.componentType)
        map.playable = playable
        map.saveToDisk()
    }
}

