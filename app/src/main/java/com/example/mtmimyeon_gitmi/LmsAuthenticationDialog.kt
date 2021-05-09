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

            val crawlingLmsInfo = CrawlingLmsInfo(
                lmsAuthenticationDialog = this@LmsAuthenticationDialog,
                myId = "60172121",
                myPw = "ksu99038485!"
            )
            crawlingLmsInfo.startCrawling()
        }
    }

    // Toast 메시지 출력
    override fun catchCurrentStatus(title: String, message: String, style: String) {
        MotionToast.createColorToast(
            mContext as HomeActivity,
            title,
            message,
            style,
            MotionToast.GRAVITY_BOTTOM,
            MotionToast.SHORT_DURATION,
            ResourcesCompat.getFont(mContext, R.font.helvetica_regular)
        )
    }
}

// Crawling Status Observing
interface ObserveCrawling {
    fun catchCurrentStatus(title: String, message: String, style: String)
}