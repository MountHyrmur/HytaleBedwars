package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider
import yt.szczurek.hyrmur.bedwars.component.Team
import java.util.*

class BedwarsGameSpawnProvider(
    val queueSpawnpoints: Array<Transform>,
    val teamSpawnpoints: Map<String, Transform>)
    : ISpawnProvider {

    fun getTeamSpawnpoint(team: String): Transform {
        return teamSpawnpoints[team]!!.clone()
    }

    override fun getSpawnPoint(world: World, uuid: UUID): Transform {
        return queueSpawnpoints.random().clone()
    }

    @Deprecated("Deprecated in Java")
    override fun getSpawnPoints(): Array<out Transform?> {
        return queueSpawnpoints
    }

    override fun isWithinSpawnDistance(position: Vector3d, distance: Double): Boolean {
        val distanceSquared = distance * distance

        for (point in queueSpawnpoints) {
            if (position.distanceSquaredTo(point.getPosition()) < distanceSquared) {
                return true
            }
        }
        return false
    }

    companion object {
        @JvmField
        var CODEC: BuilderCodec<BedwarsGameSpawnProvider> = BuilderCodec.builder(
            BedwarsGameSpawnProvider::class.java
        ) { BedwarsGameSpawnProvider(emptyArray(), emptyMap()) }
            .build()
    }
}