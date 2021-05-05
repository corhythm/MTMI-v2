package com.example.mtmimyeon_gitmi.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.mtmimyeon_gitmi.DB.Database_M
import com.example.mtmimyeon_gitmi.R
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
                var DB_M=Database_M()


                Log.d("id check : ", binding.editTextSignUpId.text.toString())
                Log.d("pw check : ",binding.editTextSignUpPw.text.toString())
                var db_id:String=binding.editTextSignUpId.text.toString()
                var db_pw:String=binding.editTextSignUpPw.text.toString()
                val isSuccessful: Boolean = DB_M.createEmail(binding.editTextSignUpId.text.toString(),binding.editTextSignUpPw.text.toString(),this)

                // Choose a stop animation if your call was successful or not
                if (isSuccessful) {
                    binding.buttonSignUpGoToSignUp.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND
                    ) {
                        Log.d("로그", binding.editTextSignUpConfirmPw.text.toString())
                        finish()
                    }
                } else {
                    binding.buttonSignUpGoToSignUp.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                }
            }, 300)
        }
    }


}