package com.example.searchengineapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val queryInput = findViewById<EditText>(R.id.queryInput)
        val searchBtn = findViewById<Button>(R.id.searchBtn)

        searchBtn.setOnClickListener {
            val query = queryInput.text.toString()
            if (query.isNotBlank()) {
                val intent = Intent(this, ResultActivity::class.java)
                intent.putExtra("query", query)
                startActivity(intent)
            }
        }
    }
}
