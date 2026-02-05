package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.builtin.instances.InstancesPlugin
import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.CompletableDeferred
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import yt.szczurek.hyrmur.bedwars.component.QueueSpawnpoint

class BedwarsGame(val mapAsset: BedwarsMap, val world: World) {


    suspend fun init() {

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
        BedwarsMapManager.loadChunks(mapAsset.chunkLoadRadius, world)
        val store = world.entityStore.store

        val queueSpawnpoints = ArrayList<Transform>()

        store.forEachEntityParallel(QueueSpawnpoint.query) { i, chunk, _ ->
            val transform = chunk.getComponent(i, TransformComponent.getComponentType())!!
            queueSpawnpoints.add(transform.transform)
        }

        val worldConfig = world.worldConfig
        worldConfig.spawnProvider = BedwarsGameSpawnProvider(queueSpawnpoints.toTypedArray())
        worldConfig.markChanged()
    }

    companion object {
        suspend fun initialize(mapName: String, returnTransform: Transform, returnWorld: World): BedwarsGame {
            val mapAsset = BedwarsMap.assetMap.getAsset(mapName)
                ?: throw IllegalArgumentException("Map asset named $mapName does not exist")

            if (!mapAsset.playable) {
                throw IllegalArgumentException("Setup of map $mapName is not finished")
            }

            val world = BedwarsMapManager.loadForPlaying(mapAsset, returnTransform, returnWorld)
            val game = BedwarsGame(mapAsset, world)

            game.init()

            return game
        }
    }
}