package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import yt.szczurek.hyrmur.bedwars.component.data.GeneratorBuilder;

import javax.annotation.Nonnull;

public class BedwarsSpawnGeneratorCommand extends AbstractPlayerCommand  {
    public BedwarsSpawnGeneratorCommand() {
        super("generator", "server.commands.bedwars.spawn.generator.desc");
    }

    @Override
    protected void execute(@Nonnull CommandContext commandContext, @Nonnull Store<EntityStore> store,
                           @Nonnull Ref<EntityStore> ref, @Nonnull PlayerRef playerRef, @Nonnull World world) {

        TransformComponent playerTransform = store.getComponent(ref, EntityModule.get().getTransformComponentType());
        assert playerTransform != null;
        Vector3d pos = playerTransform.getPosition().add(0.0d, 1.0d, 0.0d);
        Holder<EntityStore> generator = GeneratorBuilder.createGeneratorBuilderEntity(pos, store);

        world.execute(() -> store.addEntity(generator, AddReason.SPAWN));
    }


}
