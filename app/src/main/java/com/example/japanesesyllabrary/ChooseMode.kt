package com.example.japanesesyllabrary

import android.R.string
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.GET


class ChooseMode : AppCompatActivity() {

    lateinit var DiscriptionView: TextView
    lateinit var SpinnerView: Spinner
    lateinit var discrList: Array<String>
    lateinit var modesList: Array<String>
     var chosenMode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_mode)
        SpinnerView = findViewById(R.id.mode_list)
        DiscriptionView = findViewById(R.id.mode_discription)
        Log.i("on create", "$chosenMode")

        if(chosenMode != ""){
            Log.i("LOL", "good code")
            val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item, modesList)
            SpinnerView.adapter = adapter
            DiscriptionView.text = discrList!![0]
            SpinnerView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    DiscriptionView.text = discrList!![id.toInt()]
                    chosenMode = modesList!![id.toInt()]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i("nothing selected", "nothing selected")
                }
            }
        }else{
            val api = RetrofitClient.getClient(getString(R.string.base_url)).create(ModeApi::class.java)
            with(api){
                api.getModes().enqueue(object: Callback<ModesResponse>{
                    override fun onResponse(
                        call: Call<ModesResponse>,
                        response: Response<ModesResponse>
                    ) {
                        if(response.isSuccessful){
                            modesList = response.body()?.modes!!
                            discrList = response.body()?.discriptions!!
                            chosenMode = modesList!![0]
                            val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_dropdown_item, modesList)
                            SpinnerView.adapter = adapter
                            DiscriptionView.text = discrList!![0]
                            SpinnerView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    DiscriptionView.text = discrList!![id.toInt()]
                                    chosenMode = modesList!![id.toInt()]
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.i("nothing selected", "nothing selected")
                                }
                            }

                        }else{
                            Log.i("GET", "get moes request error")
                        }
                    }

                    override fun onFailure(call: Call<ModesResponse>, t: Throwable) {
                        Log.i("GET", "get modes request failure, ${t.message}")
                    }

                })
            }
        }
    }

    fun onPlay(view:View){
        val intent = Intent(this, InGameActivity::class.java).apply{
            putExtra("chosenMode",chosenMode)
        }
        Log.i("send", "send $chosenMode")
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Log.i("saving state", "saving state $chosenMode")
        outState?.run{
            putString("chosenMode", chosenMode)
            putStringArray("modesList",modesList)
            putStringArray("discrList", discrList)
        }

        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {

        chosenMode = savedInstanceState?.getString("chosenMode").toString()
        modesList = savedInstanceState?.getStringArray("modesList")!!
        discrList = savedInstanceState?.getStringArray("discrList")!!
        Log.i("restoring state", "restoring state $chosenMode")
        super.onRestoreInstanceState(savedInstanceState)
    }
}

interface ModeApi{
    @GET("/api/mods/get")
    fun getModes(): Call<ModesResponse>
}

class ChooseModeViewModel: ViewModel(){
    
}
