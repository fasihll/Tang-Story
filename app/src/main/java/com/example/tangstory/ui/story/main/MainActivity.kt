package com.example.tangstory.ui.story.main

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.tangstory.databinding.ActivityWelcomeBinding
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.auth.login.LoginActivity
import com.example.tangstory.ui.auth.login.LoginViewModel
import com.example.tangstory.ui.story.story.StoryActivity

class MainActivity : AppCompatActivity() {
    private var _activityWelcomeBinding: ActivityWelcomeBinding? = null
    private val binding get() = _activityWelcomeBinding!!

    private val viewModel by viewModels<LoginViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityWelcomeBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(_activityWelcomeBinding?.root)

        viewModel.getSession().observe(this@MainActivity){ user ->
            if (user.isLogin != false){
                startActivity(Intent(this, StoryActivity::class.java))
                finish()
            }
        }

        playAnimation()
        setupView()

        binding.apply {
            startButton.setOnClickListener{
                val intent = Intent(this@MainActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.imageView, View.TRANSLATION_X,-30f,30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val duration = 1000L

        val start = ObjectAnimator.ofFloat(binding.startButton, View.ALPHA,1f).setDuration(duration)
        val title = ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA, 1f).setDuration(duration)
        val desc = ObjectAnimator.ofFloat(binding.descTextView, View.ALPHA, 1f).setDuration(duration)

        AnimatorSet().apply {
            playSequentially(title,desc,start)
            start()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityWelcomeBinding = null
    }
}