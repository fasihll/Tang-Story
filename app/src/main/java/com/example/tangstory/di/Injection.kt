package com.example.tangstory.di

import android.content.Context
import com.example.tangstory.data.api.ApiConfig
import com.example.tangstory.data.local.StoryDatabase
import com.example.tangstory.data.pref.UserPreference
import com.example.tangstory.data.pref.dataStore
import com.example.tangstory.repository.StoryRepository
import com.example.tangstory.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
   fun provideUserRepository(context: Context): UserRepository {
       val pref = UserPreference.getInstance(context.dataStore)
       val apiService = ApiConfig.getApiServices()
       return UserRepository(pref,apiService)
   }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiServicesWithToken(user.token)
        val database = StoryDatabase.getDatabase(context)
        return StoryRepository(apiService,database)
   }
}