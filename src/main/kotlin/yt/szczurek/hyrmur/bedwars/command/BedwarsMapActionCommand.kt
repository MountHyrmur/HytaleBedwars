package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand

abstract class BedwarsMapActionCommand(literal: String) :
    AbstractPlayerCommand(literal, "server.commands.bedwars.map.$literal.desc", false) {
    protected val mapNameArg: RequiredArg<String> =
        this.withRequiredArg("mapName", "server.commands.bedwars.map.arg.name", ArgTypes.STRING)
}
