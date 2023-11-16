package com.example.tangstory.ui.story.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.tangstory.data.model.ListStoryItem
import com.example.tangstory.databinding.ActivityDetailStroyBinding
import com.example.tangstory.helper.formatDate
import java.util.TimeZone

class DetailStroyActivity : AppCompatActivity() {
    private var _activityDetailStroyBinding: ActivityDetailStroyBinding? = null
    private val binding get() = _activityDetailStroyBinding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityDetailStroyBinding = ActivityDetailStroyBinding.inflate(layoutInflater)
        setContentView(_activityDetailStroyBinding?.root)

        setupData()
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<ListStoryItem>(STORY) as ListStoryItem
        binding.apply {
            Glide.with(this@DetailStroyActivity)
                .load(story.photoUrl)
                .into(ivDetailPhoto)
            tvDate.text = formatDate(story.createdAt.toString(),TimeZone.getDefault().id)
            tvDetailName.text = story.name
            tvDetailDescription.text = story.description
            tvLat.text = if (story.lat != null) "Lat: "+story.lat else "Lat: null"
            tvLon.text = if (story.lon != null) "Lon: "+story.lon else "Lon: null"
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _activityDetailStroyBinding = null
    }

    companion object{
        const val STORY =  "story"
    }
}