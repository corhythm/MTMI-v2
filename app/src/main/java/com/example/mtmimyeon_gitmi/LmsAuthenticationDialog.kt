package com.example.mtmimyeon_gitmi

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.crawling.CrawlingLmsInfo
import com.example.mtmimyeon_gitmi.databinding.DialogLmsAuthenticationBinding
import com.marozzi.roundbutton.RoundButton
import kotlinx.coroutines.*
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Connection
import org.jsoup.Jsoup
import www.sanju.motiontoast.MotionToast

class LmsAuthenticationDialog(context: Context) : Dialog(context) {
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
            CoroutineScope(Dispatchers.Main).launch {
                // UI 동기
                // network 비동기
                val crawlingLmsInfo = CrawlingLmsInfo()

                val stage1 = CoroutineScope(Dispatchers.Default).async { // network 비동기

                }.await()


                 MotionToast.createColorToast(
                    mContext as HomeActivity,
                    "Authentication",
                    "시간표 받아오기 성공",
                    MotionToast.TOAST_SUCCESS,
                    MotionToast.GRAVITY_BOTTOM,
                    MotionToast.SHORT_DURATION,
                    ResourcesCompat.getFont(mContext, R.font.helvetica_regular))


            }

        }
    }

}
