package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.system.WorldEventSystem
import com.hypixel.hytale.protocol.SoundCategory
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent
import com.hypixel.hytale.server.core.modules.entity.component.Interactable
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.modules.interaction.Interactions
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.SoundUtil
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.BedwarsGame
import yt.szczurek.hyrmur.bedwars.BedwarsGameSpawnProvider
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.component.Generator
import yt.szczurek.hyrmur.bedwars.component.GeneratorBuilder
import yt.szczurek.hyrmur.bedwars.component.Team
import yt.szczurek.hyrmur.bedwars.event.BedwarsGameStartEvent
import yt.szczurek.hyrmur.bedwars.event.GroupPlayersEvent
import java.util.*

private const val START_SOUND = "SFX_Toad_Rhino_Alerted"

class BedwarsGameStartSystem : WorldEventSystem<EntityStore, BedwarsGameStartEvent>(BedwarsGameStartEvent::class.java) {
    override fun handle(
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: BedwarsGameStartEvent
    ) {
        val soundEventIndex = SoundEvent.getAssetMap().getIndex(START_SOUND)
        SoundUtil.playSoundEvent2d(soundEventIndex, SoundCategory.SFX, 1.5f, 1.0f, store)

        val world = store.externalData.world

        val spawnProvider: BedwarsGameSpawnProvider? = world.worldConfig.spawnProvider as? BedwarsGameSpawnProvider
        spawnProvider ?: throw IllegalStateException("World's spawn provider isn't BedwarsGameSpawnProvider")

        val teamMap = assignTeams(world, store, spawnProvider)
        val playersByUuid: Map<UUID, PlayerRef> = world.playerRefs.associateBy(PlayerRef::getUuid)

        for ((name, members) in teamMap) {
            for (member in members) {
                val player = playersByUuid[member]!!
                val ref = player.reference ?: continue
                commandBuffer.addComponent(ref, Team.componentType, Team(name))
                val teleport = Teleport.createForPlayer(spawnProvider.getTeamSpawnpoint(name))
                commandBuffer.addComponent(ref, Teleport.getComponentType(), teleport)
            }
        }
        commandBuffer.run(this::setupGenerators)
    }

    private fun assignTeams(
        world: World,
        store: Store<EntityStore>,
        spawnProvider: BedwarsGameSpawnProvider
    ): HashMap<String, ArrayList<UUID>> {
        val players = world.playerRefs
        val randomPlayers: LinkedHashSet<UUID> = LinkedHashSet(players.map(PlayerRef::getUuid))

        val groupEvent = GroupPlayersEvent(players)
        store.invoke(groupEvent)
        val groups = groupEvent.groups

        // Remove all grouped players from randomPlayersByUuid
        for (group in groups) {
            for (uuid in group) {
                randomPlayers.remove(uuid)
            }
        }

        val game = BedwarsGame.get(store)!!
        val teamSize = game.config.teamSize
        val teamNames = spawnProvider.teamSpawnpoints.keys

        val teamMap: HashMap<String, ArrayList<UUID>> = HashMap()

        for (teamName in teamNames) {
            val members = ArrayList<UUID>()

            try {
                val group = groups.removeLast()
                if (group.size <= teamSize) {
                    members.addAll(group)
                } else {
                    var i = 0
                    for (member in group) {
                        if (i < teamSize) {
                            members.add(member)
                            i += 1
                        } else {
                            // Return rest of the party to random players
                            randomPlayers.add(member)
                        }
                    }
                }
            } catch (_: NoSuchElementException) {
            } finally {
                // Fill in remaining players from randoms
                while (members.size < teamSize) {
                    try {
                        members.add(randomPlayers.removeFirst())
                    } catch (_: NoSuchElementException) {
                        break
                    }
                }
            }
            teamMap[teamName] = members
        }

        return  teamMap
    }

    private fun setupGenerators(store: Store<EntityStore>) {
        val componentType = GeneratorBuilder.componentType
        store.forEachEntityParallel(componentType) { i, archetypeChunk, commandBuffer ->
            val generatorBuilder = archetypeChunk.getComponent(i, componentType)!!
            val ref = archetypeChunk.getReferenceTo(i)
            val config = BedwarsGenerator.assetMap.getAsset(generatorBuilder.generatorName)!!
            commandBuffer.addComponent(ref, Generator.componentType, Generator(config))
            commandBuffer.tryRemoveComponent(ref, Interactable.getComponentType())
            commandBuffer.tryRemoveComponent(ref, Interactions.getComponentType())
            commandBuffer.tryRemoveComponent(ref, ModelComponent.getComponentType())
        }
    }
}