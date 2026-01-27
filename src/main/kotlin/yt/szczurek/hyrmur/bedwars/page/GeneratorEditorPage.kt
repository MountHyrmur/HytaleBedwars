package yt.szczurek.hyrmur.bedwars.page

import com.hypixel.hytale.codec.Codec
import com.hypixel.hytale.codec.KeyedCodec
import com.hypixel.hytale.codec.builder.BuilderCodec
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage
import com.hypixel.hytale.server.core.ui.builder.EventData
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.asset.BedwarsGenerator
import yt.szczurek.hyrmur.bedwars.component.Generator
import yt.szczurek.hyrmur.bedwars.component.GeneratorBuilder

class GeneratorEditorPage(
    playerRef: PlayerRef,
    private val generator: Ref<EntityStore>,
    lifetime: CustomPageLifetime
) : InteractiveCustomUIPage<GeneratorEditorPage.BindingData>(playerRef, lifetime, BindingData.CODEC) {
    private var generatorName: String? = null

    class BindingData {
        var generatorName: String? = null
        var action: String? = null

        companion object {
            val CODEC: BuilderCodec<BindingData> =
                BuilderCodec.builder(BindingData::class.java) { BindingData() }
                    .append(
                        KeyedCodec("@GeneratorName", Codec.STRING),
                        { data, s -> data.generatorName = s },
                        { data -> data.generatorName })
                    .add()
                    .append(
                        KeyedCodec("Action", Codec.STRING),
                        { data, s -> data.action = s },
                        { data -> data.action })
                    .add()
                    .build()
        }
    }

    override fun build(ref: Ref<EntityStore>, uiCommandBuilder: UICommandBuilder, uiEventBuilder: UIEventBuilder, store: Store<EntityStore>) {
        uiCommandBuilder.append("Pages/GeneratorEditorPage.ui")

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.ValueChanged,
            "#GeneratorName",
            EventData.of("@GeneratorName", "#GeneratorName.Value"),
            false
        )

        val generatorBuilder: GeneratorBuilder =
            checkNotNull(store.getComponent(generator, GeneratorBuilder.Companion.componentType))
        generatorName = generatorBuilder.generatorName
        uiCommandBuilder.set("#GeneratorName.Value", generatorName!!)

        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#ActivateGenerator",
            EventData().append("Action", "ActivateGenerator"),
            false
        )
        uiEventBuilder.addEventBinding(
            CustomUIEventBindingType.Activating,
            "#DeactivateGenerator",
            EventData().append("Action", "DeactivateGenerator"),
            false
        )
    }

    override fun handleDataEvent(
        ref: Ref<EntityStore>,
        store: Store<EntityStore>,
        data: BindingData
    ) {
        super.handleDataEvent(ref, store, data)

        if (data.generatorName != null) {
            val newName = data.generatorName!!.trim { it <= ' ' }
            if (newName != generatorName) {
                generatorName = newName
                onGeneratorNameChange(store)
            }
        }

        if (data.action != null) {
            handleAction(data.action!!, store)
        }
    }

    private fun onGeneratorNameChange(store: Store<EntityStore>) {
        // Return on invalid name
        generatorConfig ?: return
        store.putComponent(
            generator,
            GeneratorBuilder.Companion.componentType,
            GeneratorBuilder(generatorName!!)
        )
    }

    private fun handleAction(action: String, store: Store<EntityStore>) {
        when (action) {
            "ActivateGenerator" -> {
                val component = store.getComponent(generator, Generator.Companion.componentType)
                if (component != null) {
                    close()
                    return
                }

                val config = this.generatorConfig
                if (config == null) {
                    playerRef.sendMessage(Message.translation("server.customUI.generatorEditor.error.invalidGeneratorName"))
                    close()
                    return
                }

                store.addComponent(generator, Generator.Companion.componentType, Generator(config))
                close()
            }
            "DeactivateGenerator" -> {
                store.removeComponentIfExists(generator, Generator.Companion.componentType)
                return
            }
        }
    }

    private val generatorConfig: BedwarsGenerator?
        get() = BedwarsGenerator.Companion.assetMap.getAsset(generatorName)
}