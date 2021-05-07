package com.example.mtmimyeon_gitmi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.example.mtmimyeon_gitmi.databinding.DialogLmsAuthenticationBinding
import com.marozzi.roundbutton.RoundButton

class LmsAuthenticationDialog(context: Context): Dialog(context) {
    private lateinit var binding: DialogLmsAuthenticationBinding
    private val mContext: Context = context

    // when dialog on memory
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogLmsAuthenticationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // dialog 테두리 변경
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        binding.buttonLmsAuthenticationGetAuthentication.setOnClickListener {
            binding.buttonLmsAuthenticationGetAuthentication.startAnimation()
            Handler().postDelayed({ // delay 후 실행할 코드
                binding.buttonLmsAuthenticationGetAuthentication.stopAnimation()
                binding.buttonLmsAuthenticationGetAuthentication.setResultState(RoundButton.ResultState.SUCCESS)
                binding.buttonLmsAuthenticationGetAuthentication.revertAnimation()
                Toast.makeText(mContext, "사용자 정보를 받아오는 중입니다", Toast.LENGTH_SHORT).show()
            }, 1500)
        }
    }
}