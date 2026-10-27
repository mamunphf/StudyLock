package com.studylock.kids.util

import android.app.Activity
import android.text.InputType
import android.widget.EditText
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog

object PinDialog {
    fun show(a: Activity, title: String, cancelable: Boolean, onPin: (String) -> Unit) {
        val et = EditText(a).apply {
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_VARIATION_PASSWORD
            hint = "PIN"
        }
        val pad = (20 * a.resources.displayMetrics.density).toInt()
        val box = FrameLayout(a).apply { setPadding(pad, pad / 2, pad, 0); addView(et) }
        val b = AlertDialog.Builder(a)
            .setTitle(title)
            .setView(box)
            .setCancelable(cancelable)
            .setPositiveButton(android.R.string.ok) { _, _ -> onPin(et.text.toString()) }
        if (cancelable) b.setNegativeButton(android.R.string.cancel, null)
        b.show()
    }
}
