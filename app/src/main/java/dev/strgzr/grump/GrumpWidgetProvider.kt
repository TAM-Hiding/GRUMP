package dev.strgzr.grump

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.RemoteViews
import android.util.SizeF

class GrumpWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { render(context, manager, it) }
    }

    override fun onAppWidgetOptionsChanged(context: Context, manager: AppWidgetManager, id: Int, options: Bundle) {
        render(context, manager, id)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != ACTION_POKE) return
        val id = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID)
        val manager = AppWidgetManager.getInstance(context)
        // Reject deleted/foreign IDs before touching preferences.
        if (manager.getAppWidgetInfo(id)?.provider != ComponentName(context, GrumpWidgetProvider::class.java)) return
        val prefs = GrumpPreferences(context)
        val previous = prefs.read(id)
        val nextIndex = CommentEngine.nextIndex(previous.commentIndex)
        val expression = Expression.forComment(CommentEngine.comments[nextIndex], Expression.entries[previous.expression])
        prefs.write(id, WidgetState(nextIndex, expression.ordinal))
        render(context, manager, id)
    }

    override fun onDeleted(context: Context, ids: IntArray) {
        val prefs = GrumpPreferences(context)
        ids.forEach(prefs::delete)
    }

    override fun onRestored(context: Context, oldIds: IntArray, newIds: IntArray) {
        val prefs = GrumpPreferences(context)
        val restored = oldIds.map(prefs::read)
        oldIds.forEach(prefs::delete)
        newIds.forEachIndexed { index, id -> prefs.write(id, restored[index]) }
        onUpdate(context, AppWidgetManager.getInstance(context), newIds)
    }

    companion object {
        const val ACTION_POKE = "dev.strgzr.grump.POKE"

        internal fun views(context: Context, id: Int, size: SizeF = SizeF(160f, 220f)): RemoteViews {
            val state = GrumpPreferences(context).read(id)
            val expression = Expression.entries[state.expression]
            val comment = CommentEngine.comments.getOrNull(state.commentIndex)?.text ?: context.getString(R.string.idle_comment)
            val poke = Intent(context, GrumpWidgetProvider::class.java).apply {
                action = ACTION_POKE
                data = Uri.parse("grump://widget/$id/poke")
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id)
            }
            val pending = PendingIntent.getBroadcast(context, id, poke, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            return RemoteViews(context.packageName, R.layout.grump_widget).apply {
                setTextViewText(R.id.comment, comment)
                WidgetArtwork.apply(context, this, expression, size)
                setContentDescription(R.id.creature, context.getString(R.string.grump_reaction, expression.description))
                setOnClickPendingIntent(R.id.widget_root, pending)
                setOnClickPendingIntent(R.id.creature, pending)
            }
        }

        private fun render(context: Context, manager: AppWidgetManager, id: Int) {
            val options = manager.getAppWidgetOptions(id)
            val sizes = options.getParcelableArrayList<SizeF>(AppWidgetManager.OPTION_APPWIDGET_SIZES)
            val variants = sizes?.takeIf { it.isNotEmpty() }?.distinct()?.take(16)
                ?: listOf(SizeF(options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 160).toFloat(),
                    options.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 220).toFloat()))
            manager.updateAppWidget(id, RemoteViews(variants.associateWith { views(context, id, it) }))
        }
    }
}
