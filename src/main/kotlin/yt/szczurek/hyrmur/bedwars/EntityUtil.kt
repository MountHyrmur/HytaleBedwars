package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Holder
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.math.vector.Vector3d
import com.hypixel.hytale.math.vector.Vector3f
import com.hypixel.hytale.server.core.asset.type.model.config.Model
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset
import com.hypixel.hytale.server.core.entity.UUIDComponent
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.component.AutoNetworkId

object EntityUtil {
    const val SNAP_RESOLUTION: Double = 0.5
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

    fun snapEntityToGrid(entity: Ref<EntityStore?>, accessor: ComponentAccessor<EntityStore?>) {
        val transform = accessor.getComponent(entity, TransformComponent.getComponentType()) ?: return
        val teleport = Teleport.createExact(snappedPosition(transform.position), Vector3f.ZERO, Vector3f.ZERO)
        accessor.addComponent(entity, Teleport.getComponentType(),teleport)
    }

    fun snappedPosition(position: Vector3d): Vector3d {
        return position.clone().scale(1.0/SNAP_RESOLUTION).add(SNAP_RESOLUTION).floor().scale(SNAP_RESOLUTION)
    }
}
