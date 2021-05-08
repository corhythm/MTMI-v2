package com.example.mtmimyeon_gitmi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.crawling.CrawlingLmsInfo
import com.example.mtmimyeon_gitmi.databinding.DialogLmsAuthenticationBinding
import kotlinx.coroutines.*
import www.sanju.motiontoast.MotionToast

class LmsAuthenticationDialog(context: Context) : Dialog(context), ObserveCrawling {
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
//            Handler().postDelayed({ // delay 후 실행할 코드
//                binding.buttonLmsAuthenticationGetAuthentication.stopAnimation()
//                binding.buttonLmsAuthenticationGetAuthentication.setResultState(RoundButton.ResultState.SUCCESS)
//                binding.buttonLmsAuthenticationGetAuthentication.revertAnimation()
//                Toast.makeText(mContext, "사용자 정보를 받아오는 중입니다", Toast.LENGTH_SHORT).show()
//            }, 1500)
            CoroutineScope(Dispatchers.IO).launch {
                // UI 동기
                val crawlingLmsInfo = CrawlingLmsInfo(this@LmsAuthenticationDialog)
                crawlingLmsInfo.startCrawling()
            }
        }
    }

    override fun catchCurrentStart(title: String, message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            MotionToast.createColorToast(
                mContext as HomeActivity,
                title,
                message,
                MotionToast.TOAST_SUCCESS,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(mContext, R.font.helvetica_regular)
            )

        }


    }

}

interface ObserveCrawling {
    fun catchCurrentStart(title: String, message: String)
}