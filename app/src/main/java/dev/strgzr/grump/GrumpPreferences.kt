package dev.strgzr.grump

import android.content.Context

/** Only widget-local data. Future headline/AI sources should supply text to the same renderer. */
data class WidgetState(val commentIndex: Int = -1, val expression: Int = 0)

class GrumpPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("grump_widgets", Context.MODE_PRIVATE)

    fun read(id: Int) = WidgetState(
        prefs.getInt("${id}_comment", -1),
        prefs.getInt("${id}_expression", 0).coerceIn(0, Expression.entries.lastIndex)
    )

    fun write(id: Int, state: WidgetState) {
        // Synchronous tiny write: the broadcast may be the last work before process death.
        prefs.edit().putInt("${id}_comment", state.commentIndex)
            .putInt("${id}_expression", state.expression).commit()
    }

    fun delete(id: Int) {
        prefs.edit().remove("${id}_comment").remove("${id}_expression").commit()
    }
}
