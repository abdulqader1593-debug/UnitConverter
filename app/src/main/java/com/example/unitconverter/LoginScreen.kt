package com.example.unitconverter

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.viewModelFactory
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import kotlin.getValue

// OOPs
// 1. Encapsulation
// 2. Inherticance
// Procedural/ functional . OOP

//1
data class LoginScreenState(
    val email: String = "",
    val password: String = "",
    val loading: Boolean = false,
    val loginError: String? = null,
    val isChecked:Boolean = false,
)

sealed interface LoginUiAction{

    object LoginClick: LoginUiAction

    data class EnterEmail(
        val value: String
    ): LoginUiAction

    data class EnterPassword(
        val value: String
    ): LoginUiAction

    data class CheckBox(
        val value: Boolean
    ): LoginUiAction

}

sealed interface LoginUiEvent {

    object LoginSuccessful: LoginUiEvent

    data class LoginUnSuccessful(
        val message: String
    ): LoginUiEvent
}



// User -> Action -> Viewmodel -> Updates State -> observe -> Updates UI
class LoginViewModel(
    private val api: LoginApi
): ViewModel(){

    private val TAG = "LoginScreen"
   // private val api = RetrofitClientBuilder.create()
//    private val api by lazy {
//        RetrofitClientBuilder.create()
//    }
    //1
    private val _state = MutableStateFlow(LoginScreenState())
    val state get() = _state.asStateFlow()



    // Single Shot Events
    private val _events = Channel<LoginUiEvent>()
    val events get() = _events.receiveAsFlow()

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }

    //2
    // liveData
//    private val _email: MutableStateFlow<String> = MutableStateFlow("")
//    val email: StateFlow<String> = _email.asStateFlow()
//    private val _password: MutableStateFlow<String> = MutableStateFlow("")
//    val password get() = _password.asStateFlow()
//    private val _loading: MutableStateFlow<Boolean> = MutableStateFlow(false)
//    val loading get() = _loading.asStateFlow()

    fun onUiAction(action: LoginUiAction){
        when(action){
            is LoginUiAction.EnterEmail -> {
                onEmailChange(action.value)
            }
            is LoginUiAction.EnterPassword -> {
                val pass = action.value
                onPasswordChange(pass)
            }
            is LoginUiAction.LoginClick -> {
                login()
            }

            is LoginUiAction.CheckBox -> {
                onCheckBoxSelected(action.value)
            }
        }
    }

    private fun onEmailChange(value: String){
        //_email.value = value
//        _state.value = LoginScreenState(
//            email = value,
//            password = _state.value.password,
//            loading = _state.value.loading
//        )
//        _state.value = _state.value.copy(
//            email = value
//        )

        _state.update {
            it.copy(
                email = value
            )
        }

    }

    private fun onCheckBoxSelected(value:Boolean){
        _state.update {
            it.copy(
                isChecked = value
            )
        }
        Log.d(TAG, "onCheckBoxSelected: $value")
    }

    private fun onPasswordChange(value: String){
        _state.update {
            it.copy(
                password = value
            )
        }
    }

    private fun login(){
        val credentials = LoginScreen.Credentials(
            _state.value.email,
            _state.value.password
        )
        viewModelScope.launch {
            _state.update {
                it.copy(
                    loading = true
                )
            }
            try {
                val result = api.login(credentials)
                result.authToken?.let {
                    _events.trySend(LoginUiEvent.LoginSuccessful)
                } ?: {
                    _events.trySend(LoginUiEvent.LoginUnSuccessful("Auth token is null"))
                }
            } catch (e: Exception) {
                Log.d("TAG", "login: $e")
                _events.trySend(LoginUiEvent.LoginUnSuccessful("Error logging in : ${e.message}"))
                _state.update {
                    it.copy(
                        loginError = "Error Logging in."
                    )
                }
            } finally {
                _state.update {
                    it.copy(
                        loading = false
                    )
                }
            }
        }
    }

    class Factory(
        val api: LoginApi
    ): ViewModelProvider.Factory{

        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            return LoginViewModel(
                api = api
            ) as? T ?: error("")
        }
    }

}
// MVVM + MVI
class LoginScreen : ComponentActivity() {
    private val tag = "LoginScreen"
    val loginViewModelFactor = LoginViewModel.Factory(RetrofitClientBuilder.create())
    private val loginViewModel by viewModels<LoginViewModel>(
        factoryProducer = {
            loginViewModelFactor
        }
    )
  //  private lateinit var loginViewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
       // val loginAPi = RetrofitClientBuilder.create()
       // val loginViewModel = LoginViewModel()
      //  val loginViewModel by viewModels<LoginViewModel>()
//        loginViewModel = LoginViewModel(
//            api = RetrofitClientBuilder.create()
//        )
    ///    loginViewModel = ViewModelProvider.create(this,loginViewModelFactor)[LoginViewModel::class.java]
        //Toast.makeText(this, "ahca", Toast.LENGTH_SHORT).show()
        setContent {
            MaterialTheme {
//                 val email by loginViewModel.email.collectAsState()
//                 val password by loginViewModel.password.collectAsState()
//                 val loading by loginViewModel.loading.collectAsState()
                val state by loginViewModel.state.collectAsState()
//                LaunchedEffect(Unit) {
//                    Toast.makeText(this@LoginScreen,state.loginError.orEmpty(),Toast.LENGTH_SHORT).show()
//                }
//                val events = loginViewModel.events
//                LaunchedEffect(Unit) {
//                    events.collect { loginUiEvent ->
//
//
//                    }
//                }
                LoginScreenComponent(
                    onAction = {
                       loginViewModel.onUiAction(it)
                    },
                    state = state
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            loginViewModel.events.collect{ loginUiEvent ->
                when(loginUiEvent){
                    LoginUiEvent.LoginSuccessful -> {
                        Toast.makeText(this@LoginScreen,"Login Successful", Toast.LENGTH_SHORT).show()
                    }
                    is LoginUiEvent.LoginUnSuccessful -> {
                        Toast.makeText(this@LoginScreen,loginUiEvent.message, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    @Composable
    fun LoginScreenComponent(
        state: LoginScreenState,
        onAction: (LoginUiAction) -> Unit
    ){
        LoginScreenComponent(
            password = state.password,
            loading = state.loading,
            onLogin = {
                onAction(LoginUiAction.LoginClick)
            },
            onEmailChange = {
                onAction(LoginUiAction.EnterEmail(it))
            },
            onPasswordChange = {
                onAction(LoginUiAction.EnterPassword(it))
            },
            email = state.email,
            onCheckboxChange = {
                onAction(LoginUiAction.CheckBox(it))
               // state.isChecked
            },
            checked = state.isChecked
        )
    }

    data class Credentials(
        @SerializedName("username")
        val email: String,
        @SerializedName("password")
        val password: String,
    )

    @SuppressLint("NotConstructor")
    @Composable
    fun LoginScreenComponent(
        //api: LoginApi
       // viewModel: LoginViewModel,
        email: String,
        password: String,
        loading: Boolean,
        onLogin: () -> Unit,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit,
        onCheckboxChange: (Boolean) -> Unit,
        checked: Boolean
    ){
       // val email by viewModel.email.collectAsState()
       // val password by viewModel.password.collectAsState()
       // val loading by viewModel.loading.collectAsState()


        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
//            var credentials by remember {
//                mutableStateOf(
//                    Credentials("", "")
//                )
//            }
//            var loginPressed by remember { mutableStateOf(false) }
//            LaunchedEffect(loginPressed) {
//                if (loginPressed) {
//                    if (credentials.email.isNotEmpty() && credentials.password.isNotEmpty()){
//                        Log.d(tag, "LoginScreen: LoginCalled")
//                        try {
//                            val response = api.login(credentials)
//                            Log.d(tag, "AuthToken: ${response.authToken}")
//                            Log.d(tag, "LoginScreen: $response")
//                        } catch (e: Exception) {
//                            Log.e(tag, "Login error: ${e.message}", e)
//                        }
//                    }
//                    loginPressed = false
//                }
//            }

            TextField(
                value = email,
                onValueChange = {
                    onEmailChange(it)
                }
            )
            TextField(
                value = password,
                onValueChange = {
                  onPasswordChange(it)
                }
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = {
                        onCheckboxChange(it)
                    }
                )
                Text(text = "Remember me")
            }


            Button(
                onClick = {
                   onLogin()
                }
            ) {
                Text(
                    text = "Login"
                )
            }
        }
    }

    @Preview
    @Composable
    fun LoginScreenPreview(){
        LoginScreenComponent(
            email="abc@gmail.com",
            password = "abc",
            loading = true,
            onLogin = {},
            onEmailChange = {},
            onPasswordChange = {},
            onCheckboxChange = {},
            checked = false
        )
    }
}


data class LoginResponse(
    val id: Int = 0,
    val username: String = "",
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val gender: String = "",
    val image: String = "",
    @SerializedName(value = "accessToken", alternate = ["token", "bc", "authToken"])
    val authToken: String? = null,
    val refreshToken: String? = null
)

// 1. Add Retrofits As Dependency
// 2. API Client ( using Retrofit )
// 3. Response/Request DTO ( Data Transfer Object )
interface LoginApi {
    @POST("/auth/login")
    suspend fun login(
       @Body credentials: LoginScreen.Credentials
    ): LoginResponse
}

object RetrofitClientBuilder {
    fun create(): LoginApi {
        return Retrofit.Builder()
            .baseUrl("https://dummyjson.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<LoginApi>()
    }
}
