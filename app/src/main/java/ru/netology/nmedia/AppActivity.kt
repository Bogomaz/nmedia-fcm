package ru.netology.nmedia

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.databinding.ActivityAppBinding

class AppActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.navController) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val action = intent.action
        if(action == Intent.ACTION_SEND){
            val text = intent.getStringExtra(Intent.EXTRA_TEXT)
            if(text.isNullOrEmpty()){
                Snackbar.make(binding.root, R.string.blank_text_error, Snackbar.LENGTH_SHORT)
                    .setAction(android.R.string.ok){
                        finish()
                    }
                    .show()
            }else{
                Log.d("IntentHandlerActivity", text)
            }
            binding.navController.getFragment<NavHostFragment>().navController.navigate(
                R.id.action_feedFragment_to_editPostFragment,
            )
        }
    }
}