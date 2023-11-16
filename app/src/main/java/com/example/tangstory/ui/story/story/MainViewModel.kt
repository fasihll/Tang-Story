package com.example.tangstory.ui.story.story


import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.tangstory.data.model.ListStoryItem
import com.example.tangstory.repository.StoryRepository
import java.io.File


class MainViewModel(
    private val storyRepository: StoryRepository
): ViewModel() {

    val location = MutableLiveData<Location>()

    val getAllStories: LiveData<PagingData<ListStoryItem>> =
        storyRepository.getAllStories().cachedIn(viewModelScope)

    fun getAllStoriesLocation()= storyRepository.getAllStoriesLocation()

    fun addNewStory(imageFile: File, description: String) = storyRepository.addNewStory(imageFile,description)

    fun addNewStoryWithLocation(imageFile: File, description: String) =
        storyRepository.addNewStoryWithLocation(imageFile,description,location.value!!)
}