package com.example.mtmimyeon_gitmi.myClass

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.crawling.CrawlingLmsInfo
import com.example.mtmimyeon_gitmi.crawling.ObserveCrawlingInterface
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassTimetableBinding
import com.example.mtmimyeon_gitmi.databinding.ItemOverallSubjectInfoVerticalBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectHomeworkHorizontalBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.github.tlaabs.timetableview.Schedule
import com.github.tlaabs.timetableview.Time
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import www.sanju.motiontoast.MotionToast
import java.lang.Exception


class MyClassTimetableActivity : AppCompatActivity(), ObserveCrawlingInterface {
    private lateinit var binding: ActivityMyClassTimetableBinding
    private val TAG = "로그"

    // This property is only valid between onCreateView and OnDestroyView
    private val schedules = ArrayList<Schedule>()
    private lateinit var itemSubjectInfoList: ArrayList<ItemSubjectInfo>
    private lateinit var subjectInfoRecyclerAdapter: SubjectInfoRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyClassTimetableBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.timetableViewMyClassTimetableTimetable.setOnStickerSelectEventListener { idx, schedules ->
            binding.timetableViewMyClassTimetableTimetable.edit(idx, schedules)
        }

        // sharedPreferences에 저장된 subject info 가져오기
        itemSubjectInfoList =
            SharedPrefManager.getUserLmsSubjectInfoList() as ArrayList<ItemSubjectInfo>

        try {
            // 현재 수강중인 과목 시간표에 추가
            itemSubjectInfoList.forEach {
                val title = it.subjectName.split('(')[0] // 공학수학1(KME02103-0281)
                val professor = it.professorName
                var lectureDay = it.lectureTime.subSequence(0, 1).toString().getDay()
                var lectureStartTime = Time(
                    it.lectureTime.subSequence(2, 4).toString().toInt(),
                    it.lectureTime.subSequence(4, 6).toString().toInt()
                )
                var lectureEndTime = Time(
                    it.lectureTime.subSequence(7, 9).toString().toInt(),
                    it.lectureTime.subSequence(9, 11).toString().toInt()
                )
                schedules.add(Schedule().apply {
                    classTitle = title
                    classPlace = professor
                    day = lectureDay
                    startTime = lectureStartTime
                    endTime = lectureEndTime
                })

                if (it.lectureTime.contains(',')) { // 강의 시간대가 두 개면
                    lectureDay = it.lectureTime.split(',')[1].subSequence(0, 1).toString().getDay()
                    lectureStartTime = Time(
                        it.lectureTime.split(',')[1].subSequence(2, 4).toString().toInt(),
                        it.lectureTime.split(',')[1].subSequence(4, 6).toString().toInt()
                    )
                    lectureEndTime = Time(
                        it.lectureTime.split(',')[1].subSequence(7, 9).toString().toInt(),
                        it.lectureTime.split(',')[1].subSequence(9, 11).toString().toInt()
                    )
                    schedules.add(Schedule().apply {
                        classTitle = title
                        classPlace = professor
                        day = lectureDay
                        startTime = lectureStartTime
                        endTime = lectureEndTime
                    })
                }
            }
        } catch (exception: Exception) {
            MotionToast.createColorToast(
                this,
                "Error",
                "시간표를 가져오는데 실패했어요",
                MotionToast.TOAST_ERROR,
                MotionToast.GRAVITY_BOTTOM,
                MotionToast.SHORT_DURATION,
                ResourcesCompat.getFont(this, R.font.helvetica_regular)
            )
            schedules.clear()
        }


        // 시간표 TimetableView에 추가
        binding.timetableViewMyClassTimetableTimetable.add(schedules);
        // binding.timetableViewMyClassTimetableTimetable.setHeaderHighlight(2) (월, 화, 수, 목, 금 중에 하이라이트 할 요일 선택)

        // 리사이클러뷰 초기화
        subjectInfoRecyclerAdapter = SubjectInfoRecyclerAdapter(itemSubjectInfoList, this)
        binding.recyclerviewMyClassTimetableHomeworkList.apply {
            adapter = subjectInfoRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MyClassTimetableActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

        // update
        binding.swipeRefreshLayoutMyClassTimetableRefresh.setOnRefreshListener {
            val crawlingLmsInfo = CrawlingLmsInfo(
                activityType = MyClassTimetableActivity::class.java,
                observeCrawlingInterface = this,
                mContext = this,
                myId = SharedPrefManager.getUserLmsId(),
                myPw = SharedPrefManager.getUserLmsPw()
            )

            CoroutineScope(Dispatchers.IO).launch {
                val test = crawlingLmsInfo.getLmsData()
            }
        }
    }

    private fun String.getDay(): Int {
        return when (this) {
            "월" -> 0
            "화" -> 1
            "수" -> 2
            "목" -> 3
            "금" -> 4
            else -> 0
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_slide_back_in, R.anim.activity_slide_back_out)
    }

    override suspend fun isCrawlingFinished(activityType: Class<out Activity>) {
        withContext(Dispatchers.Main) {
            try {
                init()
                onResume()
                binding.swipeRefreshLayoutMyClassTimetableRefresh.isRefreshing = false
            } catch (exception: Exception) {

            }
        }

    }
}

class SubjectInfoRecyclerAdapter(
    private val itemSubjectInfoList: ArrayList<ItemSubjectInfo>,
    private val mContext: Context
) :
    RecyclerView.Adapter<ItemSubjectInfoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemSubjectInfoViewHolder {
        val binding = ItemOverallSubjectInfoVerticalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemSubjectInfoViewHolder(mContext, binding)
    }

    override fun onBindViewHolder(holder: ItemSubjectInfoViewHolder, position: Int) {
        holder.bind(itemSubjectInfoList[position])
    }

    override fun getItemCount(): Int {
        return itemSubjectInfoList.size
    }
}

// recyclerview viewHolder
class ItemSubjectInfoViewHolder(
    private val mContext: Context,
    private val item: ItemOverallSubjectInfoVerticalBinding,
) :
    RecyclerView.ViewHolder(item.root) {
    private lateinit var homeworkRecyclerAdapter: HomeworkRecyclerAdapter
    private var isUnfolded = false

    fun bind(itemSubjectInfo: ItemSubjectInfo) {
        item.textViewItemOverallSubjectInfoVerticalSubjectName.text = itemSubjectInfo.subjectName
        item.textViewItemOverallSubjectInfoVerticalProfessorName.text =
            itemSubjectInfo.professorName
        item.textViewItemOverallSubjectInfoVerticalLectureTime.text = itemSubjectInfo.lectureTime
        item.textViewItemOverallSubjectInfoVerticalNowAttendance.text =
            itemSubjectInfo.nowAttendanceRate
        item.textViewItemOverallSubjectInfoVerticalTotalAttendance.text =
            "${itemSubjectInfo.totalAttendanceRate} (전체 출석률)"

        // 더보기(과제 보기 클릭했을 때)
        item.root.setOnClickListener {
            if (!isUnfolded) { // 과제목록 리스트가 접혀있을 때
                if (itemSubjectInfo.homeworkList.size != 0) {
                    item.recyclerviewItemOverallSubjectInfoVerticalHomeworkList.visibility =
                        View.VISIBLE
                    item.recyclerviewItemOverallSubjectInfoVerticalHomeworkList.apply {
                        adapter = HomeworkRecyclerAdapter(itemSubjectInfo.homeworkList)
                        layoutManager = LinearLayoutManager(
                            mContext,
                            LinearLayoutManager.VERTICAL,
                            false
                        )
                    }
                    item.imageViewItemOverallSubjectInfoVerticalMore.setImageResource(R.drawable.ic_item_overall_subject_info_vertical_arrow_up)
                    isUnfolded = true
                } else {
                    MotionToast.createColorToast(
                        mContext as MyClassTimetableActivity,
                        "No Information",
                        "이 과목은 현재 진행중인 과제가 없어요.",
                        MotionToast.TOAST_INFO,
                        MotionToast.GRAVITY_BOTTOM,
                        MotionToast.SHORT_DURATION,
                        ResourcesCompat.getFont(mContext, R.font.helvetica_regular)
                    )
                }
            } else { // 과제목록 리스트가 펼쳐져 있을 때
                item.recyclerviewItemOverallSubjectInfoVerticalHomeworkList.visibility = View.GONE
                item.imageViewItemOverallSubjectInfoVerticalMore.setImageResource(R.drawable.ic_item_overall_subject_info_vertical_arrow_down)
                isUnfolded = false
            }

        }
    }

}


data class Homework(
    var order: String = "",
    var title: String = "",
    var onGoing: String = "",
    var isSubmitted: String = "",
    var myScore: String = "",
    var totalScore: String = "",
    var deadline: String = ""
)

data class ItemSubjectInfo(
    var nowAttendanceRate: String = "",
    var totalAttendanceRate: String = "",
    var subjectName: String = "",
    var professorName: String = "",
    var lectureTime: String = "",
    var subjectCode: String = "",
    val homeworkList: ArrayList<Homework> = ArrayList<Homework>()
)


// 과제 뷰홀더
class HomeworkRecyclerAdapter(private val homeworkList: ArrayList<Homework>) :
    RecyclerView.Adapter<HomeworkViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeworkViewHolder {
        val binding = ItemSubjectHomeworkHorizontalBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomeworkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HomeworkViewHolder, position: Int) {
        holder.bind(homeworkList[position])
    }

    override fun getItemCount(): Int {
        return homeworkList.size
    }
}

// recyclerview viewHolder
class HomeworkViewHolder(private val item: ItemSubjectHomeworkHorizontalBinding) :
    RecyclerView.ViewHolder(item.root) {

    fun bind(homework: Homework) {
        item.textViewItemSubjectHomeworkHorizontalNumber.text = homework.order
        item.textViewItemSubjectHomeworkHorizontalTitle.text = homework.title
        item.textViewItemSubjectHomeworkHorizontalMyScoreValue.text = homework.myScore
        item.textViewItemSubjectHomeworkHorizontalTotalScoreValue.text = homework.totalScore
        item.textViewItemSubjectHomeworkHorizontalDeadline.text = homework.deadline

        if (homework.onGoing == "진행중" && homework.isSubmitted == "미제출") {
            item.root.setBackgroundResource(R.drawable.bg_rounded_corner_homework)
            item.viewItemSubjectHomeworkHorizontalDivider.visibility = View.INVISIBLE
        }
    }
}