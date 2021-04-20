package com.example.mtmimyeon_gitmi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.mtmimyeon_gitmi.databinding.ActivitySignUpBinding
import com.royrodriguez.transitionbutton.TransitionButton

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val majorList = resources.getStringArray(R.array.major).toMutableList()
        binding.spinnerSignUpMajorList.setItem(majorList)

        binding.buttonSignUpGoToSignUp.setOnClickListener {
            binding.buttonSignUpGoToSignUp.startAnimation()

            // Do your networking task or background work here.
            Handler().postDelayed(Runnable {
                val isSuccessful: Boolean = true

                // Choose a stop animation if your call was succesful or not
                if (isSuccessful) {
                    binding.buttonSignUpGoToSignUp.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND
                    ) {
                        Log.d("로그", binding.editTextSignUpConfirmPassword.editText.text.toString())
                        finish()
                    }
                } else {
                    binding.buttonSignUpGoToSignUp.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)

                }
            }, 300)
        }
    }


}