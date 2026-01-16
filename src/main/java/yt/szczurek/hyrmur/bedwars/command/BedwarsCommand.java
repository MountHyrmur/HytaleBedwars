package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class BedwarsCommand extends AbstractCommandCollection {
    public BedwarsCommand() {
        super("bedwars", "server.commands.bedwars.desc");
        this.addAliases("bw");
        this.addSubCommand(new BedwarsMapCommand());
    }

    public static class BedwarsMapCommand extends AbstractCommandCollection {
        public BedwarsMapCommand() {
            super("map", "server.commands.bedwars.map.desc");
            this.addSubCommand(new BedwarsMapCreateCommand());
            this.addSubCommand(new BedwarsMapEditCommand());
            this.addSubCommand(new BedwarsMapDeleteCommand());
            this.addSubCommand(new BedwarsMapListCommand());
        }
    }
}
