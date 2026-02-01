package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.server.core.Message
import java.awt.Color

data class ValidationReport(val description: Message, val errors: MutableList<Message> = ArrayList()) {
    constructor(message: String) : this(Message.raw(message))

    fun addTextError(message: String) {
        errors.add(Message.raw(message))
    }

    fun isOk(): Boolean {
        return errors.isEmpty()
    }

    fun toMessage(): Message {
        return if (isOk()) {
            description
        } else {
            description.insert("\nErrors:\n").insertAll(errors.map {
                err -> Message.raw(" • ").insert(err).insert("\n").color(BedwarsPlugin.RED_COLOR)
            })
        }
    }
}
