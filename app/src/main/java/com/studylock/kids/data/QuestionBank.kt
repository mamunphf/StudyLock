package com.studylock.kids.data

import kotlin.random.Random

data class Question(val text: String, val options: List<String>, val answer: Int)

/** Random math questions + a fixed general-knowledge list. */
object QuestionBank {

    private val fixed = listOf(
        Question("বাংলাদেশের রাজধানী কোনটি?", listOf("ঢাকা", "চট্টগ্রাম", "খুলনা", "সিলেট"), 0),
        Question("বাংলাদেশের জাতীয় ফুল কোনটি?", listOf("শাপলা", "গোলাপ", "জবা", "বেলি"), 0),
        Question("এক সপ্তাহে কত দিন?", listOf("৭", "৫", "৬", "৮"), 0),
        Question("What is the capital of France?", listOf("Paris", "London", "Rome", "Madrid"), 0),
        Question("Which one is a fruit?", listOf("Mango", "Potato", "Carrot", "Onion"), 0),
        Question("How many legs does a spider have?", listOf("8", "6", "4", "10"), 0),
        Question("The opposite of \"hot\" is…", listOf("Cold", "Warm", "Big", "Fast"), 0),
        Question("Which planet do we live on?", listOf("Earth", "Mars", "Venus", "Jupiter"), 0)
    )

    fun random(): Question = if (Random.nextBoolean()) math() else shuffled(fixed.random())

    private fun shuffled(q: Question): Question {
        val correct = q.options[q.answer]
        val opts = q.options.shuffled()
        return Question(q.text, opts, opts.indexOf(correct))
    }

    private fun math(): Question {
        val a = Random.nextInt(1, 20)
        val b = Random.nextInt(1, 20)
        val (text, ans) = when (Random.nextInt(3)) {
            0 -> "$a + $b = ?" to a + b
            1 -> { val hi = maxOf(a, b); val lo = minOf(a, b); "$hi − $lo = ?" to hi - lo }
            else -> { val x = a % 10 + 1; val y = b % 10 + 1; "$x × $y = ?" to x * y }
        }
        val opts = mutableSetOf(ans)
        while (opts.size < 4) {
            val w = ans + Random.nextInt(-5, 6)
            if (w >= 0) opts.add(w)
        }
        val list = opts.map { it.toString() }.shuffled()
        return Question(text, list, list.indexOf(ans.toString()))
    }
}
