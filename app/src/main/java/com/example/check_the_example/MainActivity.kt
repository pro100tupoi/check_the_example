package com.example.check_the_example

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import kotlin.math.roundToInt
import kotlin.math.round

class MainActivity : AppCompatActivity() {

    private lateinit var totalExamplesTextView: TextView
    private lateinit var correctAnswersTextView: TextView
    private lateinit var wrongAnswersTextView: TextView
    private lateinit var percentageTextView: TextView
    private lateinit var minTimeTextView: TextView
    private lateinit var maxTimeTextView: TextView
    private lateinit var avgTimeTextView: TextView
    private lateinit var exampleTextView: TextView
    private lateinit var resultTextView: TextView
    private lateinit var startButton: Button
    private lateinit var correctButton: Button
    private lateinit var wrongButton: Button

    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var totalExamples = 0
    private var startTime = 0L
    private var currentAnswer = 0.0
    private var isCorrectAnswer = false

    private val timeRecords = mutableListOf<Long>()
    private val softGreen = Color.parseColor("#C8E6C9")
    private val softRed = Color.parseColor("#FFCDD2")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()
        setupButtons()
    }

    private fun initViews() {
        totalExamplesTextView = findViewById(R.id.totalExamples)
        correctAnswersTextView = findViewById(R.id.correctAnswers)
        wrongAnswersTextView = findViewById(R.id.wrongAnswers)
        percentageTextView = findViewById(R.id.percentage)
        minTimeTextView = findViewById(R.id.minTime)
        maxTimeTextView = findViewById(R.id.maxTime)
        avgTimeTextView = findViewById(R.id.avgTime)
        exampleTextView = findViewById(R.id.exampleText)
        resultTextView = findViewById(R.id.resultText)
        startButton = findViewById(R.id.startButton)
        correctButton = findViewById(R.id.correctButton)
        wrongButton = findViewById(R.id.wrongButton)
    }

    private fun setupButtons() {
        startButton.setOnClickListener {
            generateExample()
            startButton.isEnabled = false
            correctButton.isEnabled = true
            wrongButton.isEnabled = true
            resultTextView.text = ""
            startTime = SystemClock.elapsedRealtime()
        }

        correctButton.setOnClickListener {
            checkAnswer(true)
            disableAnswerButtons()
        }

        wrongButton.setOnClickListener {
            checkAnswer(false)
            disableAnswerButtons()
        }
    }

    private fun generateExample() {
        val num1 = Random.nextInt(10, 100)
        val num2 = Random.nextInt(10, 100)
        val operator = when (Random.nextInt(0, 4)) {
            0 -> "+"
            1 -> "-"
            2 -> "*"
            3 -> "/"
            else -> "+"
        }

        currentAnswer = when (operator) {
            "+" -> (num1 + num2).toDouble()
            "-" -> (num1 - num2).toDouble()
            "*" -> (num1 * num2).toDouble()
            "/" -> if (num2 != 0) num1.toDouble() / num2 else 0.0
            else -> 0.0
        }

        // Округление для деления
        if (operator == "/") {
            currentAnswer = round(currentAnswer * 100) / 100
        }

        // С вероятностью 50% делаем ответ неверным
        isCorrectAnswer = Random.nextBoolean()
        val displayedAnswer = if (isCorrectAnswer) {
            currentAnswer
        } else {
            generateWrongAnswer(currentAnswer, operator)
        }

        // Форматирование вывода
        val answerText = when {
            operator == "/" && currentAnswer != currentAnswer.roundToInt().toDouble() ->
                "%.2f".format(displayedAnswer)
            else -> displayedAnswer.toInt().toString()
        }

        exampleTextView.text = "$num1 $operator $num2 = $answerText"
    }

    private fun generateWrongAnswer(correctAnswer: Double, operator: String): Double {
        return when (operator) {
            "+", "-" -> correctAnswer + Random.nextInt(-5, 6)
            "*", "/" -> correctAnswer * (0.8 + Random.nextDouble() * 0.4)
            else -> correctAnswer + 1
        }.let {
            if (operator == "/") round(it * 100) / 100 else it.roundToInt().toDouble()
        }
    }

    private fun checkAnswer(userChoice: Boolean) {
        val endTime = SystemClock.elapsedRealtime()
        val timeSpent = (endTime - startTime) / 1000.0
        timeRecords.add(timeSpent.roundToInt().toLong())

        totalExamples++
        val isCorrect = userChoice == isCorrectAnswer

        if (isCorrect) {
            correctAnswers++
            resultTextView.text = "ПРАВИЛЬНО"
            resultTextView.setBackgroundColor(softGreen)
        } else {
            wrongAnswers++
            resultTextView.text = "НЕ ПРАВИЛЬНО"
            resultTextView.setBackgroundColor(softRed)
        }

        updateStatistics(timeSpent)
        startButton.isEnabled = true
    }

    private fun updateStatistics(timeSpent: Double) {
        totalExamplesTextView.text = totalExamples.toString()
        correctAnswersTextView.text = correctAnswers.toString()
        wrongAnswersTextView.text = wrongAnswers.toString()

        val percentage = if (totalExamples > 0) {
            (correctAnswers.toDouble() / totalExamples * 100).let {
                "%.2f%%".format(it)
            }
        } else {
            "0.00%"
        }
        percentageTextView.text = percentage

        if (timeRecords.isNotEmpty()) {
            minTimeTextView.text = timeRecords.min().toString()
            maxTimeTextView.text = timeRecords.max().toString()
            avgTimeTextView.text = "%.2f".format(timeRecords.average())
        }
    }

    private fun disableAnswerButtons() {
        correctButton.isEnabled = false
        wrongButton.isEnabled = false
    }
}