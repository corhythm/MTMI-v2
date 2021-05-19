package com.example.mtmimyeon_gitmi.crawling

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.HomeActivity
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.DialogLmsAuthenticationBinding
import com.example.mtmimyeon_gitmi.myClass.MyClassTimetableActivity
import kotlinx.coroutines.*
import okhttp3.internal.wait
import www.sanju.motiontoast.MotionToast

class LmsAuthenticationDialog(
    private val mContext: Context,
    private val activityType: Class<out Activity>
) : Dialog(mContext),
    ObserveCrawlingInterface {
    private lateinit var binding: DialogLmsAuthenticationBinding
    private val TAG = "temp"

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
                activityType = this.activityType,
                observeCrawlingInterface = this,
                mContext = this.mContext,
                myId = binding.editTextLmsAuthenticationId.text.toString(),
                myPw = binding.editTextLmsAuthenticationPw.text.toString()
            )

            CoroutineScope(Dispatchers.IO).launch { crawlingLmsInfo.getLmsData() }
        }
    }

    override suspend fun isCrawlingFinished(activityType: Class<out Activity>, isSuccess: Boolean) {
        withContext(Dispatchers.Main) {
            dismiss()
            if(isSuccess) {
                Intent(mContext, activityType).also {
                    mContext.startActivity(it)
                    (mContext as HomeActivity).overridePendingTransition(
                        R.anim.activity_slide_in,
                        R.anim.activity_slide_out
                    )
                }
            }
        }

    }
}

interface ObserveCrawlingInterface {
    suspend fun isCrawlingFinished(activityType: Class<out Activity>, isSuccess: Boolean)
}
