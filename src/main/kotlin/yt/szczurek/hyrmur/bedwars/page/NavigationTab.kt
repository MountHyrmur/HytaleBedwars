package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder

data class NavigationTab(var id: String? = null, var icon: String? = null, var tooltip: String? = null) {
    companion object {
        val CODEC: BuilderCodec<NavigationTab> = BuilderCodec.builder(NavigationTab::class.java) { NavigationTab() }
            .append(
                KeyedCodec("Id", Codec.STRING),
                { p, id -> p.id = id },
                { p -> p.id })
            .add()
            .append(
                KeyedCodec("Icon", Codec.STRING),
                { p, icon -> p.icon = icon },
                { p -> p.icon })
            .add()
            .append(
                KeyedCodec("TooltipText", Codec.STRING),
                { p, tooltip -> p.tooltip = tooltip },
                { p -> p.tooltip })
            .add()
            .build()
        init {
            // Register the codec if NavigationTab using reflection because the map is private
            val field = UICommandBuilder::class.java.getDeclaredField("CODEC_MAP")
            field.isAccessible = true
            @Suppress("UNCHECKED_CAST") // Reflection
            val codecMap = field.get(null) as MutableMap<Class<*>, Codec<*>>
            codecMap[NavigationTab::class.java] = CODEC
        }
    }
}