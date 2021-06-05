package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityLoginBinding
import com.royrodriguez.transitionbutton.TransitionButton

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    var db = DatabaseManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.textViewLoginSignUp.setOnClickListener {
            Intent(this, SignUpActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }

        }

        binding.buttonLoginSignIn.setOnClickListener {
            // Start the loading animation when the user tap the button
            binding.buttonLoginSignIn.startAnimation()

            // Do your networking task or background work here.
            val handler: Handler = Handler()
            handler.postDelayed({
                val loginUserId: String = binding.editTextLoginEmailAddress.text.toString()
                val loginUserPw: String = binding.editTextLoginPassword.text.toString()

                db.loginEmail(loginUserId, loginUserPw, this, object : Callback<Boolean> {
                    override fun onCallback(data: Boolean) {
                        if (data) {
                            binding.buttonLoginSignIn.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND,
                                TransitionButton.OnAnimationStopEndListener {
                                    Intent(this@LoginActivity, HomeActivity::class.java).also {
                                        startActivity(it)
                                    }
                                    finish() // 임시
                                })
                        } else {
                            binding.buttonLoginSignIn.stopAnimation(
                                TransitionButton.StopAnimationStyle.SHAKE,
                                null
                            )
                        }
                    }
                })
                // Choose a stop animation if your call was succesful or not


            }, 300)


        }
    }
}