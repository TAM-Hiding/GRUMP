package dev.strgzr.grump

import android.graphics.BitmapFactory
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
        val sprites = intArrayOf(R.drawable.grump_idle, R.drawable.grump_annoyed,
            R.drawable.grump_exasperated, R.drawable.grump_suspicious,
            R.drawable.grump_impressed, R.drawable.grump_unimpressed)
        val id = -726
        val prefs = GrumpPreferences(context)
        try {
            sprites.forEachIndexed { expression, resource ->
                val bitmap = BitmapFactory.decodeResource(context.resources, resource)
                assertNotNull(bitmap)
                assertEquals(512, bitmap.width)
                assertEquals(512, bitmap.height)
                assertTrue(bitmap.hasAlpha())
                for (pixel in 0 until 512) {
                    assertEquals(0, Color.alpha(bitmap.getPixel(pixel, 0)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(pixel, 511)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(0, pixel)))
                    assertEquals(0, Color.alpha(bitmap.getPixel(511, pixel)))
                }
                prefs.write(id, WidgetState(0, expression))
                instrumentation.runOnMainSync {
                    val root = GrumpWidgetProvider.views(context, id).apply(context, FrameLayout(context))
                    val shown = (root.findViewById<ImageView>(R.id.creature).drawable as BitmapDrawable).bitmap
                    assertTrue("Wrong portrait for expression $expression", bitmap.sameAs(shown))
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
                val width = (140 * density).toInt()
                val height = (180 * density).toInt()
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
}
