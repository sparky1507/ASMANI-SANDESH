package com.example.aasmanisandesh

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.SearchView.OnQueryTextListener
import com.example.aasmanisandesh.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Tag
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Agartala")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(
            object  : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query : String?) : Boolean {
                    if (query != null) {
                        fetchWeatherData(query)
                    }
                    return true
                }

                override fun onQueryTextChange(newText : String?) : Boolean {
                    return true
                }


            }
        )

    }
    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(apiinterface::class.java)
        val response =
            retrofit.getweatherData(cityName , "5aff7c61fd2e418aaf1979ad36c4ea93" , "metric")
        response.enqueue(object : Callback<weatherapp> {
            override fun onResponse(call : Call<weatherapp> , response : Response<weatherapp>) {
                val resposebody = response.body()
                if (response.isSuccessful && resposebody != null) {
                    val temperature = resposebody.main.temp.toString()
                    binding.temp.text = "$temperature °c"
                    val humidity = resposebody.main.humidity.toString()
                    binding.humidity.text = "$humidity %"
                    val tempmax = resposebody.main.temp_max.toString()
                    val tempmin = resposebody.main.temp_min.toString()
                    binding.max.text = "Max Temp: $tempmax °c"
                    binding.min.text = "Min Temp: $tempmin °c"
                    val sealevel = resposebody.main.sea_level.toString()
                    binding.sealevel.text = "$sealevel hpa"
                    val sunrise = resposebody.sys.sunrise.toLong()
                    val sunset = resposebody.sys.sunset.toLong()
                    binding.sunrise.text = "${time(sunrise)}"
                    binding.sunset.text = "${time(sunset)}"
                    val windspeed = resposebody.wind.speed.toString()
                    binding.windspeed.text = "$windspeed m/s"
                    val loc = resposebody.name.toString()
                    binding.location.text = cityName
                    val condition = resposebody.weather.firstOrNull()?.main ?: "unknown"
                    binding.condition.text = condition
                    binding.wecondition.text = condition
                    binding.day.text=day()
                    binding.datetext.text=date()
                    changeImage(condition)
                }
            }

            override fun onFailure(call : Call<weatherapp> , t : Throwable) {
                TODO("Not yet implemented")
            }

        })
    }
    private fun changeImage(condition: String){
        when(condition){
            "Partly Clouds","Clouds","Overcast","Mist","Foggy"->{
                binding.root.setBackgroundResource(R.drawable.colud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }
            "Clear Sky","Sunny","Clear"->{
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
            "Light Rain","Drizzle","Moderate Rain","Showers","Heavy Rain"->{
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }
            "Light Snow","Moderate Snow","Heavy Snow","Blizzard"->{
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }
        }
    }


    private fun date():String{
        val sdf=SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format(Date())
    }
    private fun time(timestamp: Long):String{
        val sdf=SimpleDateFormat("HH MM", Locale.getDefault())
        return sdf.format(Date(timestamp*1000))
    }
    private fun day():String{
        val sdf=SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format(Date())
    }
}


