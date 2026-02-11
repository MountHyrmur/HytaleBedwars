package yt.szczurek.hyrmur.bedwars.system

import com.hypixel.hytale.component.CommandBuffer
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.component.system.WorldEventSystem
import com.hypixel.hytale.protocol.SoundCategory
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent
import com.hypixel.hytale.server.core.universe.world.SoundUtil
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import yt.szczurek.hyrmur.bedwars.event.BedwarsGameStartEvent

private const val START_SOUND = "SFX_Toad_Rhino_Alerted"

class BedwarsGameStartSystem() : WorldEventSystem<EntityStore, BedwarsGameStartEvent>(BedwarsGameStartEvent::class.java) {
    override fun handle(
        store: Store<EntityStore>,
        commandBuffer: CommandBuffer<EntityStore>,
        event: BedwarsGameStartEvent
    ) {
        val soundEventIndex = SoundEvent.getAssetMap().getIndex(START_SOUND)
        SoundUtil.playSoundEvent2d(soundEventIndex, SoundCategory.SFX, 1.5f, 1.0f, store)

        
    }
}