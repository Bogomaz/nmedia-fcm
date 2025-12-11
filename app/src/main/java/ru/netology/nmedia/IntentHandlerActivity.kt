package ru.netology.nmedia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.ActivityIntentHandlerBinding
import kotlin.math.log

class IntentHandlerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityIntentHandlerBinding.inflate(layoutInflater)
        setContentView(binding.main)

        setContentView(binding.main)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val action = intent.action
        if(action == Intent.ACTION_SEND){
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if(text.isNullOrEmpty()){
                Snackbar.make(binding.main, R.string.blank_text_error, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok){
                        finish()
                    }
                    .show()
            }else{
                Log.d("IntentHandlerActivity", text)
            }
        }
    }
}