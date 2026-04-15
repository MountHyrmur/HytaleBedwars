package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.component.AddReason
import com.hypixel.hytale.component.Ref
import com.hypixel.hytale.component.Store
import com.hypixel.hytale.server.core.Message
import com.hypixel.hytale.server.core.command.system.CommandContext
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand
import com.hypixel.hytale.server.core.entity.entities.Player
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent
import com.hypixel.hytale.server.core.universe.PlayerRef
import com.hypixel.hytale.server.core.universe.world.World
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore
import com.hypixel.hytale.server.core.util.TargetUtil
import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner
import yt.szczurek.hyrmur.bedwars.asset.BedwarsShop
import yt.szczurek.hyrmur.bedwars.asset.BedwarsTeam
import yt.szczurek.hyrmur.bedwars.component.PreGameCountdown
import yt.szczurek.hyrmur.bedwars.page.BedwarsShopPage

class BedwarsDevCommand : AbstractPlayerCommand("dev", "Command for triggering stuff during development") {
    private val arg: OptionalArg<String> =
        this.withOptionalArg("arg", "Arr", ArgTypes.STRING)
    override fun execute(
        commandContext: CommandContext,
        store: Store<EntityStore>,
        ref: Ref<EntityStore>,
        playerRef: PlayerRef,
        world: World
    ) {
        val player: Player = checkNotNull(store.getComponent(ref, Player.getComponentType()))

        if (arg.provided(commandContext)) {
            val team = BedwarsTeam.assetMap.getAsset("Red")!!
            val shop = BedwarsShop.assetMap.getAsset("Shop_Template")!!
            player.pageManager.openCustomPage(ref, store, BedwarsShopPage(playerRef, shop, team))
//            val team = store.getComponent(ref, Team.componentType)?.team ?: "No team"
//            player.sendMessage(Message.raw("Team $team"))

//            val e = TargetUtil.getTargetEntity(ref, store)
//            if (e != null) {
//                store.removeComponent(e, ModelComponent.getComponentType())
//            }
        } else {
            val holder = EntityStore.REGISTRY.newHolder()
            holder.addComponent(PreGameCountdown.componentType, PreGameCountdown(1))
            store.addEntity(holder, AddReason.SPAWN)
        }

        commandContext.sendMessage(Message.raw("Nothing to see here!"))
    }
}