package yt.szczurek.hyrmur.bedwars.component.running

import com.hypixel.hytale.component.Component
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry
import java.util.*
import java.util.stream.Collectors

class Generator : Component<EntityStore?> {
    val drops: MutableMap<String, GeneratorDropEntry>
    var level: Int = 0
    val cooldownsByItem: HashMap<String, Long>

    constructor(config: BedwarsGenerator) {
        this.drops = Arrays.stream(config.drops).collect(
            Collectors.toMap(GeneratorDropEntry::item
            ) { it }
        )
        val cooldowns = HashMap<String, Long>()
        for (item in drops.keys) {
            cooldowns[item] = 0L
        }
        this.cooldownsByItem = cooldowns
    }

    constructor(other: Generator) {
        this.drops = HashMap(other.drops)
        this.level = other.level
        this.cooldownsByItem = HashMap(other.cooldownsByItem)
    }

    override fun clone(): Generator {
        return Generator(this)
    }

    companion object {
        val componentType: ComponentType<EntityStore?, Generator>
            get() = BedwarsPlugin.get().generatorComponentType
    }
}
