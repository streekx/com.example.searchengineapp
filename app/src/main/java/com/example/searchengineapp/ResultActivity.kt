package com.example.searchengineapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Supabase API interface placeholder
interface SearchApi {
    @GET("rest/v1/search")
    suspend fun search(@Query("query") query: String): List<SearchResult>
}

data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

class ResultActivity : AppCompatActivity() {

    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        resultView = findViewById(R.id.resultView)

        val query = intent.getStringExtra("query") ?: ""
        if (query.isNotBlank()) {
            fetchResults(query)
        }
    }

    private fun fetchResults(query: String) {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://YOUR_SUPABASE_PROJECT_URL/") // replace with your Supabase REST endpoint
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(SearchApi::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val results = api.search(query)
                withContext(Dispatchers.Main) {
                    if (results.isEmpty()) {
                        resultView.text = "No results found"
                    } else {
                        val display = results.joinToString("\n\n") {
                            "${it.title}\n${it.snippet}\n${it.url}"
                        }
                        resultView.text = display
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    resultView.text = "Error: ${e.message}"
                }
            }
        }
    }
}
