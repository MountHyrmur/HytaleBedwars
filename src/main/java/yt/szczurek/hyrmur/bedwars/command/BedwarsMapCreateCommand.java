package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.jetbrains.annotations.NotNull;
import yt.szczurek.hyrmur.bedwars.BedwarsMapManager;

import java.util.Optional;

public class BedwarsMapCreateCommand extends BedwarsMapActionCommand {
    public BedwarsMapCreateCommand() {
        super("create");
    }

    @Override
    protected void execute(@NotNull CommandContext ctx, @NotNull Store<EntityStore> store, @NotNull Ref<EntityStore> ref, @NotNull PlayerRef playerRef, @NotNull World world) {
        String mapName = mapNameArg.get(ctx);
        ctx.sendMessage(Message.translation("server.commands.bedwars.map.create.createStart").param("name", mapName));
        Optional<Message> error = BedwarsMapManager.createNewMap(mapName).join();
        if (error.isPresent()) {
            ctx.sendMessage(error.get());
            return;
        }
        BedwarsMapManager.loadMapForEditing(mapName).thenAccept(mapWorld -> {
            mapWorld.execute(() -> BedwarsMapManager.initializeMapWorld(mapWorld));
            ctx.sendMessage(Message.translation("server.commands.bedwars.map.create.createEnd").param("name", mapName));
            world.execute(() -> {
                Transform spawnTransform = mapWorld.getWorldConfig().getSpawnProvider().getSpawnPoint(ref, store);
                Teleport teleportComponent = Teleport.createForPlayer(mapWorld, spawnTransform);
                store.addComponent(ref, Teleport.getComponentType(), teleportComponent);
            });
        });
    }
}
