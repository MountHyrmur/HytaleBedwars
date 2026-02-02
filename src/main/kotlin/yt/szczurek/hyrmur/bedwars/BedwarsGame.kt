package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap

class BedwarsGame(val world: World) {

    fun addPlayer(player: PlayerRef) {

    }

    fun removePlayer(player: PlayerRef) {

    }

    companion object {
        suspend fun initialize(mapName: String, returnTransform: Transform, returnWorld: World): BedwarsGame {
            val mapAsset = BedwarsMap.assetMap.getAsset(mapName)
                ?: throw IllegalArgumentException("Map asset named $mapName does not exist")

            if (!mapAsset.playable) {
                throw IllegalArgumentException("Setup of map $mapName is not finished")
            }

            val world = BedwarsMapManager.loadForPlaying(mapAsset, returnTransform, returnWorld)

            return BedwarsGame(world)
        }
    }
}