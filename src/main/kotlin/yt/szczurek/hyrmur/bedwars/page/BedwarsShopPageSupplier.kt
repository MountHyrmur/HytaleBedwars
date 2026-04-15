package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.ComponentAccessor
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.protocol.InteractionState
import com.hypixel.hytale.server.core.entity.InteractionContext
import com.hypixel.hytale.server.core.entity.entities.player.pages.CustomUIPage
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.server.OpenCustomUIInteraction.CustomPageSupplier
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import yt.szczurek.hyrmur.bedwars.component.Team


class BedwarsShopPageSupplier : CustomPageSupplier {
    override fun tryCreate(
        ref: Ref<EntityStore>,
        componentAccessor: ComponentAccessor<EntityStore>,
        playerRef: PlayerRef,
        ctx: InteractionContext
    ): CustomUIPage? {

        val teamComponent = componentAccessor.getComponent(ref, Team.componentType)
        if (teamComponent == null) {
            ctx.state.state = InteractionState.Failed
            return null
        }
        // TODO: PARAMETER
        val shop = BedwarsShop.assetMap.getAsset("Shop_Template")!!
        return BedwarsShopPage(playerRef, shop, teamComponent.team)
    }

    companion object {
        val CODEC: BuilderCodec<BedwarsShopPageSupplier?> =
            BuilderCodec.builder(BedwarsShopPageSupplier::class.java) { BedwarsShopPageSupplier() }.build()
    }
}