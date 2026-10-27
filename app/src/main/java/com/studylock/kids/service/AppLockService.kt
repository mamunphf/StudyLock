package com.studylock.kids.service

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.studylock.kids.LockActivity
import com.studylock.kids.data.Prefs

/** Fires whenever a new app window opens; shows the quiz if that app is locked. */
class AppLockService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return
        val pkg = event.packageName?.toString() ?: return
        if (pkg == packageName) return
        if (pkg !in Prefs.getLockedApps(this)) return
        if (System.currentTimeMillis() < Prefs.getUnlockUntil(this)) return

        startActivity(Intent(this, LockActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            putExtra(LockActivity.EXTRA_PKG, pkg)
        })
    }

    override fun onInterrupt() {}
}
