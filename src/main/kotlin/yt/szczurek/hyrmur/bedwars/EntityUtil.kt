package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.component.Holder
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
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.AutoNetworkId

object EntityUtil {
    fun createUtilityEntity(pos: Vector3d, modelName: String): Holder<EntityStore> {
        val modelAsset: ModelAsset = checkNotNull(ModelAsset.getAssetMap().getAsset(modelName))
        val model = Model.createScaledModel(modelAsset, 1.0f)

        val holder = EntityStore.REGISTRY.newHolder()

        holder.addComponent(
            TransformComponent.getComponentType(),
            TransformComponent(pos, Vector3f.ZERO)
        )
        holder.addComponent(ModelComponent.getComponentType(), ModelComponent(model))
        holder.addComponent(PersistentModel.getComponentType(), PersistentModel(model.toReference()))
        holder.ensureComponent(AutoNetworkId.componentType)
        holder.ensureComponent(UUIDComponent.getComponentType())
        holder.ensureComponent(PrefabCopyableComponent.getComponentType())
        return holder
    }
}
