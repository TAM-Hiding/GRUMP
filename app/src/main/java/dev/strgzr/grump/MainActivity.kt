package dev.strgzr.grump

import android.app.Activity
import android.os.Bundle
import android.view.WindowInsets
import android.widget.ScrollView
import android.widget.TextView

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val padding = (24 * resources.displayMetrics.density).toInt()
        val instructions = TextView(this).apply {
            text = getString(R.string.instructions)
            textSize = 18f
            setTextColor(0xFFF4F1E7.toInt())
            setPadding(padding, padding, padding, padding)
        }
        val content = ScrollView(this).apply { addView(instructions) }
        content.setOnApplyWindowInsetsListener { view, insets ->
            val bars = insets.getInsets(WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout())
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        setContentView(content)
        content.requestApplyInsets()
    }
}
