package com.example.wordle

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    // Game state
    private lateinit var targetWord: String
    private var guessCount = 0  // 0,1,2 (max 3 guesses)

    // Views
    private lateinit var etGuess: EditText
    private lateinit var btnSubmit: Button
    private lateinit var tvGuess1: TextView
    private lateinit var tvGuess2: TextView
    private lateinit var tvGuess3: TextView
    private lateinit var tvAnswer: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Window inset padding (Android Studio’s default template)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 1) Grab views
        etGuess = findViewById(R.id.etGuess)
        btnSubmit = findViewById(R.id.btnSubmit)
        tvGuess1 = findViewById(R.id.tvGuess1)
        tvGuess2 = findViewById(R.id.tvGuess2)
        tvGuess3 = findViewById(R.id.tvGuess3)
        tvAnswer = findViewById(R.id.tvAnswer)

        // 2) Pick a random 4-letter word
        targetWord = FourLetterWordList.getRandomFourLetterWord()
        // If you want to peek for debugging, uncomment:
        // Toast.makeText(this, "Debug word: $targetWord", Toast.LENGTH_SHORT).show()

        // 3) Button click = read input, validate, check, display
        btnSubmit.setOnClickListener {
            val guess = etGuess.text.toString().trim().uppercase()

            // Validate: must be exactly 4 letters A-Z
            if (!guess.matches(Regex("^[A-Z]{4}$"))) {
                Toast.makeText(this, "Enter exactly 4 letters (A–Z).", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Compute feedback string like "O+XX"
            val feedback = checkGuess(guess, targetWord)

            // Place into the correct TextView
            when (guessCount) {
                0 -> tvGuess1.text = "Guess 1: $guess   ($feedback)"
                1 -> tvGuess2.text = "Guess 2: $guess   ($feedback)"
                2 -> tvGuess3.text = "Guess 3: $guess   ($feedback)"
            }

            // Increment guess count
            guessCount++

            // If guessed correctly OR used all 3 guesses, end round
            if (guess == targetWord || guessCount == 3) {
                tvAnswer.text = "The word was: $targetWord"
                tvAnswer.visibility = View.VISIBLE
                btnSubmit.isEnabled = false
                // (Optional) You could show a Toast:
                // if (guess == targetWord) Toast.makeText(this, "You got it!", Toast.LENGTH_SHORT).show()
            }

            // Clear input + hide keyboard for nicer UX
            etGuess.text.clear()
            hideKeyboard(etGuess)
        }
    }

    // Step 2 Part B helper: returns O + X feedback vs target
    fun checkGuess(guess: String, wordToGuess: String): String {
        val g = guess.uppercase()
        val w = wordToGuess.uppercase()
        val sb = StringBuilder()

        for (i in g.indices) {
            sb.append(
                when {
                    g[i] == w[i] -> 'O'     // right letter, right spot
                    g[i] in w    -> '+'     // right letter, wrong spot
                    else         -> 'X'     // not in the word
                }
            )
        }
        return sb.toString()
    }

    // Small utility to hide the keyboard after submit
    private fun hideKeyboard(view: View) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}
