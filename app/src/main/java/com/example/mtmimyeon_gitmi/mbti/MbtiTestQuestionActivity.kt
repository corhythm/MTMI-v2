package com.example.mtmimyeon_gitmi.mbti

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMbtiTestQuestionBinding
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment

class MbtiTestQuestionActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMbtiTestQuestionBinding
    private var nowQuestionStatusCount = 0 // 현재 질문 개수
    private var totalQuestionNum = 0
    private lateinit var aTypeMbtiQuestion: Array<String>
    private lateinit var bTypeMbtiQuestion: Array<String>
    private var myMbtiResult = ""
    private var aTypeMbtiQuestionCount = 0
    private var bTypeMbtiQuestionCount = 0
    private val A_TYPE = 0
    private val B_TYPE = 1

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
        aTypeMbtiQuestion = resources.getStringArray(R.array.mbti_E)
        bTypeMbtiQuestion = resources.getStringArray(R.array.mbti_I)

        binding.textViewActivityMbtiTestQuestionFirstQuestion.text = aTypeMbtiQuestion[0]
        binding.textViewActivityMbtiTestQuestionSecondQuestion.text = bTypeMbtiQuestion[0]

        // 선택지 두 개 중 하나 선택했을 때
        binding.textViewActivityMbtiTestQuestionFirstQuestion.setOnClickListener {
            aTypeMbtiQuestionCount++
            updateMbtiResult(A_TYPE, aTypeMbtiQuestionCount)
        }

        binding.textViewActivityMbtiTestQuestionSecondQuestion.setOnClickListener {
            bTypeMbtiQuestionCount++
            updateMbtiResult(B_TYPE, bTypeMbtiQuestionCount)
        }

    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    private fun updateMbtiResult(whatType: Int, questionCount: Int) {
        if (nowQuestionStatusCount == totalQuestionNum) return
        nowQuestionStatusCount++

        when (nowQuestionStatusCount) {
            5 -> {
                aTypeMbtiQuestion = resources.getStringArray(R.array.mbti_S)
                bTypeMbtiQuestion = resources.getStringArray(R.array.mbti_N)

                var mySideSpelling = ""
                var oppositeSideSpelling = ""

                if (whatType == A_TYPE) { // 외향
                    mySideSpelling = "E"
                    oppositeSideSpelling = "I"
                } else { // 내향
                    mySideSpelling = "I"
                    oppositeSideSpelling = "E"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)
            }
            10 -> {
                aTypeMbtiQuestion = resources.getStringArray(R.array.mbti_T)
                bTypeMbtiQuestion = resources.getStringArray(R.array.mbti_F)

                var mySideSpelling = ""
                var oppositeSideSpelling = ""

                if (whatType == A_TYPE) {
                    mySideSpelling = "N"
                    oppositeSideSpelling = "S"
                } else {
                    mySideSpelling = "S"
                    oppositeSideSpelling = "N"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)

            }
            15 -> {
                aTypeMbtiQuestion = resources.getStringArray(R.array.mbti_J)
                bTypeMbtiQuestion = resources.getStringArray(R.array.mbti_P)

                var mySideSpelling = ""
                var oppositeSideSpelling = ""

                if (whatType == A_TYPE) { // 사고
                    mySideSpelling = "T"
                    oppositeSideSpelling = "F"
                } else { // 감정
                    mySideSpelling = "F"
                    oppositeSideSpelling = "T"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)
            }
            20 -> {
                binding.progressBarActivityMbtiTestQuestionStatus.progress =
                    nowQuestionStatusCount.toFloat()
                binding.progressBarActivityMbtiTestQuestionStatus.labelText =
                    "achieve $nowQuestionStatusCount / $totalQuestionNum"

                var mySideSpelling = ""
                var oppositeSideSpelling = ""

                if (whatType == A_TYPE) { // 판단
                    mySideSpelling = "J"
                    oppositeSideSpelling = "P"
                } else { // 인식
                    mySideSpelling = "P"
                    oppositeSideSpelling = "J"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)

                Log.d("로그", "myMbtiResulit = $myMbtiResult")
                return
            }

        }
        Log.d("로그", "MbtiTestQuestionActivity -onClick() called / $nowQuestionStatusCount")
        binding.textViewActivityMbtiTestQuestionFirstQuestion.text =
            aTypeMbtiQuestion[nowQuestionStatusCount % aTypeMbtiQuestion.size]
        binding.textViewActivityMbtiTestQuestionSecondQuestion.text =
            bTypeMbtiQuestion[nowQuestionStatusCount % bTypeMbtiQuestion.size]

        binding.progressBarActivityMbtiTestQuestionStatus.progress =
            nowQuestionStatusCount.toFloat()
        binding.progressBarActivityMbtiTestQuestionStatus.labelText =
            "achieve $nowQuestionStatusCount / $totalQuestionNum"
    }

    private fun decideMbtiSpellingType( // mbti 철자 결정
        mySideMbtiQuestionCount: Int,
        mySideSpelling: String,
        oppositeSideSpelling: String
    ) {
        if (mySideMbtiQuestionCount > 3)
            this.myMbtiResult += mySideSpelling
        else
            this.myMbtiResult += oppositeSideSpelling
    }

    override fun onBackPressed() {
        if (nowQuestionStatusCount == 0) { // 단계를 진행을 안 했을 때
            super.onBackPressed()
        } else { // toast 메시지로 나갈 건지 의사 묻기
            val mDialog = BottomSheetMaterialDialog.Builder(this)
                .setTitle("Stop?")
                .setAnimation("question.json")
                .setMessage(
                    "MBTI 테스트를 그만두시겠어요?\n(도중에 테스트를 중단하면 결과가 저장되지 않아요)",
                    TextAlignment.CENTER
                )
                .setPositiveButton("Yes") { dialogInterface, which ->
                    super.onBackPressed()
                }
                .setNegativeButton("No") { dialogInterface, which ->
                    dialogInterface.dismiss()
                }
                .build();


            // Show Dialog
            mDialog.show();
        }
    }
}