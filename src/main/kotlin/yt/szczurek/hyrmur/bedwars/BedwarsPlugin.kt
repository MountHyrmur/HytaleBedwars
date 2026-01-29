package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.assetstore.AssetRegistry
import com.hypixel.hytale.assetstore.map.DefaultAssetMap
import com.hypixel.hytale.component.ComponentType
import com.hypixel.hytale.logger.HytaleLogger
import com.hypixel.hytale.server.core.asset.HytaleAssetStore
import com.hypixel.hytale.server.core.asset.type.item.config.Item
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction
import com.hypixel.hytale.server.core.plugin.JavaPlugin
import com.hypixel.hytale.server.core.plugin.JavaPluginInit
import com.hypixel.hytale.server.core.universe.world.events.RemoveWorldEvent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.asset.BedwarsMap
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam
import yt.szczurek.hyrmur.bedwars.command.BedwarsCommand
import yt.szczurek.hyrmur.bedwars.component.*
import yt.szczurek.hyrmur.bedwars.interaction.SnapToGridInteraction
import yt.szczurek.hyrmur.bedwars.page.GeneratorEditorPageSupplier
import yt.szczurek.hyrmur.bedwars.page.TeamSpawnpointEditorPageSupplier
import yt.szczurek.hyrmur.bedwars.system.AutoAddNetworkIdSystem
import yt.szczurek.hyrmur.bedwars.system.GeneratorSystem
import yt.szczurek.hyrmur.bedwars.system.UpdateGeneratorFromBuilderSystem


class BedwarsPlugin(init: JavaPluginInit) : JavaPlugin(init) {
    lateinit var generatorComponentType: ComponentType<EntityStore?, Generator>
        private set
    lateinit var generatorBuilderComponent: ComponentType<EntityStore, GeneratorBuilder>
        private set
    lateinit var teamSpawnpointComponent: ComponentType<EntityStore, TeamSpawnpoint>
        private set
    lateinit var queueSpawnpointComponent: ComponentType<EntityStore, QueueSpawnpoint>
        private set
    lateinit var autoNetworkIdComponent: ComponentType<EntityStore, AutoNetworkId>
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

        this.generatorBuilderComponent = entityStoreRegistry.registerComponent(
            GeneratorBuilder::class.java,
            "GeneratorBuilder",
            GeneratorBuilder.CODEC
        )

        this.teamSpawnpointComponent = entityStoreRegistry.registerComponent(
            TeamSpawnpoint::class.java,
            "TeamSpawnpoint",
            TeamSpawnpoint.CODEC
        )

        this.queueSpawnpointComponent = entityStoreRegistry.registerComponent(
            QueueSpawnpoint::class.java,
            "QueueSpawnpoint",
            QueueSpawnpoint.CODEC
        )

        this.autoNetworkIdComponent = entityStoreRegistry.registerComponent(
            AutoNetworkId::class.java,
            "AutoNetworkId",
            AutoNetworkId.CODEC
        )

        entityStoreRegistry.registerSystem(UpdateGeneratorFromBuilderSystem())
        entityStoreRegistry.registerSystem(GeneratorSystem())
        entityStoreRegistry.registerSystem(AutoAddNetworkIdSystem())

        this.getCodecRegistry(Interaction.CODEC)
            .register("SnapToGrid", SnapToGridInteraction::class.java, SnapToGridInteraction.CODEC)

        this.getCodecRegistry(OpenCustomUIInteraction.PAGE_CODEC)
            .register("GeneratorEditor", GeneratorEditorPageSupplier::class.java, GeneratorEditorPageSupplier.CODEC)
        this.getCodecRegistry(OpenCustomUIInteraction.PAGE_CODEC)
            .register("TeamSpawnpointEditor", TeamSpawnpointEditorPageSupplier::class.java, TeamSpawnpointEditorPageSupplier.CODEC)

        this.eventRegistry.registerGlobal(RemoveWorldEvent::class.java, BedwarsMapManager::onWorldRemoveEvent)
    }

    override fun shutdown() {
        scope.cancel("Bedwars plugin shutting down")
    }

    companion object {
        val LOGGER: HytaleLogger = HytaleLogger.forEnclosingClass()
        private lateinit var instance: BedwarsPlugin

        @JvmStatic
        fun get(): BedwarsPlugin = instance
    }
}