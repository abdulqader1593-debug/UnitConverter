package com.example.unitconverter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.annotations.SerializedName
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.Serial

class LoginScreenPractise: ComponentActivity(){
   private val loginScreenPractiseViewModel by viewModels<LoginScreenPractiseViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme() {
                val state by loginScreenPractiseViewModel.loginScreenState.collectAsState()
                LoginScreenComponent(
                    state = state,
                    onAction = {
                        loginScreenPractiseViewModel.onUiAction(it)
                    }
                )

            }
        }
    }


    @Composable
    fun LoginScreenComponent(
        state: LoginScreenState1,
        onAction: (LoginUiAction1) -> Unit
    ){
        LoginScreenComponent1(
            email = state.email,
            password = state.password,
            onLogin = {
                onAction(LoginUiAction1.LoginClick)
                },

            onEmailChange = {
                onAction(LoginUiAction1.EnterEmail(it))
            },
            onPasswordChange = {
                onAction(LoginUiAction1.EnterPassword(it))
            },
            loading = state.loading
        )
    }

    @Composable
    fun LoginScreenComponent1(
        email:String,
        password:String,
        onLogin: ()-> Unit,
        loading: Boolean,
        onEmailChange: (String) -> Unit,
        onPasswordChange: (String) -> Unit
    ){
        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                value = email,
                onValueChange = onEmailChange
            )
            TextField(
                value = password,
                onValueChange = onPasswordChange
            )

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


}

data class LoginScreenState1(
    val email:String = "",
    val password:String = "",
    val loading: Boolean = false,
    val loginError: String ?= null
)

sealed interface LoginUiAction1{
    object LoginClick: LoginUiAction1
    data class EnterEmail(val email:String): LoginUiAction1
    data class EnterPassword(val password:String): LoginUiAction1
}

sealed interface LoginUiEvents1{
    object LoginSuccessful : LoginUiEvents1
    data class LoginFailure(val message: String): LoginUiEvents1
}

class LoginScreenPractiseViewModel: ViewModel() {
    private val api  by lazy{
        RetrofitBuilder.create()
    }
    private val TAG = "LoginScreenPractise"
    private val _loginScreenState = MutableStateFlow(LoginScreenState1())
    val loginScreenState =  _loginScreenState.asStateFlow()

    private val _events = Channel<LoginUiEvents1>()
    val events = _events.receiveAsFlow()

    fun onUiAction(action: LoginUiAction1){
        when(action){
            is LoginUiAction1.EnterEmail -> {
               onEmailChange(action.email)
            }
            is LoginUiAction1.EnterPassword -> {
               onPasswordChange(action.password)
            }
            LoginUiAction1.LoginClick -> {
                login()
            }
        }
    }


    fun login(){
        val credentials = Credentials(
            _loginScreenState.value.email,
            _loginScreenState.value.password
        )
        viewModelScope.launch {
            _loginScreenState.update {
                it.copy(
                    loading = true
                )
            }
            try{
                val results = api.login(credentials)
                results.authToken?.let {
                    _events.trySend(LoginUiEvents1.LoginSuccessful)
                } ?: {
                    _events.trySend(LoginUiEvents1.LoginFailure("Error Logging in"))
                }
            }catch(e: Exception){
                Log.d(TAG, "login: $e")
                _events.trySend(LoginUiEvents1.LoginFailure("Error logging in ${e.message}"))
                _loginScreenState.update {
                    it.copy(
                        loginError = "Error logging in"
                    )
                }
            }finally {
                _loginScreenState.update {
                    it.copy(
                        loading = false
                    )
                }
            }


        }

    }

    data class Credentials(
        @SerializedName("username")
        val email: String,
        @SerializedName("password")
        val password: String
    )

    fun onEmailChange(value: String){
        _loginScreenState.update {
            it.copy(
                email = value
            )
        }
    }

    fun onPasswordChange(value: String){
        _loginScreenState.update {
            it.copy(
                password = value
            )
        }
    }


}


data class LoginResponse1(
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

interface LoginApi1{
    @POST
    suspend fun login(
        @Body credentials: LoginScreenPractiseViewModel.Credentials
    ): LoginResponse1
}

object RetrofitBuilder{
    fun create(): LoginApi1 {
        return Retrofit.Builder()
            .baseUrl("")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create<LoginApi1>()
    }
}

