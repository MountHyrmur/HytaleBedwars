package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam

class BedwarsShopPage(
    playerRef: PlayerRef,
    private val shop: BedwarsShop,
    private val team: BedwarsTeam,
) : InteractiveCustomUIPage<BedwarsShopPage.BindingData>(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC) {

    class BindingData {
        companion object {
            val CODEC: BuilderCodec<BindingData> =
                BuilderCodec.builder(BindingData::class.java) { BindingData() }
                    .build()
        }
    }

    override fun build(ref: Ref<EntityStore>, uiCommandBuilder: UICommandBuilder, uiEventBuilder: UIEventBuilder, store: Store<EntityStore>) {
        uiCommandBuilder.append("Pages/BedwarsShopPage.ui")

        // It seems it is not possible to use item icons in ui
        // So for now we use a placeholder icon
        val icon = "Common/RecipesIcon.png"
        val tabs = shop.pages.withIndex().map { (i, page) -> NavigationTab("Tab$i", icon, page.title) }.toList()
        uiCommandBuilder.set("#TopTabs.Tabs", tabs)
        uiCommandBuilder.set("#TopTabs.SelectedTab", "Tab0")
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: BindingData
    ) {
        super.handleDataEvent(ref, store, data)
    }
}