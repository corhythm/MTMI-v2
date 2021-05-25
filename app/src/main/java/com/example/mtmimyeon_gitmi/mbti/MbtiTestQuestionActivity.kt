package com.example.mtmimyeon_gitmi.mbti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMbtiTestQuestionBinding
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class MbtiTestQuestionActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMbtiTestQuestionBinding
    private var nowQuestionStatusCount = 0 // 현재 질문 개수
    private var totalQuestionNum = 0
    private lateinit var firstQuestion: Array<String>
    private lateinit var secondQuestion: Array<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMbtiTestQuestionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        // 프로그래스바 설정
        totalQuestionNum = binding.progressBarActivityMbtiTestQuestionStatus.max.toInt()

        // 처음 질문: 외향 vs 내향
        firstQuestion = resources.getStringArray(R.array.mbti_E)
        secondQuestion = resources.getStringArray(R.array.mbti_I)

        binding.textViewActivityMbtiTestQuestionFirstQuestion.text = firstQuestion[0]
        binding.textViewActivityMbtiTestQuestionSecondQuestion.text = secondQuestion[0]

        // 선택지 두 개 중 하나 선택했을 때
        binding.textViewActivityMbtiTestQuestionFirstQuestion.setOnClickListener(this)
        binding.textViewActivityMbtiTestQuestionSecondQuestion.setOnClickListener(this)

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    override fun onClick(v: View?) {
        if (nowQuestionStatusCount == totalQuestionNum) return
        nowQuestionStatusCount++

        when (nowQuestionStatusCount) {
            5 -> { // 두 번째 질문: 감각 vs 직관
                firstQuestion = resources.getStringArray(R.array.mbti_S)
                secondQuestion = resources.getStringArray(R.array.mbti_N)
            }
            10 -> { // 세 번째 질문: 사고 vs 감정
                firstQuestion = resources.getStringArray(R.array.mbti_T)
                secondQuestion = resources.getStringArray(R.array.mbti_F)
            }
            15 -> { // 네 번째 질문: 판단 vs 인식
                firstQuestion = resources.getStringArray(R.array.mbti_J)
                secondQuestion = resources.getStringArray(R.array.mbti_P)
            }
            20 -> {
                binding.progressBarActivityMbtiTestQuestionStatus.progress =
                    nowQuestionStatusCount.toFloat()
                binding.progressBarActivityMbtiTestQuestionStatus.labelText =
                    "achieve $nowQuestionStatusCount / $totalQuestionNum"
                return
            }

        }
        Log.d("로그", "MbtiTestQuestionActivity -onClick() called / $nowQuestionStatusCount")
        binding.textViewActivityMbtiTestQuestionFirstQuestion.text =
            firstQuestion[nowQuestionStatusCount % firstQuestion.size]
        binding.textViewActivityMbtiTestQuestionSecondQuestion.text =
            secondQuestion[nowQuestionStatusCount % secondQuestion.size]

        binding.progressBarActivityMbtiTestQuestionStatus.progress =
            nowQuestionStatusCount.toFloat()
        binding.progressBarActivityMbtiTestQuestionStatus.labelText =
            "achieve $nowQuestionStatusCount / $totalQuestionNum"
    }

    override fun onBackPressed() {
        if (nowQuestionStatusCount == 1) { // 단계를 진행을 안 했을 때
            super.onBackPressed()
        } else { // toast 메시지로 나갈 건지 의사 묻기
            val mDialog = BottomSheetMaterialDialog.Builder(this)
                .setTitle("Stop?")
                .setAnimation("question.json")
                .setMessage(
                    "MBTI 테스트를 그만두시겠습니까? (도중에 테스트를 중단하면 결과가 저장되지 않습니다)",
                    TextAlignment.CENTER
                )
                .setPositiveButton("Yes") { dialogInterface, which ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") {dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                .build();


            // Show Dialog
            mDialog.show();
        }
    }
}