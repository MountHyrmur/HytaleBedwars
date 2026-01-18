package yt.szczurek.hyrmur.bedwars;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class EntityUtil {
    @Nonnull
    public static Holder<EntityStore> createUtilityEntity(@Nonnull Vector3d pos, String modelName, @Nonnull Store<EntityStore> store) {
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(modelName);
        assert modelAsset != null;
        Model model = Model.createScaledModel(modelAsset, 1.0f);

        Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();

        holder.addComponent(TransformComponent.getComponentType(), new TransformComponent(pos, Vector3f.ZERO));
        holder.addComponent(ModelComponent.getComponentType(), new ModelComponent(model));
        Box boundingBox = model.getBoundingBox();
        assert boundingBox != null;
        holder.addComponent(BoundingBox.getComponentType(), new BoundingBox(boundingBox));
        holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
        holder.ensureComponent(UUIDComponent.getComponentType());
        holder.ensureComponent(Intangible.getComponentType());
        return holder;
    }
}
