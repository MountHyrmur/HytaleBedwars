package yt.szczurek.hyrmur.bedwars.interaction

import com.google.gson.JsonElement
import com.hypixel.hytale.server.npc.asset.builder.BuilderDescriptorState
import com.hypixel.hytale.server.npc.asset.builder.BuilderSupport
import com.hypixel.hytale.server.npc.asset.builder.InstructionType
import com.hypixel.hytale.server.npc.asset.builder.holder.AssetHolder
import com.hypixel.hytale.server.npc.corecomponents.builders.BuilderActionBase
import com.hypixel.hytale.server.npc.instructions.Action
import java.util.*

class BuilderActionOpenBedwarsShop : BuilderActionBase() {
    private val shopId: AssetHolder = AssetHolder()

    override fun getShortDescription(): String {
        return "Open the bedwars shop"
    }

    override fun getLongDescription(): String {
        return shortDescription
    }

    override fun build(builderSupport: BuilderSupport): Action {
        return ActionOpenBedwarsShop(this, builderSupport)
    }

    override fun getBuilderDescriptorState(): BuilderDescriptorState {
        return BuilderDescriptorState.Stable
    }

    override fun readConfig(data: JsonElement): BuilderActionOpenBedwarsShop {
        this.requireAsset(
            data,
            "Shop",
            this.shopId,
            BedwarsShopExistsValidator.required(),
            BuilderDescriptorState.Stable,
            "The bedwars shop to open",
            null
        )
        this.requireInstructionType(EnumSet.of(InstructionType.Interaction))
        return this
    }

    fun getShopId(support: BuilderSupport): String {
        return this.shopId.get(support.executionContext)
    }
}