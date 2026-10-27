package com.studylock.kids.data

import android.content.Context

/** All saved settings in one place (SharedPreferences). */
object Prefs {
    private const val NAME = "study_lock"
    private fun sp(c: Context) = c.getSharedPreferences(NAME, Context.MODE_PRIVATE)

    fun getLockedApps(c: Context): Set<String> =
        sp(c).getStringSet("locked_apps", emptySet())?.toSet() ?: emptySet()
    fun setLockedApps(c: Context, apps: Set<String>) =
        sp(c).edit().putStringSet("locked_apps", HashSet(apps)).apply()

    fun getPin(c: Context): String = sp(c).getString("pin", "") ?: ""
    fun setPin(c: Context, pin: String) = sp(c).edit().putString("pin", pin).apply()

    fun getUnlockMinutes(c: Context): Int = sp(c).getInt("unlock_minutes", 15)
    fun setUnlockMinutes(c: Context, m: Int) = sp(c).edit().putInt("unlock_minutes", m).apply()

    fun getQuestionsRequired(c: Context): Int = sp(c).getInt("questions_required", 3)
    fun setQuestionsRequired(c: Context, n: Int) = sp(c).edit().putInt("questions_required", n).apply()

    /** Locked apps stay open until this time (millis). */
    fun getUnlockUntil(c: Context): Long = sp(c).getLong("unlock_until", 0L)
    fun setUnlockUntil(c: Context, t: Long) = sp(c).edit().putLong("unlock_until", t).apply()
}
