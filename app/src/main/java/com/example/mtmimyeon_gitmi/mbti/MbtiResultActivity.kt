package com.example.mtmimyeon_gitmi.mbti

import android.content.res.TypedArray
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMbtiResultBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager

class MbtiResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMbtiResultBinding
    private val myMbtiResult = SharedPrefManager.getMyMbtiType() // 내 mbti 결과 가져오기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMbtiResultBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        val mbtiImgList = resources.obtainTypedArray(R.array.mbti_img)
        // 해당 mbti에 해당하는 values.xml index 매칭 HashMap
        val mbtiIndex = HashMap<String, Int>()
        mbtiIndex["INTJ"] = 0
        mbtiIndex["INFJ"] = 1
        mbtiIndex["ISTJ"] = 2
        mbtiIndex["ISTP"] = 3

        mbtiIndex["INTP"] = 4
        mbtiIndex["INFP"] = 5
        mbtiIndex["ISFJ"] = 6
        mbtiIndex["ISFP"] = 7

        mbtiIndex["ENTJ"] = 8
        mbtiIndex["ENFJ"] = 9
        mbtiIndex["ESTJ"] = 10
        mbtiIndex["ESTP"] = 11

        mbtiIndex["ENTP"] = 12
        mbtiIndex["ENFP"] = 13
        mbtiIndex["ESFJ"] = 14
        mbtiIndex["ESFP"] = 15

        val mbtiTitleList = resources.getStringArray(R.array.mbti_title)
        val mbtiTypeCharacteristicsList = resources.getStringArray(R.array.mbti_type_characteristics)

        binding.imageViewActivityMbtiResultMbtiResultImg.setImageResource(mbtiImgList.getResourceId(mbtiIndex[myMbtiResult]!!, -1))
        binding.textViewActivityMbtiResultMbtiResultTitle.text = mbtiTitleList[mbtiIndex[myMbtiResult]!!]
        binding.textViewActivityMbtiResultMbtiTypeCharacteristics.text = mbtiTypeCharacteristicsList[mbtiIndex[myMbtiResult]!!]

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}