package com.example.tangstory.repository

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.tangstory.data.Result
import com.example.tangstory.data.StoryRemoteMediator
import com.example.tangstory.data.api.ApiServices
import com.example.tangstory.data.local.StoryDatabase
import com.example.tangstory.data.model.DefaultResponse
import com.example.tangstory.data.model.ListStoryItem
import com.example.tangstory.data.model.StoriesResponse
import com.example.tangstory.helper.reduceFileImage
import com.example.tangstory.helper.wrapEspressoIdlingResource
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository(
    private val apiService: ApiServices,
    private val database: StoryDatabase
) {
    fun getAllStories(): LiveData<PagingData<ListStoryItem>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5
                ),
                remoteMediator = StoryRemoteMediator(database,apiService),
                pagingSourceFactory = {
                    database.storyDao().getAllStory()
                }
            ).liveData
        }
    }

    fun getAllStoriesLocation(): LiveData<Result<StoriesResponse>> = liveData {
    emit(Result.Loading)
        try {
            val response = apiService.getAllStoriesLocation(1)
            emit(Result.Success(response))
        }catch (e:HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DefaultResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun addNewStory(imageFile: File, description: String): LiveData<Result<DefaultResponse>> = liveData{
       emit(Result.Loading)
        try {
            val requestBody =  description.toRequestBody("text/plain".toMediaType())
            val imageFileReduce = imageFile.reduceFileImage()
            val requestImageFile = imageFileReduce.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.addNewStory(multipartBody,requestBody)
            emit(Result.Success(response))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DefaultResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }

    fun addNewStoryWithLocation(imageFile: File, description: String, location: Location):
            LiveData<Result<DefaultResponse>> = liveData{
        emit(Result.Loading)
        try {
            val imageFileReduce = imageFile.reduceFileImage()
            val requestImageFile = imageFileReduce.asRequestBody("image/jpeg".toMediaType())
            val multipartBody = MultipartBody.Part.createFormData(
                "photo",
                imageFile.name,
                requestImageFile
            )

            val response = apiService.addNewStoryWithLocation(multipartBody,description.toRequestBody(),location
                .latitude.toFloat(),location.longitude.toFloat())
            emit(Result.Success(response))
        }catch (e: HttpException){
            val errorBody = e.response()?.errorBody()?.string()
            val errorResponse = Gson().fromJson(errorBody, DefaultResponse::class.java)
            emit(Result.Error(errorResponse.message.toString()))
        }
    }


}