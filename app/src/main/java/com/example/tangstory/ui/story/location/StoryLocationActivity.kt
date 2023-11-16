package com.example.tangstory.ui.story.location

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.example.tangstory.R
import com.example.tangstory.data.Result
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.tangstory.databinding.ActivityStoryLocationBinding
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.story.story.MainViewModel
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions

class StoryLocationActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryLocationBinding
    private val boundsBuilder = LatLngBounds.Builder()

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appbar)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isIndoorLevelPickerEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true

        setMapStyle()
        addManyMarker()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return true
    }

    fun setMapStyle(){
        try {
            val success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this,R.raw
                .map_style))
            if (!success){
                Toast.makeText(this,getString(R.string.style_parsing_failed),Toast.LENGTH_SHORT).show()
                Log.e(TAG, getString(R.string.style_parsing_failed))
            }
        }catch (exception: Resources.NotFoundException){
            Toast.makeText(this,getString(R.string.cant_find_style),Toast.LENGTH_SHORT).show()
            Log.e(TAG, getString(R.string.cant_find_style),exception)
        }
    }

    fun addManyMarker(){
        viewModel.getAllStoriesLocation().observe(this){ result ->
            if (result != null){
                when(result){
                    is Result.Loading -> {
                        setLoading(true)
                    }
                    is Result.Success -> {
                        if (result.data.listStory != null){
                            result.data.listStory.forEach{ data ->
                                val latLng = LatLng(data?.lat!!.toDouble(),data?.lon!!.toDouble())
                                mMap.addMarker(
                                    MarkerOptions()
                                        .position(latLng)
                                        .title(data.name)
                                        .snippet(data.description)
                                )
                                boundsBuilder.include(latLng)
                            }

                            val bounds: LatLngBounds = boundsBuilder.build()

                            mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    bounds,
                                    resources.displayMetrics.widthPixels,
                                    resources.displayMetrics.heightPixels,
                                    300
                                )
                            )
                        }
                        setLoading(false)
                    }
                    is Result.Error -> {
                        Toast.makeText(this,result.error,Toast.LENGTH_SHORT).show()
                        setLoading(false)
                    }
                }
            }
        }
    }



    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object{
        private const val TAG = "StoryLocationActivity"
    }
}