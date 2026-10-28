package com.example.unitconverter

import android.content.Intent
import android.graphics.Color
import android.graphics.Color.WHITE
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.example.unitconverter.databinding.ActivityHomeBinding

data class Dimension(
    val label: String, // "Length", "Weight"
    val defaultUnit: String,
    val supportedUnits: List<String>,
    val isItemSelected: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.createStringArrayList() ?: emptyList()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(label)
        dest.writeString(defaultUnit)
        dest.writeStringList(supportedUnits)
    }

    companion object CREATOR : Parcelable.Creator<Dimension> {
        override fun createFromParcel(parcel: Parcel): Dimension {
            return Dimension(parcel)
        }

        override fun newArray(size: Int): Array<Dimension?> {
            return arrayOfNulls(size)
        }
    }
}

fun getSupportedDimensions(): List<Dimension> {
    return listOf(
        Dimension("Length", "m", listOf("m", "km", "mi", "cm", "mm", "in", "ft", "yd")),
        Dimension("Weight", "kg", listOf("kg", "g", "mg", "oz", "lb", "ton", "st")),
        Dimension("Temperature", "\u00B0C", listOf("\u00B0C", "\u00B0F", "K")),
        Dimension("Volume", "L", listOf("L", "ml", "gal", "qt", "pt", "cup", "fl oz")),
        Dimension("Area", "m\u00B2", listOf("m\u00B2", "km\u00B2", "mi\u00B2", "ac", "ha", "ft\u00B2")),
        Dimension("Speed", "km/h", listOf("km/h", "mi/h", "m/s", "kn")),
        Dimension("Time", "s", listOf("s", "min", "h", "day", "week", "month")),
        Dimension("Data", "GB", listOf("GB", "MB", "KB", "TB", "B")),
        Dimension("Currency", "$", listOf("$", "\u20AC", "\u00A3", "\u00A5", "\u20B9", "\u20BF")),
        Dimension("Fuel", "km/L", listOf("km/L", "mi/gal", "L/100km")),
        Dimension("Pressure", "Pa", listOf("Pa", "bar", "psi", "atm", "mmHg")),
        Dimension("Energy", "J", listOf("J", "kJ", "cal", "kcal", "Wh", "kWh"))
    )
}


// Retrofit, Volley, Ktor
// API/Network
// Dimension <---
// Json/Json
// Serialization,de-serialization
// xml, xml
data class Credentials(
    val email: String,
    val password: String,
)
data class Token(
    val autheTken: String,
    val refrTken: String,
)
const val DimensionKey = "DimensionKey"

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MaterialTheme {
                var dimensions by remember { mutableStateOf(getSupportedDimensions()) }

                HomeScreenBinding(
                    dimensions = dimensions,
                    onDimensionClick = { clickedDimension ->
                        dimensions = dimensions.map {
                            if (it.label == clickedDimension.label) {
                                it.copy(isItemSelected = true)
                            } else {
                                it
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun HomeScreenBinding(
    dimensions: List<Dimension>,
    onDimensionClick: (Dimension) -> Unit
) {
    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(dimensions) { dimension ->
                DimensionCard(dimension = dimension, onClick = { onDimensionClick(dimension) })
            }
        }
    }
}

@Composable
fun DimensionCard(
    dimension: Dimension,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = (if (dimension.isItemSelected) Color.RED else MaterialTheme.colorScheme.surface) as androidx.compose.ui.graphics.Color
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = dimension.label, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = dimension.defaultUnit, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "${dimension.supportedUnits.size} units", style = MaterialTheme.typography.bodySmall)
        }
    }
}
