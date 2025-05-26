package com.example.check_the_example

import android.graphics.Color
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import com.example.check_the_example.databinding.ActivityMainBinding
import kotlin.random.Random
import kotlin.math.roundToInt
import kotlin.math.round

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var totalExamples = 0
    private var startTime = 0L
    private var currentAnswer = 0.0
    private var isCorrectAnswer = false

    private val timeRecords = mutableListOf<Double>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupButtons()
    }

    private fun setupButtons() {
        binding.startButton.setOnClickListener {
            generateExample()
            binding.startButton.isEnabled = false
            binding.correctButton.isEnabled = true
            binding.wrongButton.isEnabled = true
            binding.resultText.text = ""
            startTime = SystemClock.elapsedRealtime()
        }

        binding.correctButton.setOnClickListener {
            checkAnswer(true)
            disableAnswerButtons()
        }

        binding.wrongButton.setOnClickListener {
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

        binding.exampleText.text = "$num1 $operator $num2 = $answerText"
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
        val timeSpent = (endTime - startTime) / 1000.0  // Уже Double
        timeRecords.add(timeSpent)  // Теперь сохраняем Double, а не Long

        // Остальной код без изменений
        totalExamples++
        val isCorrect = userChoice == isCorrectAnswer

        if (isCorrect) {
            correctAnswers++
            binding.resultText.text = "ПРАВИЛЬНО"
            binding.resultText.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        } else {
            wrongAnswers++
            binding.resultText.text = "НЕПРАВИЛЬНО"
            binding.resultText.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        }

        updateStatistics(timeSpent)
        binding.startButton.isEnabled = true
    }

    private fun updateStatistics(timeSpent: Double) {
        binding.totalExamples.text = totalExamples.toString()
        binding.correctAnswers.text = correctAnswers.toString()
        binding.wrongAnswers.text = wrongAnswers.toString()

        val percentage = if (totalExamples > 0) {
            (correctAnswers.toDouble() / totalExamples * 100).let {
                "%.2f%%".format(it)
            }
        } else {
            "0.00%"
        }
        binding.percentage.text = percentage

        if (timeRecords.isNotEmpty()) {
            binding.minTime.text = "%.2f".format(timeRecords.min())
            binding.maxTime.text = "%.2f".format(timeRecords.max())
            binding.avgTime.text = "%.2f".format(timeRecords.average())
        }
    }

    private fun disableAnswerButtons() {
        binding.correctButton.isEnabled = false
        binding.wrongButton.isEnabled = false
    }
}