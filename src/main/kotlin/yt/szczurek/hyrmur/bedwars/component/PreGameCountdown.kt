package yt.szczurek.hyrmur.bedwars.component

import com.hypixel.hytale.component.*
import com.hypixel.hytale.component.query.Query
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.util.EventTitleUtil
import yt.szczurek.hyrmur.bedwars.BedwarsPlugin
import kotlin.math.roundToInt

const val DEFAULT_COUNTDOWN_SECONDS: Float = 10.0f
val MESSAGE_TIMES_WHITELIST: IntArray = intArrayOf(1, 2, 3, 4, 5, 10, 15, 30, 60, 120, 180, 240, 300)
private val MESSAGE_COUNTDOWN_TITLE = Message.translation("server.bedwars.pregame.countdown.primaryTitle")
private val MESSAGE_COUNTDOWN_SUBTITLE = Message.translation("server.bedwars.pregame.countdown.secondaryTitle")
private val MESSAGE_COUNTDOWN_CHAT = Message.translation("server.bedwars.pregame.countdown.chatMessage")
private val MESSAGE_START_CANCELLED_TITLE = Message.translation("server.bedwars.pregame.startCancelled.primaryTitle")
private val MESSAGE_START_CANCELLED_SUBTITLE = Message.translation("server.bedwars.pregame.startCancelled.secondaryTitle")
private val MESSAGE_START_CANCELLED_CHAT = Message.translation("server.bedwars.pregame.startCancelled.chatMessage")

class PreGameCountdown(val requiredPlayers: Int) : Component<EntityStore> {
    var countdown: Float = DEFAULT_COUNTDOWN_SECONDS

    override fun clone(): PreGameCountdown {
        return PreGameCountdown(requiredPlayers)
    }

    companion object {
        val componentType: ComponentType<EntityStore, PreGameCountdown>
            get() = BedwarsPlugin.get().preGameCountdownComponentType
    }

    class TickCountdown : DelayedEntitySystem<EntityStore>(1.0f) {
        override fun tick(
            dt: Float, i: Int, archetypeChunk: ArchetypeChunk<EntityStore>,
            store: Store<EntityStore>, commandBuffer: CommandBuffer<EntityStore>
        ) {
            val component = archetypeChunk.getComponent(i, componentType)!!
            val world = store.externalData.world
            val remaining: Int
            if (world.playerCount >= component.requiredPlayers) {
                remaining = component.countdown.roundToInt()

                if (remaining in MESSAGE_TIMES_WHITELIST) {
                    sendMessageCountdown(remaining, store, world)
                }

                component.countdown -= dt
            } else {
                remaining = component.countdown.roundToInt()
                if (component.countdown != DEFAULT_COUNTDOWN_SECONDS) {
                    component.countdown = DEFAULT_COUNTDOWN_SECONDS
                    sendMessageCancelled(remaining, store, world)
                }
            }

            if (remaining <= 0) {
                commandBuffer.removeEntity(archetypeChunk.getReferenceTo(i), RemoveReason.REMOVE)
            }
        }

        private fun titleDuration(remaining: Int): Float {
            return if (remaining > 5) 1.0f else 0.6f
        }

        private fun titleFadeDuration(remaining: Int): Float {
            return if (remaining > 5) 0.4f else 0.2f
        }

        private fun sendMessageCountdown(
            remaining: Int,
            store: Store<EntityStore>,
            world: World
        ) {
            sendTitle(MESSAGE_COUNTDOWN_TITLE, MESSAGE_COUNTDOWN_SUBTITLE, remaining, store)
            world.sendMessage(MESSAGE_COUNTDOWN_CHAT.param("remaining", remaining))
        }

        private fun sendMessageCancelled(
            remaining: Int,
            store: Store<EntityStore>,
            world: World
        ) {
            sendTitle(MESSAGE_START_CANCELLED_TITLE, MESSAGE_START_CANCELLED_SUBTITLE, remaining, store)
            world.sendMessage(MESSAGE_START_CANCELLED_CHAT.param("remaining", remaining))
        }

        private fun sendTitle(
            primary: Message,
            secondary: Message,
            remaining: Int,
            store: Store<EntityStore>,
        ) {
            EventTitleUtil.showEventTitleToWorld(
                primary.param("remaining", remaining),
                secondary.param("remaining", remaining),
                true,
                null,
                titleDuration(remaining),
                titleFadeDuration(remaining),
                titleFadeDuration(remaining),
                store
            )
        }

        override fun getQuery(): Query<EntityStore> {
            return componentType
        }
    }
}