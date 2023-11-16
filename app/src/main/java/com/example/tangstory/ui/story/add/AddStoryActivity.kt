package com.example.tangstory.ui.story.add

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.ConnectivityManager
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.net.toUri
import com.example.tangstory.R
import com.example.tangstory.data.Result
import com.example.tangstory.databinding.ActivityAddStoryBinding
import com.example.tangstory.helper.uriToFile
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.story.add.CameraActivity.Companion.CAMERA_RESULT
import com.example.tangstory.ui.story.story.MainViewModel
import com.example.tangstory.ui.story.story.StoryActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices


class AddStoryActivity : AppCompatActivity() {
    private var _activityAddStoryBinding: ActivityAddStoryBinding? = null
    private val binding get() = _activityAddStoryBinding!!

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(this)
    }

    private var currentImageUri: Uri? = null

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted: Boolean ->
        if (isGranted){
            Toast.makeText(this, getString(R.string.permission_request_granted),Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this, getString(R.string.permission_request_denied),Toast.LENGTH_SHORT).show()
        }
    }

    private fun allPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this,
            REQUIRED_PERMISSION
        ) == PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityAddStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(_activityAddStoryBinding?.root)

        setSupportActionBar(binding.appbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.baseline_arrow_back_24)

        if (!allPermissionGranted()){
            requestPermissionLauncher.launch(REQUIRED_PERMISSION)
        }

        binding.btnGalery.setOnClickListener{ startGalery() }
        binding.btnCamera.setOnClickListener { startCamera() }
        binding.buttonAdd.setOnClickListener{
            if (NetworkAvailbale()){
                addNewStory()
            }else{
                Toast.makeText(this,
                    getString(R.string.network_available),Toast.LENGTH_SHORT)
                    .show()
            }
        }

        playAnimation()

        binding.smNowLoaction.setOnCheckedChangeListener{_:CompoundButton, isChecked: Boolean ->
            if (isChecked){
                getMyLastLocation()
            }else{
                viewModel.location.value = null
            }
        }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

    }

    private fun playAnimation() {
        val duration = 200L

        val previewImage = ObjectAnimator.ofFloat(binding.previewImageView,View.ALPHA,1f)
            .setDuration(duration)
        val btnCamera = ObjectAnimator.ofFloat(binding.btnCamera,View.ALPHA,1f)
            .setDuration(duration)
        val btnGalery = ObjectAnimator.ofFloat(binding.btnGalery,View.ALPHA,1f)
            .setDuration(duration)
        val description = ObjectAnimator.ofFloat(binding.descriptionLayout,View.ALPHA,1f)
            .setDuration(duration)
        val buttonUpload = ObjectAnimator.ofFloat(binding.buttonAdd,View.ALPHA,1f)
            .setDuration(duration)

        val together = AnimatorSet().apply {
            playTogether(btnCamera,btnGalery)
        }

        AnimatorSet().apply {
            playSequentially(previewImage,together,description,buttonUpload)
            start()
        }

    }

    private fun addNewStory() {

        currentImageUri?.let { uri ->

            val imageFile = uriToFile(uri,this)
            Log.d("Image File: ","showImage: ${imageFile.path}")
            val description = binding.edAddDescription.text.toString()

            when{
                uri.equals("") -> Toast.makeText(this, getString(R.string.warning_emty_image),Toast
                    .LENGTH_SHORT).show()
                description.isEmpty() -> binding.edAddDescription.error = getString(R.string.warning_empty_description)
                else -> {
                    if (viewModel.location.value != null){
                        viewModel.addNewStoryWithLocation(imageFile,description).observe(this@AddStoryActivity){
                                result ->
                            if (result != null){
                                when(result){
                                    is Result.Loading -> {
                                        setLoading(true)
                                    }
                                    is Result.Success -> {
                                        Toast.makeText(this,result.data.message,Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, StoryActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    is Result.Error ->{
                                        setLoading(false)
                                        Toast.makeText(this,result.error,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }else{
                        viewModel.addNewStory(imageFile,description).observe(this@AddStoryActivity){ result ->
                            if (result != null){
                                when(result){
                                    is Result.Loading -> {
                                        setLoading(true)
                                    }
                                    is Result.Success -> {
                                        Toast.makeText(this,result.data.message,Toast.LENGTH_SHORT).show()
                                        val intent = Intent(this, StoryActivity::class.java)
                                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                        startActivity(intent)
                                        finish()
                                    }
                                    is Result.Error ->{
                                        setLoading(false)
                                        Toast.makeText(this,result.error,Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } ?: showToast()
    }

    private fun showToast() {
        Toast.makeText(this, getString(R.string.emty_image_warning),Toast.LENGTH_SHORT).show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> finish()
        }
        return  true
    }

    private fun startGalery(){
        launcherGalery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGalery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ){ uri: Uri? ->
        if (uri != null){
            currentImageUri = uri
            showImage()
        }else{
            Log.d("Photo Picker","No Media selected")
        }
    }

    private fun startCamera(){
        val intent = Intent(this,CameraActivity::class.java)
        launcherIntentCameraX.launch(intent)
    }

    private val launcherIntentCameraX = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ){
        if (it.resultCode == CAMERA_RESULT){
            currentImageUri = it.data?.getStringExtra(CameraActivity.EXTRA_CAMERAX_IMAGE)?.toUri()
            showImage()
        }
    }

    private fun showImage(){
        currentImageUri?.let {
            Log.d("Image URI","showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val requestLocationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLastLocation()
                }
                else -> {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "No Locatiion access granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun getMyLastLocation() {
        if     (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    viewModel.location.value = location
                } else {
                    Toast.makeText(
                        this@AddStoryActivity,
                        "Location is not found. Try Again",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    fun NetworkAvailbale(): Boolean{
        val connectionManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val internetInfo = connectionManager.activeNetworkInfo
        return internetInfo != null && internetInfo.isConnected
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityAddStoryBinding = null
    }

    companion object{
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}