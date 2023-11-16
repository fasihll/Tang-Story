package com.example.tangstory.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.tangstory.data.Result
import com.example.tangstory.data.api.ApiServices
import com.example.tangstory.data.model.DefaultResponse
import com.example.tangstory.data.model.LoginResponse
import com.example.tangstory.data.model.LoginResult
import com.example.tangstory.data.pref.UserPreference
import com.example.tangstory.helper.wrapEspressoIdlingResource
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException


class UserRepository(
    private val userPreference: UserPreference,
    private val apiService: ApiServices
){

    suspend fun saveSession(user: LoginResult){
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<LoginResult> {
        return userPreference.getSession()
    }

    fun login(email:String,password: String): LiveData<Result<LoginResponse>> = liveData{
        emit(Result.Loading)
       wrapEspressoIdlingResource {
           try {
               val response = apiService.login(email,password)
               emit(Result.Success(response))
           }catch (e: HttpException){
               val errorBody = e.response()?.errorBody()?.string()
               val errorResponse = Gson().fromJson(errorBody,LoginResponse::class.java)
               emit(Result.Error(errorResponse.message))
           }
       }
    }

    fun register(name: String,email: String,password: String): LiveData<Result<DefaultResponse>> = liveData {
        emit(Result.Loading)
        try {
            val response = apiService.register(name,email,password)
            emit(Result.Success(response))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody,DefaultResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    suspend fun logout(){
        userPreference.logout()
    }
}