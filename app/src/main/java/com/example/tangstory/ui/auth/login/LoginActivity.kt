package com.example.tangstory.ui.auth.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.viewModels
import com.example.tangstory.data.Result
import com.example.tangstory.data.model.LoginResponse
import com.example.tangstory.databinding.ActivityLoginBinding
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.auth.register.SignupActivity
import com.example.tangstory.ui.story.story.StoryActivity


class LoginActivity : AppCompatActivity() {
    private var _activityLoginBinding: ActivityLoginBinding? = null
    private val binding get() = _activityLoginBinding!!

    private val viewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance (this@LoginActivity)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activityLoginBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(_activityLoginBinding?.root)

        binding.apply {
            signupButton.setOnClickListener{
                val intent = Intent(this@LoginActivity,SignupActivity::class.java)
                startActivity(intent)
            }

            loginButton.setOnClickListener{
                val email = edLoginEmail.text.toString()
                val password = edLoginPassword.text.toString()

                when {
                    email.isEmpty() -> {
                        edLoginEmail.error = "Email tidak boleh kosong!"
                    }
                    password.isEmpty() -> {
                        edLoginPassword.error = "Password tidak boleh kosong!"
                    }
                    else -> {
                        viewModel.login(email,password).observe(this@LoginActivity) { result ->
                            if (result != null){
                                when(result){
                                    is Result.Loading -> {
                                        setLoading(true)
                                    }
                                    is Result.Success -> {
                                        loginAction(result.data)
                                        setLoading(false)
                                    }
                                    is Result.Error -> {
                                        setLoading(false)
                                        Toast.makeText(this@LoginActivity,result.error, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }

        playAnimation()
        setupView()

    }


    private fun loginAction(data: LoginResponse) {
           if (data.error){
               Toast.makeText(this@LoginActivity,data.message,Toast.LENGTH_SHORT).show()
           }else{
               Toast.makeText(this@LoginActivity,data.message,Toast.LENGTH_SHORT).show()
               viewModel.saveSession(data.loginResult)
               val intent = Intent(this, StoryActivity::class.java)
               startActivity(intent)
               finish()
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

        val duration = 200L

        val anim = listOf(
            ObjectAnimator.ofFloat(binding.titleTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.messageTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA,1f).setDuration
                (duration),
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA,1f).setDuration
                (duration),
            ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA,1f).setDuration(duration),
        )

        AnimatorSet().apply {
            playSequentially(anim)
            start()
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progresBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        _activityLoginBinding = null
    }
}