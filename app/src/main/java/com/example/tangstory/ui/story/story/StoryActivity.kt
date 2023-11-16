package com.example.tangstory.ui.story.story

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tangstory.R
import com.example.tangstory.databinding.ActivityMainBinding
import com.example.tangstory.ui.LoadingStateAdapter
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.auth.login.LoginViewModel
import com.example.tangstory.ui.story.add.AddStoryActivity
import com.example.tangstory.ui.story.location.StoryLocationActivity
import com.example.tangstory.ui.story.main.MainActivity


class StoryActivity : AppCompatActivity() {

    private var _activityMainBinding: ActivityMainBinding? = null
    private val binding get() = _activityMainBinding!!

    private val viewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(this)
    }

    private val viewModel2 by viewModels<LoginViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(_activityMainBinding?.root)


        binding.appbar.setOnMenuItemClickListener { menuItem ->
            when(menuItem.itemId){
                R.id.action_logout -> {
                    viewModel2.logout()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this@StoryActivity,"Logged out",Toast.LENGTH_SHORT).show()
                    finish()
                    true
                }
                R.id.action_language -> {
                    startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    true
                }
                R.id.action_map -> {
                    if (NetworkAvailbale()){
                        startActivity(Intent(this,StoryLocationActivity::class.java))
                    }else{
                        Toast.makeText(this,
                            getString(R.string.network_available),Toast.LENGTH_SHORT)
                            .show()
                    }
                    true
                }
                else -> false
            }
        }

        val layoutManager = LinearLayoutManager(this@StoryActivity)
        binding.rvStory.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(this@StoryActivity,layoutManager.orientation)
        binding.rvStory.addItemDecoration(itemDecoration)

        val adapter = ListStoryAdapter()
        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter{
                adapter.retry()
            }
        )
        setLoading(true)

        viewModel.getAllStories.observe(this,{ data ->
            if (data != null){
                setLoading(false)
                adapter.submitData(lifecycle,data)
            }
        })


        binding.buttonAdd.setOnClickListener{
            val intent = Intent(this,AddStoryActivity::class.java)
            startActivity(intent)
        }

        playAnimation()
    }

    private fun playAnimation() {
        val duration = 500L
        val buttonAdd = ObjectAnimator.ofFloat(binding.buttonAdd,View.TRANSLATION_Y,0F).setDuration(duration)

        AnimatorSet().apply {
            playSequentially(buttonAdd)
            start()
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

    override fun onResume() {
        super.onResume()
        viewModel.getAllStories
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityMainBinding = null
    }
}