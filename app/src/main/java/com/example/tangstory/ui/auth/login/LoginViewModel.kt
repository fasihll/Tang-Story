package com.example.tangstory.ui.auth.login
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.tangstory.data.model.LoginResult
import com.example.tangstory.repository.UserRepository
import kotlinx.coroutines.launch


class LoginViewModel(private val repository: UserRepository): ViewModel() {
     fun login(email: String, password: String) = repository.login(email,password)

     fun register(name: String,email: String,password: String) = repository.register(name,email,password)

     fun getSession(): LiveData<LoginResult> {
          return repository.getSession().asLiveData()
     }

     fun logout(){
          viewModelScope.launch {
               repository.logout()
          }
     }

     fun saveSession(user: LoginResult){
          viewModelScope.launch {
               repository.saveSession(user)
          }
     }
}