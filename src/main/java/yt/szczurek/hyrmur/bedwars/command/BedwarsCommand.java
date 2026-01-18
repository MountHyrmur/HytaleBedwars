package yt.szczurek.hyrmur.bedwars.command;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class BedwarsCommand extends AbstractCommandCollection {
    public BedwarsCommand() {
        super("bedwars", "server.commands.bedwars.desc");
        this.addAliases("bw");
        this.addSubCommand(new BedwarsMapCommand());
        this.addSubCommand(new BedwarsSpawnCommand());
        this.addSubCommand(new BedwarsDevCommand());
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

    public static class BedwarsSpawnCommand extends AbstractCommandCollection {
        public BedwarsSpawnCommand() {
            super("spawn", "server.commands.bedwars.spawn.desc");
            this.addSubCommand(new BedwarsSpawnGeneratorCommand());
        }
    }
}
