package com.example.japanesesyllabrary

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import java.io.File

class MainMenu : AppCompatActivity() {

    var fileName: String = "data.txt"
    lateinit var dir: File
    lateinit var file: File
    lateinit var GreetingView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)
        dir = this.filesDir
        file = File(dir, fileName)
        var useraname: String = file.readText()?.split(" ")[0]
        GreetingView = findViewById(R.id.test2)
        GreetingView.text = "Welcome, $useraname!"

    }

    fun LogOut(view: View){
        file.writeText("")
        val intent = Intent(this, MainActivity::class.java);
        startActivity(intent);
    }

    fun onPlay(view: View){
        val intent = Intent(this, ChooseMode::class.java)
        startActivity(intent)
    }

}