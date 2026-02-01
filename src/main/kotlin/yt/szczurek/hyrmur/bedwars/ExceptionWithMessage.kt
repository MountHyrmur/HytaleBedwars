package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.server.core.Message

class ExceptionWithMessage(message: Message, cause: Throwable? = null) : Exception(cause) {
    private val theMessage: Message = message

    fun getMessage(): Message {
        return if (cause == null) {
            theMessage
        } else {
            theMessage.insert("\nError: ${cause!!.message}")
        }.color(BedwarsPlugin.RED_COLOR)
    }
}