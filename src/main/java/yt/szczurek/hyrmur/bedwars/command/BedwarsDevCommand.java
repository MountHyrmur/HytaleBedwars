package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.component.running.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.data.GeneratorConfig;
import yt.szczurek.hyrmur.bedwars.data.GeneratorDropEntry;

import javax.annotation.Nonnull;
import java.util.List;

public class BedwarsDevCommand extends AbstractPlayerCommand {
    public BedwarsDevCommand() {
        super("dev", "Command for triggering stuff during development");
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        Ref<EntityStore> entity = TargetUtil.getTargetEntity(ref, 5.0f, store);
        if (entity == null) {
            entity = ref;
        }

        BedwarsGenerator oldGenerator = store.getComponent(entity, BedwarsGenerator.getComponentType());
        if (oldGenerator == null) {
            GeneratorConfig config = new GeneratorConfig();
            config.id = "Test";
            config.drops = List.of(new GeneratorDropEntry());
            BedwarsGenerator generator = new BedwarsGenerator(config);
            store.addComponent(entity, BedwarsPlugin.get().getBedwarsGeneratorComponentType(), generator);
            player.sendMessage(Message.raw("Added generator component!"));
        } else {
            store.removeComponent(entity, BedwarsPlugin.get().getBedwarsGeneratorComponentType());
            player.sendMessage(Message.raw("Removed generator component!"));
        }

    }
}