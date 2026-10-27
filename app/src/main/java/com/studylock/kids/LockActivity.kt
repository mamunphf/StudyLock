package com.studylock.kids

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import com.studylock.kids.data.Prefs
import com.studylock.kids.data.Question
import com.studylock.kids.data.QuestionBank
import com.studylock.kids.util.PinDialog

/** Quiz screen: answer N questions correctly to unlock for X minutes. */
class LockActivity : AppCompatActivity() {

    private var targetPkg: String? = null
    private var correct = 0
    private var required = 3
    private lateinit var current: Question

    private lateinit var tvProgress: TextView
    private lateinit var tvQuestion: TextView
    private lateinit var tvFeedback: TextView
    private lateinit var buttons: List<Button>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lock)

        tvProgress = findViewById(R.id.tvProgress)
        tvQuestion = findViewById(R.id.tvQuestion)
        tvFeedback = findViewById(R.id.tvFeedback)
        buttons = listOf(R.id.opt1, R.id.opt2, R.id.opt3, R.id.opt4).map { findViewById(it) }
        buttons.forEachIndexed { i, b -> b.setOnClickListener { answer(i) } }

        findViewById<Button>(R.id.btnParent).setOnClickListener {
            PinDialog.show(this, getString(R.string.enter_pin), true) { pin ->
                if (pin == Prefs.getPin(this)) unlock()
                else Toast.makeText(this, R.string.wrong_pin, Toast.LENGTH_SHORT).show()
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() = goHome()
        })

        start(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        start(intent)
    }

    private fun start(i: Intent?) {
        targetPkg = i?.getStringExtra(EXTRA_PKG)
        required = Prefs.getQuestionsRequired(this)
        correct = 0
        tvFeedback.text = ""
        nextQuestion()
    }

    private fun nextQuestion() {
        current = QuestionBank.random()
        tvQuestion.text = current.text
        buttons.forEachIndexed { i, b -> b.text = current.options[i] }
        tvProgress.text = getString(R.string.progress, correct, required)
    }

    private fun answer(i: Int) {
        if (i == current.answer) {
            correct++
            tvFeedback.text = getString(R.string.correct)
            if (correct >= required) unlock() else nextQuestion()
        } else {
            tvFeedback.text = getString(R.string.wrong)
            nextQuestion()
        }
    }

    private fun unlock() {
        val minutes = Prefs.getUnlockMinutes(this)
        Prefs.setUnlockUntil(this, System.currentTimeMillis() + minutes * 60_000L)
        Toast.makeText(this, getString(R.string.unlocked, minutes), Toast.LENGTH_SHORT).show()
        targetPkg?.let { pkg ->
            packageManager.getLaunchIntentForPackage(pkg)?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(it)
            }
        }
        finish()
    }

    private fun goHome() {
        startActivity(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    companion object { const val EXTRA_PKG = "pkg" }
}
