package com.example.mtmimyeon_gitmi.account

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.example.mtmimyeon_gitmi.DB.Callback
import com.example.mtmimyeon_gitmi.DB.Database_M
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivitySignUpBinding
import com.royrodriguez.transitionbutton.TransitionButton

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    var DB_M = Database_M()
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

                var isSuccessful: Boolean

                //임시코드: 로그랑 데이터값 확인용 ,삭제예정
                Log.d("id check : ", binding.editTextSignUpId.text.toString())
                Log.d("pw check : ", binding.editTextSignUpPw.text.toString())
                var signUp_userId: String = binding.editTextSignUpId.text.toString()
                var signUp_userPw: String = binding.editTextSignUpPw.text.toString()
                var signUp_userStudentId: String = binding.editTextSignUpStudentId.text.toString()
                var signUp_userBirth: String = binding.editTextSignUpDateOfBirth.text.toString()
                var signUp_userMajor: String = binding.spinnerSignUpMajorList.selectedItem.toString()
                var signUp_userGender: String = binding.radioButtonSignUpGenderGroup.checkedRadioButtonId.toString()

                // interface로 callback처리함 일단 임시방편용 이후에 문제가 생길경우 다른코드 대안 찾을것.
                DB_M.createEmail(signUp_userId,signUp_userPw,
                    this,
                    object : Callback<Boolean> {
                        override fun onCallback(data: Boolean) {
                            isSuccessful = data
                            Log.d("callback method", ": in")
                            Log.d("boolean check", isSuccessful.toString())
                            // Choose a stop animation if your call was successful or not
                            if (isSuccessful) {
                                binding.buttonSignUpGoToSignUp.stopAnimation(
                                    TransitionButton.StopAnimationStyle.EXPAND
                                ) {
                                    Log.d("로그", binding.editTextSignUpConfirmPw.text.toString())
                                    finish()
                                    Log.d("callback method", ": out")
                                }
                            } else {
                                binding.buttonSignUpGoToSignUp.stopAnimation(
                                    TransitionButton.StopAnimationStyle.SHAKE,
                                    null
                                )
                                Log.d("callback method", ": create auth faied")
                            }
                        }
                    })
                Log.d("After callback  : ", "Create method exit")

            }, 300)
        }
    }


}