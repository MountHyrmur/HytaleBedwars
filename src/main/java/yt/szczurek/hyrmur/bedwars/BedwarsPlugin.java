package yt.szczurek.hyrmur.bedwars;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.command.BedwarsCommand;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;
import yt.szczurek.hyrmur.bedwars.component.running.Generator;
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.system.GeneratorSystem;

public class BedwarsPlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static BedwarsPlugin instance;

    private ComponentType<EntityStore, Generator> generatorComponent;
    private ComponentType<EntityStore, GeneratorBuilder> generatorBuilderComponent;

    public BedwarsPlugin(@NotNull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    public static BedwarsPlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Setting up plugin " + this.getName());

        this.getCommandRegistry().registerCommand(new BedwarsCommand());

        AssetRegistry.register(
                HytaleAssetStore.builder(BedwarsGenerator.class, new DefaultAssetMap<>())
                        .setPath("Bedwars/Generators").setCodec(BedwarsGenerator.CODEC)
                        .setKeyFunction(BedwarsGenerator::getId)
                        .loadsAfter(Item.class)
                        .build()
        );

        this.generatorComponent = this.getEntityStoreRegistry().registerComponent(Generator.class, () -> {
            throw new UnsupportedOperationException("Generator must be created directly");
        });

        this.generatorBuilderComponent = this.getEntityStoreRegistry().registerComponent(GeneratorBuilder.class, () -> {
            throw new UnsupportedOperationException("GeneratorBuilder must be created directly");
        });

        this.getEntityStoreRegistry().registerSystem(new GeneratorSystem());
    }

    public ComponentType<EntityStore, Generator> getGeneratorComponentType() {
        return generatorComponent;
    }

    public ComponentType<EntityStore, GeneratorBuilder> getGeneratorBuilderComponent() {
        return generatorBuilderComponent;
    }
}