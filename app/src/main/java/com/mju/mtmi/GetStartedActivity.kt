package com.mju.mtmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.mju.mtmi.databinding.ActivityGetStartedBinding
import com.mju.mtmi.account.LoginActivity

class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        binding.buttonGetStartedGoToGetStarted.setOnClickListener {
//            Intent(this, LoginActivity::class.java).also {
//                startActivity(it)
//                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
//            }
//            finish() // 임시
//            Log.d("로그", "GetStartedActivity -onCreate() called")
//        }

        Handler().postDelayed({
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
            finish()
        }, 1000)


    }
}