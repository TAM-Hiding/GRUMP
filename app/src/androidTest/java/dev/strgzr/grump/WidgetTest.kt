package dev.strgzr.grump

import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.SizeF
import java.io.File
import android.graphics.Color
import android.widget.ImageView
import android.graphics.drawable.BitmapDrawable
import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.res.Configuration
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WidgetTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    @Test fun widgetIsDiscoverableAndTapsKeepInstancesIndependent() {
        val manager = AppWidgetManager.getInstance(context)
        val component = ComponentName(context, GrumpWidgetProvider::class.java)
        assertTrue(manager.installedProviders.any { it.provider == component })
        val host = AppWidgetHost(context, 725)
        val ids = IntArray(2) { host.allocateAppWidgetId() }
        try {
            ids.forEach { assertTrue("CI must grant appwidget bind permission", manager.bindAppWidgetIdIfAllowed(it, component)) }
            val prefs = GrumpPreferences(context)
            ids.forEach(prefs::delete)
            repeat(12) {
                val before = prefs.read(ids[0])
                instrumentation.runOnMainSync {
                    val view = GrumpWidgetProvider.views(context, ids[0]).apply(context, FrameLayout(context))
                    assertTrue(view.findViewById<View>(R.id.creature).performClick())
                }
                val deadline = System.nanoTime() + 3_000_000_000L
                while (prefs.read(ids[0]) == before && System.nanoTime() < deadline) Thread.sleep(20)
                val after = prefs.read(ids[0])
                assertNotEquals(before.expression, after.expression)
                assertEquals(Expression.forComment(CommentEngine.comments[after.commentIndex],
                    Expression.entries[before.expression]).ordinal, after.expression)
                assertTrue(after.commentIndex in CommentEngine.comments.indices)
                assertNotEquals(before.commentIndex, after.commentIndex)
                assertEquals(WidgetState(), prefs.read(ids[1]))
                assertEquals(after, GrumpPreferences(context).read(ids[0]))
            }
            GrumpWidgetProvider().onDeleted(context, intArrayOf(ids[0]))
            assertEquals(WidgetState(), prefs.read(ids[0]))
        } finally {
            ids.forEach { host.deleteAppWidgetId(it); GrumpPreferences(context).delete(it) }
            host.deleteHost()
        }
    }


    @Test fun allSixPngExpressionsRenderWithTransparentPadding() {
        val sprites = intArrayOf(R.drawable.grump_full_idle, R.drawable.grump_full_annoyed,
            R.drawable.grump_full_exasperated, R.drawable.grump_full_suspicious,
            R.drawable.grump_full_impressed, R.drawable.grump_full_unimpressed)
        val id = -726
        val prefs = GrumpPreferences(context)
        try {
            sprites.forEachIndexed { expression, resource ->
                val bitmap = BitmapFactory.decodeResource(context.resources, resource)
                assertNotNull(bitmap)
                assertEquals(512, bitmap.width)
                assertEquals(768, bitmap.height)
                assertTrue(bitmap.hasAlpha())
                for (pixel in 0 until 512) {
                    assertEquals(0, Color.alpha(bitmap.getPixel(pixel, 0)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(pixel, 767)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(0, pixel)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(511, pixel)))
                }
                for (pixel in 0 until 768) {
                    assertEquals(0, Color.alpha(bitmap.getPixel(0, pixel)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(511, pixel)))
                }
                prefs.write(id, WidgetState(0, expression))
                instrumentation.runOnMainSync {
                    val root = GrumpWidgetProvider.views(context, id).apply(context, FrameLayout(context))
                    val shown = (root.findViewById<ImageView>(R.id.creature).drawable as BitmapDrawable).bitmap
                    val crops = arrayOf(intArrayOf(54, 0, 404, 768), intArrayOf(4, 0, 504, 768),
                        intArrayOf(22, 0, 468, 768), intArrayOf(0, 270, 512, 498),
                        intArrayOf(94, 0, 323, 768), intArrayOf(25, 0, 462, 768))
                    val crop = crops[expression]
                    val expected = Bitmap.createBitmap(bitmap, crop[0], crop[1], crop[2], crop[3])
                    assertTrue("Wrong full-body pose for expression $expression", expected.sameAs(shown))
                }
                assertEquals(expression, GrumpPreferences(context).read(id).expression)
            }
            val fullbody = BitmapFactory.decodeResource(context.resources, R.drawable.grump_fullbody)
            assertEquals(512, fullbody.width)
            assertEquals(1024, fullbody.height)
            assertTrue(fullbody.hasAlpha())
        } finally { prefs.delete(id) }
    }

    @Test fun allBubblesFitMinimumSizeAtNormalAndLargeFont() {
        for (scale in listOf(1f, 1.5f, 2f)) {
            val config = Configuration(context.resources.configuration).apply { fontScale = scale }
            val sizedContext = context.createConfigurationContext(config)
            instrumentation.runOnMainSync {
                val density = sizedContext.resources.displayMetrics.density
                val width = (160 * density).toInt()
                val height = (220 * density).toInt()
                val prefs = GrumpPreferences(sizedContext)
                val testId = -725
                try {
                    for (index in CommentEngine.comments.indices) {
                        prefs.write(testId, WidgetState(index, index % Expression.entries.size))
                        val root = GrumpWidgetProvider.views(sizedContext, testId).apply(sizedContext, FrameLayout(sizedContext))
                        root.measure(View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY))
                        root.layout(0, 0, width, height)
                        val bubble = root.findViewById<TextView>(R.id.comment)
                        assertEquals(CommentEngine.comments[index].text, bubble.text.toString())
                        assertTrue("Clipped comment $index at font scale $scale: text=${bubble.textSize}px, " +
                                "layout=${bubble.layout.height}px, available=${bubble.height - bubble.compoundPaddingTop - bubble.compoundPaddingBottom}px",
                            bubble.layout.height <= bubble.height - bubble.compoundPaddingTop - bubble.compoundPaddingBottom)
                        assertEquals(bubble.text.length, bubble.layout.getLineEnd(bubble.layout.lineCount - 1))
                    }
                } finally { prefs.delete(testId) }
            }
        }
    }
    @Test fun sixPosePreviewsAndResizing() {
        val id = -727
        val prefs = GrumpPreferences(context)
        val output = File(context.getExternalFilesDir(null), "widget-previews").apply { mkdirs() }
        try {
            for (size in listOf(SizeF(160f, 220f), SizeF(180f, 280f), SizeF(300f, 220f))) {
                for (expression in Expression.entries) {
                    prefs.write(id, WidgetState(0, expression.ordinal))
                    instrumentation.runOnMainSync {
                        val root = GrumpWidgetProvider.views(context, id, size).apply(context, FrameLayout(context))
                        val d = context.resources.displayMetrics.density
                        val w = (size.width * d).toInt()
                        val h = (size.height * d).toInt()
                        root.measure(View.MeasureSpec.makeMeasureSpec(w, View.MeasureSpec.EXACTLY),
                            View.MeasureSpec.makeMeasureSpec(h, View.MeasureSpec.EXACTLY))
                        root.layout(0, 0, w, h)
                        val creature = root.findViewById<ImageView>(R.id.creature)
                        val bubble = root.findViewById<View>(R.id.bubble)
                        assertTrue("Overlapping regions at $size: creature right=${creature.right}, bubble left=${bubble.left}", creature.right <= bubble.left)
                        val text = root.findViewById<TextView>(R.id.comment)
                        assertTrue(text.layout.height <= text.height - text.compoundPaddingTop - text.compoundPaddingBottom)
                        for ((background, color) in listOf("light" to Color.rgb(232, 228, 216), "dark" to Color.rgb(30, 37, 33))) {
                            val preview = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                            val canvas = Canvas(preview)
                            canvas.drawColor(color)
                            root.draw(canvas)
                            File(output, "${expression.name.lowercase()}-${size.width.toInt()}x${size.height.toInt()}-$background.png")
                                .outputStream().use { preview.compress(Bitmap.CompressFormat.PNG, 100, it) }
                            preview.recycle()
                        }
                    }
                }
            }
        } finally { prefs.delete(id) }
    }

}
