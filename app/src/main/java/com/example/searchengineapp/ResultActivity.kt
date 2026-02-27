package com.example.searchengineapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// -------------------- Supabase API Interface --------------------
interface SearchApi {
    @GET("pages") // <- apna crawler table name yaha
    suspend fun search(@Query("query") query: String): List<SearchResult>
}

// -------------------- Data class mapping table columns --------------------
data class SearchResult(
    val title: String,
    val url: String,
    val snippet: String
)

// -------------------- ResultActivity --------------------
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

        // -------------------- Retrofit + OkHttpClient with API Key --------------------
        val retrofit = Retrofit.Builder()
            .baseUrl("https://jhyqyskemsvoizmmupka.supabase.co/") // <- yaha apna Supabase URL dalna
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder()
                    .addInterceptor { chain ->
                        val request = chain.request().newBuilder()
                            .addHeader(
                                "apikey",
                                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpoeXF5c2tlbXN2b2l6bW11cGthIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE4NDQ5ODUsImV4cCI6MjA4NzQyMDk4NX0.IvjAWJZ4DeOCNG0SzKgV5P-LXW2aYvX_RA-NDw5S-ec"
                            ) // <- yaha apna anon key dalna
                            .addHeader(
                                "Authorization",
                                "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImpoeXF5c2tlbXN2b2l6bW11cGthIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzE4NDQ5ODUsImV4cCI6MjA4NzQyMDk4NX0.IvjAWJZ4DeOCNG0SzKgV5P-LXW2aYvX_RA-NDw5S-ec"
                            ) // <- yaha bhi same key
                            .build()
                        chain.proceed(request)
                    }
                    .build()
            )
            .build()

        val api = retrofit.create(SearchApi::class.java)

        // -------------------- Network call in Coroutine --------------------
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
