package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.entity.ItemUtils
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.inventory.InventoryComponent
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer
import com.hypixel.hytale.server.core.inventory.transaction.ItemStackTransaction
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam
import yt.szczurek.hyrmur.bedwars.asset.data.ShopTrade


class BedwarsShopPage(
    playerRef: PlayerRef,
    private val shop: BedwarsShop,
    private val team: BedwarsTeam,
) : InteractiveCustomUIPage<BedwarsShopPage.BindingData>(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction, BindingData.CODEC) {

    var selectedPageIndex = 0

    class BindingData {
        var tradeIndex: Int? = null
        var pageIndex: Int? = null

        companion object {
            const val TRADE_INDEX_KEY = "TradeIndex"
            const val TAB_INDEX_KEY = "TabIndex"
            val CODEC: BuilderCodec<BindingData> =
                BuilderCodec.builder(BindingData::class.java) { BindingData() }
                    .append(KeyedCodec(TRADE_INDEX_KEY, Codec.STRING),
                        { data, s -> data.tradeIndex = s?.toIntOrNull() },
                        { data -> data.tradeIndex.toString() }
                    ).add()
                    .append(KeyedCodec(TAB_INDEX_KEY, Codec.STRING),
                        { data, s -> data.pageIndex = s?.toIntOrNull() },
                        { data -> data.pageIndex.toString() }
                    ).add()
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

        for (i in shop.pages.indices) {
            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                "#TopTabs[$i]",
                EventData.of(BindingData.TAB_INDEX_KEY, "$i")
            )
        }

        updatePage(ref, store, uiCommandBuilder, uiEventBuilder)
    }

    private fun updatePage(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        uiCommandBuilder: UICommandBuilder,
        uiEventBuilder: UIEventBuilder
    ) {
        val page = shop.pages[selectedPageIndex]
        val inventory = getInventory(ref, store)

        uiCommandBuilder.set("#TitleLabel.Text", page.title)

        uiCommandBuilder.set("#TopTabs.SelectedTab", "Tab${selectedPageIndex}")

        uiCommandBuilder.clear("#ItemGrid")

        for ((i, trade) in page.trades.withIndex()) {
            uiCommandBuilder.append("#ItemGrid", "Pages/ShopEntry.ui")
            val selector = "#ItemGrid[$i]"

            // Price
            uiCommandBuilder.set("$selector #PriceSlot.ItemId", trade.input!!.itemId)
            uiCommandBuilder.set("$selector #PriceCount.Text", "${trade.input!!.quantity}")

            val canBuy = canBuyTrade(trade, inventory)
            if (canBuy) {
                uiCommandBuilder.set("$selector #PriceSlotBorder.Background", "#37b57a")
            }

            // Product
            uiCommandBuilder.set("$selector #ProductSlot.ItemId", trade.output!!.itemId)
            val outputCount = trade.output!!.quantity
            if (outputCount > 1) {
                uiCommandBuilder.set("$selector #ProductCount.Text", "$outputCount")
            }

            uiEventBuilder.addEventBinding(
                CustomUIEventBindingType.Activating,
                selector,
                EventData.of(BindingData.TRADE_INDEX_KEY, "$i")
            )
        }
    }

    fun getInventory(ref: Ref<EntityStore>, store: Store<EntityStore>): CombinedItemContainer {
        return InventoryComponent.getCombined(store, ref, InventoryComponent.Hotbar.getComponentType(), InventoryComponent.Storage.getComponentType())
    }

    fun canBuyTrade(trade: ShopTrade, inventory: CombinedItemContainer): Boolean {
        return inventory.canRemoveItemStack(trade.toPriceItemStack()) && inventory.canAddItemStack(trade.toProductItemStack())
    }

        val trade = shop.pages[selectedPageIndex].trades[tradeIndex]

        val inventory = getInventory(ref, store)
        val canBuy = canBuyTrade(trade, inventory)

        if (!canBuy) {
            return
        }

        val transaction = inventory.removeItemStack(trade.toPriceItemStack())
        if (transaction.succeeded()) {
            inventory.addItemStack(trade.toProductItemStack())
        }
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: BindingData
    ) {
        if (data.tradeIndex != null) {
            tryExecuteTrade(ref, store, data.tradeIndex!!)
            sendUpdate()
        }
        if (data.pageIndex != null) {
            selectedPageIndex = data.pageIndex!!
            val uiBuilder = UICommandBuilder()
            val eventBuilder = UIEventBuilder()
            updatePage(ref, store, uiBuilder, eventBuilder)
            sendUpdate(uiBuilder, eventBuilder, false)
        }
    }
}