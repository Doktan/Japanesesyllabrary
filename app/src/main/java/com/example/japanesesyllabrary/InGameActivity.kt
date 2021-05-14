package com.example.japanesesyllabrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView

class InGameActivity : AppCompatActivity() {

    lateinit var scoreView: TextView
    lateinit var comboView: TextView
    var combo: Int = 0
    var score: Int = 0
    lateinit var mode: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in_game)
        mode = intent.getStringExtra("chosenMode").toString()
        findViewById<TextView>(R.id.chosenMode).text = "Game mode: $mode"
        scoreView = findViewById(R.id.ScoreValue)
        comboView = findViewById(R.id.comboCount)
        scoreView.text = "Score: ${score.toString()}"
        comboView.text = "Score: ${combo.toString()}"


    }
}