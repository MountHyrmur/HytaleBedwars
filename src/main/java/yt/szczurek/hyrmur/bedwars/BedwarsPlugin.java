package yt.szczurek.hyrmur.bedwars;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.command.BedwarsCommand;
import yt.szczurek.hyrmur.bedwars.component.running.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.system.GeneratorSystem;

public class BedwarsPlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private static BedwarsPlugin instance;

    private ComponentType<EntityStore, BedwarsGenerator> bedwarsGeneratorComponent;

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

        this.bedwarsGeneratorComponent = this.getEntityStoreRegistry().registerComponent(BedwarsGenerator.class, () -> {
            throw new UnsupportedOperationException("BedwarsGenerator must be created directly");
        });

        this.getEntityStoreRegistry().registerSystem(new GeneratorSystem());
    }

    public ComponentType<EntityStore, BedwarsGenerator> getBedwarsGeneratorComponentType() {
        return bedwarsGeneratorComponent;
    }
}