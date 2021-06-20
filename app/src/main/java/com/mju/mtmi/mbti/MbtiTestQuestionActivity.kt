package com.mju.mtmi.mbti

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mju.mtmi.R
import com.mju.mtmi.database.DataBaseCallback
import com.mju.mtmi.database.FirebaseManager
import com.mju.mtmi.databinding.ActivityMbtiTestQuestionBinding
import com.mju.mtmi.databinding.ItemQuestionBannerBinding
import com.mju.mtmi.util.SharedPrefManager
import dev.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog
import dev.shreyaspatil.MaterialDialog.model.TextAlignment
import kotlin.math.abs

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

        // ViewPager 들어갈 데이터 설정
        val mbtiImgList = resources.obtainTypedArray(R.array.mbti_img)
        val mbtiTextList = resources.getStringArray(R.array.mbti_question_banner_text)
        val questionBannerList = ArrayList<QuestionBannerItem>()

        val bannerColorList = arrayListOf(
            R.drawable.bg_rounded_banner1,
            R.drawable.bg_rounded_banner2,
            R.drawable.bg_rounded_banner3,
            R.drawable.bg_rounded_banner4,
        )
        val bannerItemList = ArrayList<QuestionBannerItem>()

        for (i in mbtiTextList.indices) {
            questionBannerList.add(
                QuestionBannerItem(
                    imgId = mbtiImgList.getResourceId(i, -1),
                    text = mbtiTextList[i],
                    background = bannerColorList[i % 4]
                )
            )
        }

        // 뷰페이저 설정
        val questionBannerAdapter =
            QuestionBannerAdapter(mContext = this, questionBannerItemList = questionBannerList)
        binding.viewPager2ActivityMbtiTestQuestionBanner.apply {
            setPageTransformer(DepthPageTransformer())
            adapter = questionBannerAdapter
        }

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

    private fun updateMbtiResult(whatType: Int, questionCount: Int) {
        if (nowQuestionStatusCount == totalQuestionNum) return

        nowQuestionStatusCount++

        // 뷰페이저 뷰 변경
        binding.viewPager2ActivityMbtiTestQuestionBanner.setCurrentItem(
            nowQuestionStatusCount % 16,
            true
        )

        when (nowQuestionStatusCount) {
            5 -> {
                aTypeMbtiQuestion = resources.getStringArray(R.array.mbti_S)
                bTypeMbtiQuestion = resources.getStringArray(R.array.mbti_N)

                val mySideSpelling: String
                val oppositeSideSpelling: String

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

                val mySideSpelling: String
                val oppositeSideSpelling: String

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

                val mySideSpelling: String
                val oppositeSideSpelling: String

                if (whatType == A_TYPE) { // 사고
                    mySideSpelling = "F"
                    oppositeSideSpelling = "T"
                } else { // 감정
                    mySideSpelling = "T"
                    oppositeSideSpelling = "F"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)
            }
            20 -> {
                binding.progressBarActivityMbtiTestQuestionStatus.progress =
                    nowQuestionStatusCount.toFloat()
                binding.progressBarActivityMbtiTestQuestionStatus.labelText =
                    "achieve $nowQuestionStatusCount / $totalQuestionNum"

                val mySideSpelling: String
                val oppositeSideSpelling: String

                if (whatType == A_TYPE) { // 판단
                    mySideSpelling = "J"
                    oppositeSideSpelling = "P"
                } else { // 인식
                    mySideSpelling = "P"
                    oppositeSideSpelling = "J"
                }
                decideMbtiSpellingType(questionCount, mySideSpelling, oppositeSideSpelling)

                Log.d("로그", "mbti 결과 = ${this.myMbtiResult}")
                // MBTI 결과 저장
                FirebaseManager.patchMbtiType(
                    this.myMbtiResult,
                    object : DataBaseCallback<Boolean> {
                        override fun onCallback(data: Boolean) {
                            if (data) { // MBTI 타입 저장 성공
                                SharedPrefManager.setMyMbtiType(myMbtiResult)
                                Intent(
                                    this@MbtiTestQuestionActivity,
                                    MbtiResultActivity::class.java
                                ).also {
                                    startActivity(it)
                                }
                                overridePendingTransition(
                                    R.anim.activity_slide_in,
                                    R.anim.activity_slide_out
                                )
                                finish()
                            }
                        }
                    }
                )
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
                .setPositiveButton("Yes") { dialogInterface, _ ->
                    super.onBackPressed()
                    dialogInterface.dismiss()
                }
                .setNegativeButton("No") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .build();


            // Show Dialog
            mDialog.show();
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }
}

data class QuestionBannerItem(val imgId: Int, val text: String, val background: Int)

// ViewPager question banner adapter
class QuestionBannerAdapter(
    private val questionBannerItemList: ArrayList<QuestionBannerItem>,
    private val mContext: Context
) :
    RecyclerView.Adapter<BannerViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BannerViewHolder {
        return BannerViewHolder(
            ItemQuestionBannerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), mContext
        )
    }

    override fun onBindViewHolder(holder: BannerViewHolder, position: Int) {
        holder.bind(this.questionBannerItemList[position])
    }

    override fun getItemCount() = this.questionBannerItemList.size

}

// ViewPager question banner ViewHolder
class BannerViewHolder(private val item: ItemQuestionBannerBinding, private val mContext: Context) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(questionBannerItem: QuestionBannerItem) {
        item.iamgeViewActivityMbtiTestQuestionRandomMbtiImg.setImageResource(questionBannerItem.imgId)
        item.textViewActivityMbtiTestQuestionRandomMbtiTitle.text = questionBannerItem.text
        item.root.background = ContextCompat.getDrawable(mContext, questionBannerItem.background)
    }
}

// VierPager 슬라이드 애니메이션
class DepthPageTransformer : ViewPager2.PageTransformer {
    private val MIN_SCALE = 0.75f

    override fun transformPage(view: View, position: Float) {
        view.apply {
            val pageWidth = width
            when {
                position < -1 -> { // [-Infinity,-1)
                    // This page is way off-screen to the left.
                    alpha = 0f
                }
                position <= 0 -> { // [-1,0]
                    // Use the default slide transition when moving to the left page
                    alpha = 1f
                    translationX = 0f
                    translationZ = 0f
                    scaleX = 1f
                    scaleY = 1f
                }
                position <= 1 -> { // (0,1]
                    // Fade the page out.
                    alpha = 1 - position

                    // Counteract the default slide transition
                    translationX = pageWidth * -position
                    // Move it behind the left page
                    translationZ = -1f

                    // Scale the page down (between MIN_SCALE and 1)
                    val scaleFactor = (MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position)))
                    scaleX = scaleFactor
                    scaleY = scaleFactor
                }
                else -> { // (1,+Infinity]
                    // This page is way off-screen to the right.
                    alpha = 0f
                }
            }
        }
    }
}