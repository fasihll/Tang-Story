package com.example.tangstory.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tangstory.di.Injection
import com.example.tangstory.repository.StoryRepository
import com.example.tangstory.repository.UserRepository
import com.example.tangstory.ui.auth.login.LoginViewModel
import com.example.tangstory.ui.story.story.MainViewModel

class ViewModelFactory(
    private val userRepository: UserRepository,
    private val storyRepository: StoryRepository,
) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when{
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            else -> throw IllegalArgumentException("Unknow ViewModel Class: "+ modelClass.name)
        }
    }

    companion object {
        fun getInstance(context: Context): ViewModelFactory {
            return ViewModelFactory(
                Injection.provideUserRepository(context),
                Injection.provideStoryRepository(context))
        }
    }

}