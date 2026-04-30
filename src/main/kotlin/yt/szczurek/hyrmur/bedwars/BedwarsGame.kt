package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.builtin.instances.config.InstanceWorldConfig
import com.hypixel.hytale.builtin.instances.removal.WorldEmptyCondition
import com.hypixel.hytale.component.*
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.CompletableDeferred
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGameConfig
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import yt.szczurek.hyrmur.bedwars.component.PreGameCountdown
import yt.szczurek.hyrmur.bedwars.component.QueueSpawnpoint
import yt.szczurek.hyrmur.bedwars.component.TeamSpawnpoint

class BedwarsGame(val mapAsset: BedwarsMap, val config: BedwarsGameConfig, val world: World) {

    suspend fun init() {

        BedwarsPlugin.LOGGER.atInfo().log("Loading chunks for game")
        BedwarsMapManager.loadChunksAsync(mapAsset.chunkLoadRadius, world)
        BedwarsPlugin.LOGGER.atInfo().log("Chunks loaded")

        val deferred = CompletableDeferred<Unit>()
        world.execute {
            this.worldInit()
            deferred.complete(Unit)
        }
        deferred.await()
    }


    fun addPlayer(player: Ref<EntityStore>, accessor: ComponentAccessor<EntityStore>) {
        InstancesPlugin.teleportPlayerToInstance(player, accessor, world, null)
    }

    fun removePlayer(player: PlayerRef) {

    }

    private fun worldInit() {
        val store = world.entityStore.store
        store.getResource(BedwarsGameHolder.resourceType).game = this

        val queueSpawnpoints = ArrayList<Transform>()

        store.forEachEntityParallel(QueueSpawnpoint.componentType) { i, chunk, commandBuffer ->
            val transform = chunk.getComponent(i, TransformComponent.getComponentType())!!
            queueSpawnpoints.add(transform.transform)
            commandBuffer.removeEntity(chunk.getReferenceTo(i), RemoveReason.REMOVE)
        }

        val teamSpawnpoints: HashMap<String, Transform> = HashMap()

        store.forEachEntityParallel(TeamSpawnpoint.componentType) { i, chunk, commandBuffer ->
            val team = chunk.getComponent(i, TeamSpawnpoint.componentType)!!
            val transform = chunk.getComponent(i, TransformComponent.getComponentType())!!
            teamSpawnpoints[team.team] = transform.transform.clone()
            commandBuffer.removeEntity(chunk.getReferenceTo(i), RemoveReason.REMOVE)
        }

        val worldConfig = world.worldConfig
        worldConfig.spawnProvider = BedwarsGameSpawnProvider(queueSpawnpoints.toTypedArray(), teamSpawnpoints)
        worldConfig.isDeleteOnRemove = true
        worldConfig.isDeleteOnUniverseStart = true
        worldConfig.isSavingPlayers = false
        worldConfig.setCanSaveChunks(false)
        InstanceWorldConfig.ensureAndGet(worldConfig).setRemovalConditions(WorldEmptyCondition(90.0))
        worldConfig.markChanged()

        val requiredPlayers = mapAsset.teamCount * config.teamSize

        val holder = EntityStore.REGISTRY.newHolder()
        holder.addComponent(PreGameCountdown.componentType, PreGameCountdown(requiredPlayers))
        store.addEntity(holder, AddReason.SPAWN)
    }

    companion object {
        suspend fun initialize(
            mapName: String,
            config: BedwarsGameConfig,
            returnTransform: Transform,
            returnWorld: World
        ): BedwarsGame {
            val mapAsset = BedwarsMap.assetMap.getAsset(mapName)
                ?: throw IllegalArgumentException("Map asset named $mapName does not exist")

            if (!mapAsset.playable) {
                throw IllegalArgumentException("Setup of map $mapName is not finished")
            }

            BedwarsPlugin.LOGGER.atInfo().log("Loading map $mapName for playing")
            val world = BedwarsMapManager.loadForPlaying(mapAsset, returnTransform, returnWorld)
            val game = BedwarsGame(mapAsset, config, world)

            BedwarsPlugin.LOGGER.atInfo().log("Initializing game on ${world.name}")

            game.init()

            BedwarsPlugin.LOGGER.atInfo().log("Game initialized")

            return game
        }

        fun get(store: Store<EntityStore>): BedwarsGame? {
            return store.getResource(BedwarsGameHolder.resourceType).game
        }
    }
}