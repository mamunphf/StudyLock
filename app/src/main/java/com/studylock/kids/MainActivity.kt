package com.studylock.kids

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.studylock.kids.data.Prefs
import com.studylock.kids.service.AppLockService
import com.studylock.kids.util.PinDialog

/** Parent dashboard — protected by PIN. */
class MainActivity : AppCompatActivity() {

    private lateinit var tvStatus: TextView
    private lateinit var tvLocked: TextView
    private lateinit var etMinutes: EditText
    private lateinit var etQuestions: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvStatus = findViewById(R.id.tvStatus)
        tvLocked = findViewById(R.id.tvLockedCount)
        etMinutes = findViewById(R.id.etMinutes)
        etQuestions = findViewById(R.id.etQuestions)

        etMinutes.setText(Prefs.getUnlockMinutes(this).toString())
        etQuestions.setText(Prefs.getQuestionsRequired(this).toString())

        findViewById<Button>(R.id.btnAccessibility).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
        findViewById<Button>(R.id.btnChooseApps).setOnClickListener {
            startActivity(Intent(this, AppPickerActivity::class.java))
        }
        findViewById<Button>(R.id.btnSave).setOnClickListener { save() }
        findViewById<Button>(R.id.btnChangePin).setOnClickListener { askNewPin(false) }

        requireParent()
    }

    override fun onResume() {
        super.onResume()
        tvStatus.text = getString(if (isServiceOn()) R.string.status_on else R.string.status_off)
        tvLocked.text = getString(R.string.locked_count, Prefs.getLockedApps(this).size)
    }

    private fun requireParent() {
        if (Prefs.getPin(this).isEmpty()) {
            askNewPin(true)
        } else {
            PinDialog.show(this, getString(R.string.enter_pin), false) { pin ->
                if (pin != Prefs.getPin(this)) {
                    Toast.makeText(this, R.string.wrong_pin, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun askNewPin(firstTime: Boolean) {
        PinDialog.show(this, getString(R.string.set_pin), !firstTime) { pin ->
            if (pin.length >= 4) {
                Prefs.setPin(this, pin)
                Toast.makeText(this, R.string.pin_saved, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, R.string.pin_short, Toast.LENGTH_SHORT).show()
                if (firstTime) askNewPin(true)
            }
        }
    }

    private fun save() {
        val m = etMinutes.text.toString().toIntOrNull()?.coerceIn(1, 240) ?: 15
        val q = etQuestions.text.toString().toIntOrNull()?.coerceIn(1, 20) ?: 3
        Prefs.setUnlockMinutes(this, m)
        Prefs.setQuestionsRequired(this, q)
        Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
    }

    private fun isServiceOn(): Boolean {
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.contains("$packageName/${AppLockService::class.java.name}")
    }
}
