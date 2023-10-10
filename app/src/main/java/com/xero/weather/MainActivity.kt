package com.xero.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import com.xero.weather.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// 9a4f78615fb11848aac4ef5066ff1dae

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        fetchWeatherData("Bangalore")
        //SearchCity()

        val searchView = findViewById<SearchView>(R.id.searchView)
        // Set an OnQueryTextListener for the SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle the query submission here (e.g., send the query to a search function)
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                // Handle text changes, if needed
                return false
            }
        })
        searchView.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                hideKeyboard(searchView)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
    }
    private fun hideKeyboard(view: SearchView) {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


/*    private fun SearchCity() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })

    }
*/

    private fun fetchWeatherData(cityName: String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)
        val response = retrofit.getWeatherData(cityName, "9a4f78615fb11848aac4ef5066ff1dae", "metric")
        response.enqueue(object : Callback<WeatherApp>{
            @SuppressLint("SetTextI18n")
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if (response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp
                    val minTemp = responseBody.main.temp_min
                    val maxTemp = responseBody.main.temp_max
                    val condition = responseBody.weather.firstOrNull()?.main?: "unknown"
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise
                    val sunSet = responseBody.sys.sunset
                    val seaLevel = responseBody.main.pressure



                    binding.temp.text = "$temperature °C"
                    binding.minTemp.text = "Min: $minTemp °C"
                    binding.maxTemp.text = "Max: $maxTemp °C"
                    binding.condition.text = "$condition"
                    binding.condition1.text = "$condition"
                    binding.textView8.text = dayName(System.currentTimeMillis())
                    binding.textView9.text = date()
                    binding.textView10.text = time(System.currentTimeMillis())
                    binding.location.text = "$cityName"
                    binding.humidity.text = "$humidity %"
                    binding.windSpeed.text = "$windSpeed m/s"
                    //binding.sunRise.text = "$sunRise"
                    //binding.sunSet.text = "$sunSet"
                    binding.seaLevel.text = "$seaLevel hPa"
                    //Log.d("TAG", "onResponse: $temperature")
                    changeImageAccCondition(condition)
                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })

    }

    private fun changeImageAccCondition(conditions: String) {
        when (conditions){
            "Clouds", "Partly Clouds", "Overcast", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.cloudy_bg)
                binding.lottieAnimationView.setAnimation(R.raw.cloud_animation)
            }
            "Overcast", "Haze", "Mist", "Foggy" -> {
                binding.root.setBackgroundResource(R.drawable.mist_bg)
                binding.lottieAnimationView.setAnimation(R.raw.mist_animation)
            }

            "Clear Sky", "Sunny", "Clear" -> {
            binding.root.setBackgroundResource(R.drawable.sunny_bg)
            binding.lottieAnimationView.setAnimation(R.raw.sun_animation)
        }
            "Light Rain", "Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain" -> {
            binding.root.setBackgroundResource(R.drawable.rain_bg)
            binding.lottieAnimationView.setAnimation(R.raw.rain_animation)
        }

            "Light Snow", "Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
            binding.root.setBackgroundResource(R.drawable.snow_bg)
            binding.lottieAnimationView.setAnimation(R.raw.snow_animation)
        }

        else -> {
            binding.root.setBackgroundResource(R.drawable.sunny_bg)
            binding.lottieAnimationView.setAnimation(R.raw.sun_animation)
        }
        }
        binding.lottieAnimationView.playAnimation()
    }

    fun dayName(timestamp: Long): String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((java.util.Date()))
    }
    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((java.util.Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Calendar.getInstance().time))
    }
}