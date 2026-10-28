package com.example.unitconverter

import android.R
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.unitconverter.databinding.ActivityUnitConversionBinding

class UnitConversionActivity : AppCompatActivity() {
    private var dimension: Dimension? = null
    private lateinit var binding: ActivityUnitConversionBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUnitConversionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        dimension = intent.getParcelableExtra<Dimension>(DimensionKey)

        dimension?.let{ dim ->
            val adapter = ArrayAdapter(
                this,
                R.layout.simple_spinner_item,
                dim.supportedUnits
            )
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
            binding.unitToSpinner.adapter = adapter
            binding.unitFromSpinner.adapter = adapter
        }

//        dimension?.let { dim ->
//            val adapter = ArrayAdapter(
//                this,
//                R.layout.simple_spinner_item,
//                dim.supportedUnits
//            )
//            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
//            binding.unitFromSpinner.adapter = adapter
//            binding.unitToSpinner.adapter = adapter
//        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val basePadding = (10 * resources.displayMetrics.density).toInt()
            v.setPadding(
                systemBars.left + basePadding,
                systemBars.top + basePadding,
                systemBars.right + basePadding,
                systemBars.bottom + basePadding
            )
            insets
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tvDetails.setText(dimension?.label)
        binding.tvUnitconversion.setText("1 ${dimension?.defaultUnit} ")
    }
}