package yt.szczurek.hyrmur.bedwars

import com.hypixel.hytale.server.core.Message

data class ValidationResult(val reports: List<ValidationReport>) {

    fun isOk(): Boolean {
        return !reports.any { report -> !report.isOk() }
    }

    fun toMessage(): Message {
        val message = Message.raw("Validation report:\n")
        for ((i, report) in reports.withIndex()) {
            message.insert(report.toMessage())
            if (i != reports.size - 1) {
                message.insert("\n")
            }
        }
        return message
    }
}
