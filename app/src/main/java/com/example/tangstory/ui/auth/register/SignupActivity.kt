package com.example.tangstory.ui.auth.register

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
import androidx.core.view.isVisible
import com.example.tangstory.data.Result
import com.example.tangstory.data.model.DefaultResponse
import com.example.tangstory.databinding.ActivitySignUpBinding
import com.example.tangstory.ui.ViewModelFactory
import com.example.tangstory.ui.auth.login.LoginActivity
import com.example.tangstory.ui.auth.login.LoginViewModel

class SignupActivity : AppCompatActivity() {
    private var _activitySignUpBinding: ActivitySignUpBinding? = null
    private val binding get() = _activitySignUpBinding!!

    private val viewModel by viewModels<LoginViewModel>{
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _activitySignUpBinding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(_activitySignUpBinding?.root)

        binding.apply {
            loginButton.setOnClickListener{
                val intent = Intent(this@SignupActivity,LoginActivity::class.java)
                startActivity(intent)
                finish()
            }

            signupButton.setOnClickListener{
                val name = edRegisterName.text.toString()
                val email = edRegisterEmail.text.toString()
                val password = edRegisterPassword.text.toString()

                when{
                    name.isEmpty() -> {
                        edRegisterName.error = "Nama tidak boleh kosong!"
                    }
                    email.isEmpty() -> {
                        edRegisterEmail.error = "Email tidak boleh kosong!"
                    }
                    password.isEmpty() -> {
                        edRegisterPassword.error = "Password tidak boleh kosong!"
                    }
                    else -> {
                        viewModel.register(name,email,password).observe(this@SignupActivity){ result ->
                            if (result != null){
                                when(result){
                                    is Result.Loading -> {
                                        setLoading(true)
                                    }
                                    is Result.Success -> {
                                        signupAction(result.data)
                                        setLoading(false)
                                    }
                                    is Result.Error -> {
                                        setLoading(false)
                                        Toast.makeText(this@SignupActivity,result.error, Toast.LENGTH_SHORT).show()

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

    private fun signupAction(data: DefaultResponse) {
        Toast.makeText(this,data.message,Toast.LENGTH_SHORT).show()
        if (data.error == false){
            finish()
        }
    }

    private fun setLoading(isLoading: Boolean) = binding.progresBar.isVisible == isLoading

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
            ObjectAnimator.ofFloat(binding.nameTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.nameEditTextLayout, View.ALPHA,1f).setDuration
                (duration),
            ObjectAnimator.ofFloat(binding.emailTextView, View.ALPHA,1f).setDuration(duration),
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA,1f).setDuration
                (duration),
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA,1f)
                .setDuration(duration),
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA,1f)
                .setDuration(duration),
            ObjectAnimator.ofFloat(binding.signupButton, View.ALPHA,1f).setDuration(duration)
        )

        AnimatorSet().apply {
            playSequentially(anim)
            start()
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        _activitySignUpBinding = null
    }
}