package com.example.japanesesyllabrary


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.*
import com.example.japanesesyllabrary.ResponseModel
import org.w3c.dom.Text
import java.io.File


/*        val intent = Intent(this, About_Program::class.java);
        startActivity(intent);*/


class MainActivity : AppCompatActivity() {

    lateinit var PasswordView: EditText
    lateinit var UsernameView: EditText
    lateinit var Username: String
    var isAuth = false
    var fileName: String = "data.txt"
    lateinit var dir: File
    lateinit var file: File
    lateinit var TextOnScreen: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        dir = this.filesDir
        file = File(dir, fileName)
        super.onCreate(savedInstanceState)
        var test:String  = "bruh"
        setContentView(R.layout.activity_main)
        TextOnScreen = findViewById(R.id.test)
        UsernameView = findViewById(R.id.editTextTextPersonName)
        PasswordView = findViewById(R.id.editTextTextPassword)

        if(file.exists()){
            Log.i("FILE","$fileName does exist")
            var fileText: String = file.readText()
            var username: String = ""
            var password: String = ""
            if(fileText != "") {
                username = fileText?.split(" ")[0]
                password = fileText?.split(" ")[1]
                Log.i("read data", "произошло чтение $username and $password")
                val api = RetrofitClient.getClient(getString(R.string.base_url)).create(testAPI::class.java)
                with(api){
                    api.check(username,password).enqueue(object: Callback<ResponseModel>{
                        override fun onResponse(
                            call: Call<ResponseModel>,
                            response: Response<ResponseModel>
                        ) {
                            if(response.isSuccessful){
                                var message: String = response.body()?.message.toString()
                                isAuth = message == "user found!"
                                if(isAuth){
                                    TextOnScreen.text = "Welcome, $username!"
                                    goToMainMenu()
                                    //тут теперь надо убрать окно логина
                                }else{
                                    TextOnScreen.text = ""
                                }
                            }
                        }

                        override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                            Log.i("failure ochka", "message  ${t.message}")
                            TextOnScreen.text = "Server isn't working\nTry again later!"
                        }

                    })
                }
            }
            else{
                Log.i("read data", "text was $username and $password")
                isAuth = false
            }
        }
        else {
            file.createNewFile()
        }
    }

    fun signUp(view: View){
        //val ui = AuthUI(PasswordView,UsernameView, getString(R.string.base_url))
        //ui.signUp(dir)
        Log.i("click","${UsernameView.text} ${PasswordView.text}")

        val api = RetrofitClient.getClient(getString(R.string.base_url)).create(testAPI::class.java)
        val context = this
        with(api){
            api.reg(UsernameView.text.toString(), PasswordView.text.toString()).enqueue(object: Callback<ResponseModel>{
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    val text: String = response.body()?.message.toString()
                    if(text == "Registration is successful"){
                        file.writeText("${UsernameView.text.toString()} ${response.body()?.data!![0]}")
                        isAuth = true
                        TextOnScreen.text = "Welcome, ${UsernameView.text.toString()}"
                    }
                    else{
                        isAuth = false
                    }
                    AlertDialog.Builder(context)
                        .setTitle("")
                        .setMessage("$text")
                        .setPositiveButton("yes") { dialog, which ->
                            dialog.cancel()
                            if(text == "Registration is successful")
                                goToMainMenu()
                        }
                        .show()
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {

                }
            })
        }
    }

    fun signIn(view: View){
        Log.i("click","${UsernameView.text} ${PasswordView.text} and $isAuth")

        val api = RetrofitClient.getClient(getString(R.string.base_url)).create(testAPI::class.java)
        val context: Context = this
        with(api){

            api.sign(UsernameView.text.toString(), PasswordView.text.toString()).enqueue(object: Callback<ResponseModel>{
                override fun onResponse(
                    call: Call<ResponseModel>,
                    response: Response<ResponseModel>
                ) {
                    var text: String = response.body()?.message.toString()
                    if(text == "Authorized!"){
                        var username = UsernameView.text.toString()
                        var hashedPass = response.body()?.data!![0].toString()
                        file.writeText("$username $hashedPass")
                        isAuth = true
                        TextOnScreen.text ="Welcome, ${UsernameView.text.toString()}"
                        UsernameView.text = null
                        PasswordView.text = null
                    }
                    else{
                        UsernameView.text = null
                        PasswordView.text = null
                        isAuth = false
                    }
                    AlertDialog.Builder(context)
                        .setTitle("")
                        .setMessage("$text")
                        .setPositiveButton("yes") { dialog, which ->
                            dialog.cancel()
                            if(text == "Authorized!")
                                goToMainMenu()
                        }
                        .show()
                }

                override fun onFailure(call: Call<ResponseModel>, t: Throwable) {

                }

            })
        }
    }

    fun goToMainMenu() {
        val intent = Intent(this, MainMenu::class.java);
        startActivity(intent);
    }
}



interface testAPI {
    @GET("biba")
    fun test(): Call<ResponseModel>

    @POST("/api/user/register")
    @FormUrlEncoded
    fun reg(@Field("name") name: String?, @Field("password") password: String?) : Call<ResponseModel>

    @POST("/api/user/signIn")
    @FormUrlEncoded
    fun sign(@Field("name") name: String?, @Field("password") password: String?): Call<ResponseModel>

    @POST("/api/user/checkUser")
    @FormUrlEncoded
    fun check(@Field("name") name: String?, @Field("password") password: String?) : Call<ResponseModel>
}



public class Body(
    var name: String? = null,
    var lastName: String? = null
)
