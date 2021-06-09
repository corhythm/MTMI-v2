package com.example.mtmimyeon_gitmi.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.TypedValue
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.db.Callback
import com.example.mtmimyeon_gitmi.db.DatabaseManager
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityLoginBinding
import com.royrodriguez.transitionbutton.TransitionButton
import www.sanju.motiontoast.MotionToast

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private var backKeyPressedTime: Long = 0 // 마지막으로 back key를 눌렀던 시간
    private lateinit var toast: Toast // toast 메시지
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
                val loginUserId = binding.editTextLoginEmailAddress.text.toString().trim()
                val loginUserPw = binding.editTextLoginPassword.text.toString().trim()

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
                            MotionToast.createColorToast(
                                this@LoginActivity,
                                "Login Error",
                                "아이디 혹은 비밀번호가 일치하지 않습니다.",
                                MotionToast.TOAST_ERROR,
                                MotionToast.GRAVITY_BOTTOM,
                                MotionToast.SHORT_DURATION,
                                ResourcesCompat.getFont(this@LoginActivity,
                                    R.font.maple_story_bold)
                            )
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

    override fun onBackPressed() {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis()
            toast = Toast.makeText(this, "\'뒤로\' 버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT)
            ((toast.view as ViewGroup).getChildAt(0) as TextView).setTextSize(
                TypedValue.COMPLEX_UNIT_DIP,
                15F
            ) // 토스트 메시지 폰트 사이즈 변경
            toast.show()
            return;
        }

        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            finish();
            toast.cancel();
        }

    }
}