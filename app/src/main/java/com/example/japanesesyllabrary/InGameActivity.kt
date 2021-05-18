package com.example.japanesesyllabrary

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*
import java.io.File
import kotlin.random.Random


class InGameActivity : AppCompatActivity() {

    lateinit var scoreView: TextView
    lateinit var comboView: TextView
    var combo: Int = 0
    var score: Int = 0
    lateinit var mode: String
    lateinit var syll: Array<String>
    lateinit var prefix: String
    lateinit var firstB: Button
    lateinit var secondB: Button
    lateinit var thirdB: Button
    lateinit var fourthB: Button
    var RightPos: Int = -1
    lateinit var RightAnsw: String
    var iteration = 0
    lateinit var ImageView: ImageView
    lateinit var dir: File
    lateinit var progressView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context = this
        dir = this.filesDir
        setContentView(R.layout.activity_in_game)
        ImageView = findViewById(R.id.imageView)
        mode = intent.getStringExtra("chosenMode").toString()
        findViewById<TextView>(R.id.chosenMode).text = "Game mode: $mode"
        scoreView = findViewById(R.id.ScoreValue)
        comboView = findViewById(R.id.comboCount)
        scoreView.text = "Score: ${score.toString()}"
        comboView.text = "Combo: ${combo.toString()}"
        progressView = findViewById(R.id.progessView)

        firstB = findViewById(R.id.firstChoise)
        secondB = findViewById(R.id.SecondChoise)
        thirdB = findViewById(R.id.ThirdChoise)
        fourthB = findViewById(R.id.FourthButton)


        //тесты

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(ImageView);
        // начинаем запрос к серверу
        var api = RetrofitClient.getClient(getString(R.string.base_url)).create(GameApi::class.java)
        with(api){
            api.getGame(mode).enqueue(object: Callback<GameResponse>{
                override fun onResponse(
                    call: Call<GameResponse>,
                    response: Response<GameResponse>
                ) {
                    if(response.isSuccessful && mode != "test"){
                        syll = response.body()?.syll!!
                        Log.i("syll","syll size ${syll.size}")
                        progressView.text = "${iteration+1} from ${syll.size}"
                        prefix = response.body()?.prefix!!
                        GenerateQuestion()

                        firstB.setOnClickListener{
                            processScore(firstB)
                        }

                        secondB.setOnClickListener{
                            processScore(secondB)
                        }

                        thirdB.setOnClickListener{
                            processScore(thirdB)
                        }

                        fourthB.setOnClickListener{
                            processScore(fourthB)
                        }

                    }else
                    {
                        if(mode == "test")
                            AlertDialog.Builder(context)
                                .setTitle("Attention!")
                                .setMessage("This is a test mode!")
                                .setPositiveButton("yes") { dialog, which ->
                                    dialog.cancel()
                                    val intent = Intent(context, ChooseMode::class.java)
                                    startActivity(intent);
                                }
                                .show()
                    }
                }

                override fun onFailure(call: Call<GameResponse>, t: Throwable) {
                    Log.i("POST", "get game request failure ${t.message}")
                }

            })
        }
    }

    fun GenerateQuestion(){
        var PathToImage: String = getString(R.string.base_url) + "/$prefix" + "${syll[iteration]}.png"
        //Picasso.get().load(PathToImage).into(ImageView);
        Picasso
            .get()
            .load(PathToImage)
            .resize(600,600)
            //.placeholder(R.drawable.ic_launcher_background)
            .into(ImageView)


        // позиция в массиве словаря
        var RightPos: Int = iteration
        var firstAnsw: Int = RightPos
        var secodnAnsw: Int = RightPos
        var thirdAnsw: Int = RightPos

        // генерируем другие варианты
        while(RightPos == firstAnsw || RightPos == secodnAnsw || RightPos == thirdAnsw ){
            firstAnsw = (syll.indices).random()
            secodnAnsw = (syll.indices).random()
            thirdAnsw = (syll.indices).random()
        }

        var buttonArray: IntArray = intArrayOf(RightPos,firstAnsw,secodnAnsw,thirdAnsw)

        buttonArray.shuffle()

        var i: Int = 0
        for(element in buttonArray){
            when(i){
                0 -> firstB.text = syll[buttonArray[i]]
                1 -> secondB.text = syll[buttonArray[i]]
                2 -> thirdB.text = syll[buttonArray[i]]
                3 -> fourthB.text = syll[buttonArray[i]]
            }
            i++
        }

        var pos: Int = 0
        for(i in buttonArray) {
            if(i == iteration){
                Log.i("answer", "answer ${pos} ${syll[iteration]}")
                RightPos = pos
                RightAnsw = syll[iteration]
            }
            pos++
        }
    }

    fun processScore(button: Button){
        iteration++
        if(iteration <= syll.size-1){
            if(button.text.toString() == RightAnsw) {
                score += 1 + combo
                combo++
                scoreView.text = "Score: $score"
            }else
                combo = 0
            comboView.text = "Combo: $combo"
            progressView.text = "${iteration+1} from ${syll.size}"
            GenerateQuestion()
        }
        else{
            var api =  RetrofitClient.getClient(getString(R.string.base_url)).create(GameApi::class.java)
            val context = this
            with(api){
                var fileName = "data.txt"
                var file = File(dir, fileName)
                val name: String = file.readText().split(" ")[0]
                Log.i("send data", "sendind $name $mode $score")
                api.createRecord(name, mode, score).enqueue(object: Callback<ResponseModel>{
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if(response.isSuccessful){
                            AlertDialog.Builder(context)
                                .setTitle("Congratulations!")
                                .setMessage("Your score has been saved")
                                .setPositiveButton("ok") { dialog, which ->
                                    dialog.cancel()
                                    val intent = Intent(context, ChooseMode::class.java)
                                    startActivity(intent)
                                    finish()
                                }
                                .show()
                        }else{
                            Log.i("POST", "score request fail ${response.body()?.message}")
                        }

                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Log.i("POST", "score request failure ${t.message}")
                    }

                })
            }
        }
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        AlertDialog.Builder(this)
            .setTitle("Warning")
            .setMessage("Are you sure you want to exit?")
            .setPositiveButton("yes") { dialog, which ->
                dialog.cancel()
                val intent = Intent(this, ChooseMode::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No"){dialog, which ->
                dialog.dismiss()
                super.onBackPressed()
            }
            .show()
    }

}

interface GameApi{
    @POST("/api/game/getGame")
    @FormUrlEncoded
    fun getGame(@Field("mode") mode: String?) : Call<GameResponse>
    //fun check(@Field("name") name: String?, @Field("password") password: String?) : Call<ResponseModel>

    @POST("/api/game/record")
    @FormUrlEncoded
    fun createRecord(@Field("name") name: String?, @Field("mode") mode: String?, @Field("record") record: Int?): Call<ResponseModel>
}
