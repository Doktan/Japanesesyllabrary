package com.example.japanesesyllabrary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST


class RecordsTable : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_records_table)

        val myViewMode: RecordsTableViewModel = ViewModelProvider(this).get(RecordsTableViewModel::class.java)
        var ModesList = myViewMode.returnModesList()
        var ModesListView: Spinner = findViewById(R.id.mode_list2)
        var currentMode: String = myViewMode.getMode()
        var Entries: ArrayList<String> = myViewMode.getEntriesForTable()
        var testList: ListView = findViewById(R.id.additionalList)
        var api = RetrofitClient.getClient(getString(R.string.base_url)).create(RecordsApi::class.java)

        if(ModesList.isNullOrEmpty()){
            with(api){
                api.getRecords().enqueue(object : Callback<ResponseModel>{
                    override fun onResponse(
                        call: Call<ResponseModel>,
                        response: Response<ResponseModel>
                    ) {
                        if(response.isSuccessful){
                            var array: Array<String> = response.body()?.data!!
                            for(mode in array){
                                myViewMode.addToList(mode)
                            }
                            ModesList = myViewMode.returnModesList()
                            val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item, ModesList)
                            ModesListView.adapter = adapter
                            ModesListView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                override fun onItemSelected(
                                    parent: AdapterView<*>?,
                                    view: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    myViewMode.addMode(ModesList[id.toInt()])
                                    //тут продолжаем получать список
                                    api.getTable(ModesList[id.toInt()]).enqueue(object: Callback<RecordsResponse>{
                                        override fun onResponse(
                                            call: Call<RecordsResponse>,
                                            response: Response<RecordsResponse>
                                        ) {
                                            if(response.isSuccessful){
                                                Log.i("POST", "get table + ${response.body()?.message}")
                                                var names: Array<String> = response.body()?.names!!
                                                var records: Array<String> = response.body()?.records!!
                                                var forTable: ArrayList<String> = ArrayList()
                                                forTable.add("Name \t Score")
                                                myViewMode.deleteList()
                                                // чекнуть на ошибки
                                                for((i, name) in names.withIndex()){
                                                    forTable.add("$name \t ${records[i]}")
                                                }
                                                for(el in forTable){
                                                    myViewMode.addEntry(el)
                                                }
                                                val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, myViewMode.getEntriesForTable())
                                                testList.adapter = adapter
                                            }
                                            Log.i("POST", "get table fail ${response.body()?.message}")
                                        }

                                        override fun onFailure(
                                            call: Call<RecordsResponse>,
                                            t: Throwable
                                        ) {
                                            Log.i("POST", "error while getting entries for table ${t.message}")
                                        }

                                    })
                                }

                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    Log.i("nothing selected", "nothing selected")
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ResponseModel>, t: Throwable) {
                        Log.i("GET", "get modes error ${t.message}")
                    }

                })
            }
        }else{
            val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext,android.R.layout.simple_spinner_item, ModesList)
            ModesListView.adapter = adapter
            val adapterTable: ArrayAdapter<String> = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, myViewMode.getEntriesForTable())
            testList.adapter = adapterTable
            ModesListView?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    myViewMode.addMode(ModesList[id.toInt()])
                    //тут продолжаем получать список
                    api.getTable(ModesList[id.toInt()]).enqueue(object: Callback<RecordsResponse>{
                        override fun onResponse(
                            call: Call<RecordsResponse>,
                            response: Response<RecordsResponse>
                        ) {
                            if(response.isSuccessful){
                                Log.i("POST", "get table + ${response.body()?.message}")
                                var names: Array<String> = response.body()?.names!!
                                var records: Array<String> = response.body()?.records!!
                                var forTable: ArrayList<String> = ArrayList()
                                forTable.add("Name \t Score")
                                myViewMode.deleteList()
                                // чекнуть на ошибки
                                for((i, name) in names.withIndex()){
                                    forTable.add("$name \t ${records[i]}")
                                }
                                for(el in forTable){
                                    myViewMode.addEntry(el)
                                }
                                val adapter: ArrayAdapter<String> = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, myViewMode.getEntriesForTable())
                                testList.adapter = adapter
                            }
                            Log.i("POST", "get table fail ${response.body()?.message}")
                        }

                        override fun onFailure(
                            call: Call<RecordsResponse>,
                            t: Throwable
                        ) {
                            Log.i("POST", "error while getting entries for table ${t.message}")
                        }

                    })
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    Log.i("nothing selected", "nothing selected")
                }
            }
        }
    }

}

class RecordsTableViewModel: ViewModel(){

    //~~~~~~~~~~~~~~~~~~~~~~~~
    // список режимов
    var modes = ArrayList<String>()

    fun addToList(el: String){
        modes.add(el)
    }

    fun returnModesList(): ArrayList<String>{
        return modes
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~

    //~~~~~~~~~~~~~~~~~~~~~~~~
    // текущий режим
    var currentMode: String = ""

    fun addMode(mode: String){
        currentMode = mode
    }

    fun getMode(): String{
        return currentMode
    }


    //~~~~~~~~~~~~~~~~~~~~~~~~
    //запись в таблице
    var Entries = ArrayList<String>()
    fun addEntry(entry: String){
        Entries.add(entry)
    }
    fun getEntriesForTable(): ArrayList<String>{
        return Entries
    }

    fun deleteList(){
        Entries = ArrayList()
    }
    //~~~~~~~~~~~~~~~~~~~~~~~~

}

interface RecordsApi{
    @GET("/api/records/get")
    fun getRecords() : Call<ResponseModel>

    @POST("/api/records/getRecords")
    @FormUrlEncoded
    fun getTable(@Field("mode") mode: String) : Call<RecordsResponse>
}