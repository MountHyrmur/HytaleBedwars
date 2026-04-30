package yt.szczurek.hyrmur.bedwars.interaction

import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport
import com.hypixel.hytale.server.npc.corecomponents.ActionBase
import com.hypixel.hytale.server.npc.role.Role
import com.hypixel.hytale.server.npc.sensorinfo.InfoProvider
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import yt.szczurek.hyrmur.bedwars.component.Team
import yt.szczurek.hyrmur.bedwars.page.BedwarsShopPage

class ActionOpenBedwarsShop(builder: BuilderActionOpenBedwarsShop, support: BuilderSupport) : ActionBase(builder) {
    private val shopId: String = builder.getShopId(support)

    override fun canExecute(
        ref: Ref<EntityStore>, role: Role, sensorInfo: InfoProvider?, dt: Double, store: Store<EntityStore>
    ): Boolean {
        return super.canExecute(ref, role, sensorInfo, dt, store) && role.getStateSupport()
            .getInteractionIterationTarget() != null
    }

    override fun execute(
        ref: Ref<EntityStore>, role: Role, sensorInfo: InfoProvider?, dt: Double, store: Store<EntityStore>
    ): Boolean {
        super.execute(ref, role, sensorInfo, dt, store)
        val playerRef = role.getStateSupport().getInteractionIterationTarget() ?: return false
        val playerRefComponent = store.getComponent(playerRef, PlayerRef.getComponentType()) ?: return false
        val playerComponent = store.getComponent(playerRef, Player.getComponentType()) ?: return false
        val teamComponent = store.getComponent(playerRef, Team.componentType) ?: return false

        val shop = BedwarsShop.assetMap.getAsset( this.shopId) ?: return false
        playerComponent.pageManager.openCustomPage(ref, store, BedwarsShopPage(playerRefComponent, shop, teamComponent.team))
        return true
    }
}