package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.math.shape.Box
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.asset.type.model.config.Model
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
import com.hypixel.hytale.server.core.entity.UUIDComponent
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore

object EntityUtil {
    fun createUtilityEntity(
        pos: Vector3d,
        modelName: String,
        store: Store<EntityStore>
    ): Holder<EntityStore> {
        val modelAsset: ModelAsset = checkNotNull(ModelAsset.getAssetMap().getAsset(modelName))
        val model = Model.createScaledModel(modelAsset, 1.0f)

        val holder = EntityStore.REGISTRY.newHolder()

        holder.addComponent(
            TransformComponent.getComponentType(),
            TransformComponent(pos, Vector3f.ZERO)
        )
        holder.addComponent(ModelComponent.getComponentType(), ModelComponent(model))
        holder.addComponent(PersistentModel.getComponentType(), PersistentModel(model.toReference()))

        val boundingBox: Box = checkNotNull(model.boundingBox)
        holder.addComponent(BoundingBox.getComponentType(), BoundingBox(boundingBox))
        holder.addComponent(
            NetworkId.getComponentType(),
            NetworkId(store.getExternalData().takeNextNetworkId())
        )
        holder.ensureComponent(UUIDComponent.getComponentType())
        holder.ensureComponent(PrefabCopyableComponent.getComponentType())
        return holder
    }
}
