package com.mju.mtmi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.mju.mtmi.databinding.ActivityGetStartedBinding
import com.mju.mtmi.account.LoginActivity

class GetStartedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGetStartedBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityGetStartedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            Intent(this, LoginActivity::class.java).also {
                startActivity(it)
                overridePendingTransition(R.anim.activity_slide_in, R.anim.activity_slide_out)
            }
            finish()
        }, 500)


    }
}