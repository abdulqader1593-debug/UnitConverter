package com.example.unitconverter

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.Text
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unitconverter.databinding.ActivityMainBinding
import java.io.Serializable

data class Counter(
    val value: Int = 0,
    val timeInMillis: Long = System.currentTimeMillis()
): Serializable

//1. ViewModel
//2. SavedInstanceState
//class MainActivity : AppCompatActivity() {
class MainActivity : ComponentActivity() {

    // Configuration Change
    private val TAG = "MainActivity"
    private val KEY_COUNTER = "counter"
    var counter: Counter? = null

    // Explicit Intent, Implicit Intent

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //1. Inflating the Layout
        // 2. Initializing the variables
        // 3. Setting up click listeners
        binding = ActivityMainBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        Log.d(TAG, "onCreate")
        setContent {
            Text("Compose Set")
        }
        // Print Time
      //  setContentView(binding.root)
        // Primt Time
        initializeVariables()
       // setupClicks()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupClicks() {
        binding.button123.setOnClickListener {
//            val newCounter = Counter(
//                value = counter?.value?.plus(1) ?: 0,
//                timeInMillis = System.currentTimeMillis()
//            )
//            counter = newCounter

            // Explicit
            val intent = Intent(this, HomeActivity::class.java)

            val intent2 = Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com"))

//            counter = counter?.copy(
//                value = counter?.value?.plus(1) ?: 0,
//                timeInMillis = System.currentTimeMillis()
//            )
//            binding.textView1.setText("Counter: $counter")
            startActivity(intent2)

        }
    }

    private fun initializeVariables() {
        counter = Counter()
    }

    override fun onStart(){
        super.onStart()
        Log.d(TAG, "onStart: ")
        // Doesnt' require uses attention or input, do that here
    }

    // Early returns
    override fun onResume(){
        super.onResume()
        Log.d(TAG, "onResume: ")

        if( counter == null){
            //binding.textView1.setText("Hello World!")
            return
        }

        if( counter?.value != 0){
            //binding.textView1.setText("Counter: $counter")
        }

    }

    override fun onPause(){
        super.onPause()
        Log.d(TAG, "onPause: ")
    }

    override fun onStop(){
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy(){
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
        // Cleanup
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(KEY_COUNTER, counter)

        Log.d(TAG, "onSaveInstanceState: ")
    }
    

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        counter = savedInstanceState.getSerializable(KEY_COUNTER) as Counter?
        Log.d(TAG, "onRestoreInstanceState: ")
    }

    override fun onRestart(){
        super.onRestart()
        // Activity once in the background comes back to the forground
        Log.d(TAG, "onRestart: ")
    }
    //1. Activity Starting to become visible to the user
    // 2. Activity starting to become invisible
}