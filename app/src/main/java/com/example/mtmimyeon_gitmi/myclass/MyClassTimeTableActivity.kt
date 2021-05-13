package com.example.mtmimyeon_gitmi.myClass

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassTimetableBinding
import com.example.mtmimyeon_gitmi.databinding.ItemOverallSubjectInfoVerticalBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectHomeworkHorizontalBinding
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.github.tlaabs.timetableview.Schedule
import com.github.tlaabs.timetableview.Time
import www.sanju.motiontoast.MotionToast
import java.lang.Exception


class MyClassTimetableActivity : AppCompatActivity() {
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
        subjectInfoRecyclerAdapter = SubjectInfoRecyclerAdapter(this)
        binding.recyclerviewMyClassTimetableHomeworkList.apply {
            adapter = subjectInfoRecyclerAdapter
            layoutManager = LinearLayoutManager(
                this@MyClassTimetableActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
            subjectInfoRecyclerAdapter.submit(itemSubjectInfoList)
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
}

class SubjectInfoRecyclerAdapter(private val mContext: Context) :
    RecyclerView.Adapter<ItemSubjectInfoViewHolder>() {
    private lateinit var itemSubjectInfoList: ArrayList<ItemSubjectInfo>

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

    fun submit(itemSubjectInfoList: ArrayList<ItemSubjectInfo>) {
        this.itemSubjectInfoList = itemSubjectInfoList
    }
}

// recyclerview viewHolder
class ItemSubjectInfoViewHolder(
    private val mContext: Context,
    private val item: ItemOverallSubjectInfoVerticalBinding
) :
    RecyclerView.ViewHolder(item.root) {
    private lateinit var homeworkRecyclerView: RecyclerView // nested recyclerview


    fun bind(itemSubjectInfo: ItemSubjectInfo) {
        item.textViewItemOverallSubjectInfoVerticalSubjectName.text = itemSubjectInfo.subjectName
        item.textViewItemOverallSubjectInfoVerticalProfessorName.text =
            itemSubjectInfo.professorName
        item.textViewItemOverallSubjectInfoVerticalLectureTime.text = itemSubjectInfo.lectureTime
        item.textViewItemOverallSubjectInfoVerticalNowAttendance.text =
            itemSubjectInfo.nowAttendanceRate
        item.textViewItemOverallSubjectInfoVerticalTotalAttendance.text =
            "${itemSubjectInfo.totalAttendanceRate} (전체 출석률)"

        val homeworkRecyclerAdapter = HomeworkRecyclerAdapter()
        item.recyclerviewItemOverallSubjectInfoVerticalHomeworkList.apply {
            adapter = homeworkRecyclerAdapter
            layoutManager = LinearLayoutManager(
                mContext,
                LinearLayoutManager.VERTICAL,
                false
            )
        }
        homeworkRecyclerAdapter.submit(itemSubjectInfo.homeworkList)
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
class HomeworkRecyclerAdapter() : RecyclerView.Adapter<HomeworkViewHolder>() {
    private lateinit var homeworkList: ArrayList<Homework>

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

    fun submit(homeworkList: ArrayList<Homework>) {
        this.homeworkList = homeworkList
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