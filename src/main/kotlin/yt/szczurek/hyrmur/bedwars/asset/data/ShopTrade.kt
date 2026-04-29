package yt.szczurek.hyrmur.bedwars.asset.data

import com.hypixel.hytale.builtin.adventure.shop.barter.BarterItemStack
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.codec.validation.Validators
import com.hypixel.hytale.server.core.inventory.ItemStack
import com.hypixel.hytale.server.core.inventory.ResourceQuantity

class ShopTrade {
    var input: BarterItemStack? = null
    var output: BarterItemStack? = null

    fun toPriceItemStack(): ItemStack {
        return ItemStack(input!!.itemId, input!!.quantity)
    }

    fun toProductItemStack(): ItemStack {
        return ItemStack(output!!.itemId, output!!.quantity)
    }

    companion object {
        val CODEC: BuilderCodec<ShopTrade> =
            BuilderCodec.builder(ShopTrade::class.java, ::ShopTrade)
                .append(
                    KeyedCodec("Output", BarterItemStack.CODEC),
                    { trade, stack -> trade.output = stack },
                    { trade -> trade.output })
                .addValidator(Validators.nonNull())
                .add()
                .append(
                    KeyedCodec("Input", BarterItemStack.CODEC),
                    { trade, stack -> trade.input = stack },
                    { trade -> trade.input })
                .addValidator(Validators.nonNull())
                .add()
                .build()
    }
}