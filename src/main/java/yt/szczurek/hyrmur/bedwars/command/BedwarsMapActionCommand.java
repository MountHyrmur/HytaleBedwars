package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;

public abstract class BedwarsMapActionCommand extends AbstractPlayerCommand {
    protected final RequiredArg<String> mapNameArg = this.withRequiredArg("mapName", "server.commands.bedwars.edit.arg.name", ArgTypes.STRING);

    public BedwarsMapActionCommand(String literal) {
        super(literal, "server.commands.bedwars.map." + literal + ".desc", false);
    }


}
