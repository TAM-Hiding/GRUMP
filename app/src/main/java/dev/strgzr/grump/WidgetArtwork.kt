package dev.strgzr.grump

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.SizeF
import android.widget.RemoteViews

/** Rendering only. Comment selection and persistent mood IDs do not depend on artwork. */
internal object WidgetArtwork {
    // Canvas head anchors from art/production/full-body/extraction.json. Keeping this
    // adjustment with the pose makes the long forward lean explicit and maintainable.
    private data class Pose(val image: Int, val left: Int, val width: Int,
        val top: Int = 0, val height: Int = 768, val headY: Float = 138f)
    private fun pose(expression: Expression) = when (expression) {
        Expression.IDLE -> Pose(R.drawable.grump_full_idle, 54, 404)
        Expression.ANNOYED -> Pose(R.drawable.grump_full_annoyed, 4, 504)
        Expression.EXASPERATED -> Pose(R.drawable.grump_full_exasperated, 22, 468)
        Expression.SUSPICIOUS -> Pose(R.drawable.grump_full_suspicious, 0, 512, 270, 498, 93f)
        Expression.IMPRESSED -> Pose(R.drawable.grump_full_impressed, 94, 323)
        Expression.UNIMPRESSED -> Pose(R.drawable.grump_full_unimpressed, 25, 462)
    }

    fun apply(context: Context, views: RemoteViews, expression: Expression, size: SizeF) {
        val pose = pose(expression)
        val width = (size.width - 8f).coerceAtLeast(100f)
        val height = (size.height - 8f).coerceAtLeast(120f)
        // Separate silhouette and text regions guarantee no face/ear overlap. The
        // full canvas shares one feet baseline, with no pose-dependent bubble movement.
        val creatureWidth = width * .48f
        val bubbleWidth = width - creatureWidth
        views.setViewLayoutWidth(R.id.creature, creatureWidth, android.util.TypedValue.COMPLEX_UNIT_DIP)
        views.setViewLayoutWidth(R.id.bubble, bubbleWidth, android.util.TypedValue.COMPLEX_UNIT_DIP)
        // Trim only transparent gutters at runtime so narrow upright drawings use
        // their available space. All source pixels and 12px clearance are retained.
        val source = BitmapFactory.decodeResource(context.resources, pose.image)
        val sprite = Bitmap.createBitmap(source, pose.left, pose.top, pose.width, pose.height)
        views.setImageViewBitmap(R.id.creature, sprite)
        if (source !== sprite) source.recycle()
        val scale = minOf(creatureWidth / pose.width, height / pose.height)
        val headY = height - pose.height * scale + pose.headY * scale
        views.setImageViewBitmap(R.id.bubble_art, bubble(context, bubbleWidth, height, headY))
    }

    /** One unioned path, one fill and stroke: the tail has no shared/doubled edge. */
    internal fun bubble(context: Context, width: Float, height: Float, headY: Float): Bitmap {
        // Cap density for predictable RemoteViews memory (under 1 MiB at large sizes).
        val density = minOf(context.resources.displayMetrics.density, 2f, 512f / maxOf(width, height))
        val bitmap = Bitmap.createBitmap((width * density).toInt().coerceAtLeast(1),
            (height * density).toInt().coerceAtLeast(1), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.scale(density, density)
        val body = Path().apply { addRoundRect(RectF(11f, 1f, width - 1f, height - 1f), 14f, 14f, Path.Direction.CW) }
        val y = headY.coerceIn(28f, height - 28f)
        val tail = Path().apply {
            moveTo(12f, y - 10f); lineTo(1f, y + 3f); lineTo(12f, y + 8f); close()
        }
        body.op(tail, Path.Op.UNION)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.rgb(244, 241, 231) }
        canvas.drawPath(body, paint)
        paint.style = Paint.Style.STROKE; paint.strokeWidth = 2f; paint.strokeJoin = Paint.Join.ROUND
        paint.color = Color.rgb(56, 72, 59)
        canvas.drawPath(body, paint)
        return bitmap
    }
}
