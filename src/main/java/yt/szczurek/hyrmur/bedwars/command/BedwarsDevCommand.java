package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin;
import yt.szczurek.hyrmur.bedwars.component.running.Generator;
import yt.szczurek.hyrmur.bedwars.data.BedwarsGenerator;
import yt.szczurek.hyrmur.bedwars.ui.GeneratorEditorGui;

import javax.annotation.Nonnull;

public class BedwarsDevCommand extends AbstractPlayerCommand {
    public BedwarsDevCommand() {
        super("dev", "Command for triggering stuff during development");
    }
    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {
        Player player = store.getComponent(ref, Player.getComponentType());
        assert player != null;

        player.sendMessage(Message.raw("Nothing to see here!"));
    }
}