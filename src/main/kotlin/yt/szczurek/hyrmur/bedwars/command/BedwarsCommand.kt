package yt.szczurek.hyrmur.bedwars.command

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection

class BedwarsCommand : AbstractCommandCollection("bedwars", "server.commands.bedwars.desc") {
    init {
        this.addAliases("bw")
        this.addSubCommand(BedwarsMapCommand())
        this.addSubCommand(BedwarsSpawnCommand())
        this.addSubCommand(BedwarsDevCommand())
        this.addSubCommand(BedwarsStartgameCommand())
    }

    class BedwarsMapCommand : AbstractCommandCollection("map", "server.commands.bedwars.map.desc") {
        init {
            this.addSubCommand(BedwarsMapCreateCommand())
            this.addSubCommand(BedwarsMapEditCommand())
            this.addSubCommand(BedwarsMapDeleteCommand())
            this.addSubCommand(BedwarsMapListCommand())
            this.addSubCommand(BedwarsMapExitCommand())
            this.addSubCommand(BedwarsMapValidateCommand())
        }
    }

    class BedwarsSpawnCommand : AbstractCommandCollection("spawn", "server.commands.bedwars.spawn.desc") {
        init {
            this.addSubCommand(BedwarsSpawnGeneratorCommand())
            this.addSubCommand(BedwarsSpawnTeamSpawnCommand())
            this.addSubCommand(BedwarsSpawnQueueSpawnCommand())
        }
    }
}
