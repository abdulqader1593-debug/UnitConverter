package com.example.unitconverter

import android.R
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET

class UnitsListActivity : ComponentActivity() {
    private val TAG = "UnitsListActivity"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val dimensionApi = RetrofitClientBuilder.create()
        setContent{
            MaterialTheme {
                UnitList(dimensionApi)
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UnitList(
        api: DimensionApi, // Block
        modifier: Modifier = Modifier
    ){
        var dimensions by remember { mutableStateOf<List<DimensionDto>>(emptyList()) }
        var isLoading by remember { mutableStateOf(true) }
        var errorMessage by remember {mutableStateOf<String?>(null)}

        LaunchedEffect(Unit) {
            try {
                isLoading = true
                errorMessage = null
                dimensions = api.getDimensions()
            } catch (e: Exception) {
                Log.e(TAG, "UnitList: Error fetching data", e)
                errorMessage = e.message
            } finally {
                isLoading = false
            }
        }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {Text(text = "Convert", style = MaterialTheme.typography.headlineLarge, color = Color.Black, fontWeight = FontWeight.Bold)}
                )
            }
        ) {innerPadding ->
            LazyVerticalGrid(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                columns = GridCells.Fixed(2),
            ) {
                items(dimensions) { dimension ->
                    DimensionItemCard(dimension = dimension)
                }
            }
        }
    }

    @Composable
    fun DimensionItemCard(dimension: DimensionDto){
        Card(modifier = Modifier.fillMaxWidth()){
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = dimension.label,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = dimension.defaultUnit,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${dimension.supportedUnits.size.toString()} units",
                    style = MaterialTheme.typography.bodyMedium
                )

            }
        }
    }

    class UnitsListViewModel: ViewModel() {
        val api1 by lazy{
            RetrofitClientBuilder.create()
        }
        private val _unitsListState = MutableStateFlow(UnitsListState())
        val unitsListState = _unitsListState.asStateFlow()


        fun fetchList(
        ){
            viewModelScope.launch {
                _unitsListState.update {
                    it.copy(
                        isLoading = true
                    )
                }
                try {
                    val results = api1.getDimensions()
                }
                catch (e: Exception){
                   // Log.d(TAG, "fetchList: $e")
                }
                
            }
            
        }
    }

    data class DimensionDto(
        @SerializedName("label")
        val label:String,
        @SerializedName("defaultUnit")
        val defaultUnit:String,
        @SerializedName("supportedUnits")
        val supportedUnits:List<String>
    )

    interface DimensionApi{
        @GET("v1/5e1bd336-e6bf-47c1-b023-60a34b44097e")
        suspend fun getDimensions(): List<DimensionDto>
    }

    object RetrofitClientBuilder{
        fun create(): DimensionApi{
            return Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl("https://mocki.io/")
                .build()
                .create<DimensionApi>()
        }
    }

}

data class UnitsListState(
    val isLoading: Boolean = false,
    val dimensions: List<UnitsListActivity.DimensionDto> = emptyList(),
    val error: String? = ""
)



sealed interface LoginUiEvents{
    data class NavigateToUnitDetails(val dimension: Dimension): LoginUiEvents
}

/*

{status: 200,
message: null,
data: [
{}....
]}
*
* */