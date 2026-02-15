package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.component.ResourceType
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.math.vector.Transform
import com.hypixel.hytale.server.core.asset.HytaleAssetStore
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.future.future
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGameConfig
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam
import yt.szczurek.hyrmur.bedwars.command.BedwarsCommand
import yt.szczurek.hyrmur.bedwars.component.*
import yt.szczurek.hyrmur.bedwars.interaction.SnapToGridInteraction
import yt.szczurek.hyrmur.bedwars.page.GeneratorEditorPageSupplier
import yt.szczurek.hyrmur.bedwars.page.TeamSpawnpointEditorPageSupplier
import yt.szczurek.hyrmur.bedwars.system.BedwarsGameStartSystem
import yt.szczurek.hyrmur.bedwars.system.BlockProtectionSystems
import yt.szczurek.hyrmur.bedwars.system.GeneratorSystem
import yt.szczurek.hyrmur.bedwars.system.UpdateGeneratorFromBuilderSystem
import java.awt.Color
import java.util.concurrent.CompletableFuture

class BedwarsPlugin(init: JavaPluginInit) : JavaPlugin(init) {
    lateinit var generatorComponentType: ComponentType<EntityStore?, Generator>
        private set
    lateinit var generatorBuilderComponentType: ComponentType<EntityStore, GeneratorBuilder>
        private set
    lateinit var teamSpawnpointComponentType: ComponentType<EntityStore, TeamSpawnpoint>
        private set
    lateinit var queueSpawnpointComponentType: ComponentType<EntityStore, QueueSpawnpoint>
        private set
    lateinit var preGameCountdownComponentType: ComponentType<EntityStore, PreGameCountdown>
        private set
    lateinit var bedwarsGameHolderResourceType: ResourceType<EntityStore, BedwarsGameHolder>
        private set
    lateinit var teamComponentType: ComponentType<EntityStore, Team>
        private set

    val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    init {
        instance = this
    }

    override fun setup() {
        LOGGER.atInfo().log("Setting up plugin $name")

        this.commandRegistry.registerCommand(BedwarsCommand())

        AssetRegistry.register(
            HytaleAssetStore.builder(
                BedwarsGenerator::class.java,
                DefaultAssetMap()
            )
                .setPath("Bedwars/Generators").setCodec(BedwarsGenerator.CODEC)
                .setKeyFunction(BedwarsGenerator::name)
                .loadsAfter(Item::class.java)
                .build()
        )

        AssetRegistry.register(
            HytaleAssetStore.builder(
                BedwarsTeam::class.java,
                DefaultAssetMap()
            )
                .setPath("Bedwars/Teams").setCodec(BedwarsTeam.CODEC)
                .setKeyFunction(BedwarsTeam::name)
                .build()
        )

        AssetRegistry.register(
            HytaleAssetStore.builder(
                BedwarsMap::class.java,
                DefaultAssetMap()
            )
                .setPath("Bedwars/Maps").setCodec(BedwarsMap.CODEC)
                .setKeyFunction(BedwarsMap::name)
                .build()
        )

        val entityStoreRegistry = this.entityStoreRegistry
        this.generatorComponentType =
            entityStoreRegistry.registerComponent(Generator::class.java) {
                throw UnsupportedOperationException("Generator must be created directly")
            }

        this.generatorBuilderComponentType = entityStoreRegistry.registerComponent(
            GeneratorBuilder::class.java,
            "GeneratorBuilder",
            GeneratorBuilder.CODEC
        )

        this.teamSpawnpointComponentType = entityStoreRegistry.registerComponent(
            TeamSpawnpoint::class.java,
            "TeamSpawnpoint",
            TeamSpawnpoint.CODEC
        )

        this.queueSpawnpointComponentType = entityStoreRegistry.registerComponent(
            QueueSpawnpoint::class.java,
            "QueueSpawnpoint",
            QueueSpawnpoint.CODEC
        )

        this.preGameCountdownComponentType = entityStoreRegistry.registerComponent(PreGameCountdown::class.java) {
            throw UnsupportedOperationException(
                "PreGameCountdown needs to be created manually"
            )
        }

        this.teamComponentType = entityStoreRegistry.registerComponent(Team::class.java) {
            throw UnsupportedOperationException(
                "Team needs to be created manually"
            )
        }

        this.bedwarsGameHolderResourceType = entityStoreRegistry.registerResource(BedwarsGameHolder::class.java, ::BedwarsGameHolder)

        entityStoreRegistry.registerSystem(UpdateGeneratorFromBuilderSystem())
        entityStoreRegistry.registerSystem(GeneratorSystem())
        entityStoreRegistry.registerSystem(PreGameCountdown.TickCountdown())
        entityStoreRegistry.registerSystem(BedwarsGameStartSystem())
        entityStoreRegistry.registerSystem(BlockProtectionSystems.BlockBreakSystem())
        entityStoreRegistry.registerSystem(BlockProtectionSystems.BlockDamageSystem())

        this.getCodecRegistry(Interaction.CODEC)
            .register("SnapToGrid", SnapToGridInteraction::class.java, SnapToGridInteraction.CODEC)

        this.getCodecRegistry(OpenCustomUIInteraction.PAGE_CODEC)
            .register("GeneratorEditor", GeneratorEditorPageSupplier::class.java, GeneratorEditorPageSupplier.CODEC)
        this.getCodecRegistry(OpenCustomUIInteraction.PAGE_CODEC)
            .register("TeamSpawnpointEditor", TeamSpawnpointEditorPageSupplier::class.java, TeamSpawnpointEditorPageSupplier.CODEC)

        ISpawnProvider.CODEC.register("Bedwars", BedwarsGameSpawnProvider::class.java, BedwarsGameSpawnProvider.CODEC)

        this.eventRegistry.registerGlobal(RemoveWorldEvent::class.java, BedwarsMapManager::onWorldRemoveEvent)
    }

    override fun shutdown() {
        scope.cancel("Bedwars plugin shutting down")
    }

    companion object {
        val LOGGER: HytaleLogger = HytaleLogger.forEnclosingClass()
        val RED_COLOR = Color(255, 85, 85)
        private lateinit var instance: BedwarsPlugin

        @JvmStatic
        fun get(): BedwarsPlugin = instance

        fun createGame(
            mapName: String,
            config: BedwarsGameConfig,
            returnTransform: Transform,
            returnWorld: World
        ): CompletableFuture<BedwarsGame> {
            return get().scope.future { BedwarsGame.initialize(mapName, config, returnTransform, returnWorld) }
        }
    }
}