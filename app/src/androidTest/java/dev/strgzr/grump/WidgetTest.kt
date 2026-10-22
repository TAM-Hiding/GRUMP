package dev.strgzr.grump

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
            repeat(9) { iteration ->
                val before = prefs.read(ids[0])
                instrumentation.runOnMainSync {
                    val view = GrumpWidgetProvider.views(context, ids[0]).apply(context, FrameLayout(context))
                    assertTrue(view.findViewById<View>(R.id.creature).performClick())
                }
                val deadline = System.nanoTime() + 3_000_000_000L
                while (prefs.read(ids[0]) == before && System.nanoTime() < deadline) Thread.sleep(20)
                val after = prefs.read(ids[0])
                assertEquals((iteration + 1) % 3, after.expression)
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
                        prefs.write(testId, WidgetState(index, index % 3))
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
