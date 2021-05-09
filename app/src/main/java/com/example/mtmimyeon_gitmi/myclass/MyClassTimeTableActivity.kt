package com.example.mtmimyeon_gitmi.myClass

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.example.mtmimyeon_gitmi.R
import com.example.mtmimyeon_gitmi.databinding.ActivityMyClassTimetableBinding
import com.example.mtmimyeon_gitmi.databinding.ItemSubjectInfoBinding
import com.example.mtmimyeon_gitmi.recyclerview_item.ItemSubjectInfo
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.github.tlaabs.timetableview.Schedule
import com.github.tlaabs.timetableview.Time
import www.sanju.motiontoast.MotionToast
import java.lang.Exception
import kotlin.system.exitProcess

enum class Day(day: String, dayNum: Int) {
    MON("월", 1),
    TUE("화", 2),
    WED("수", 3),
    THU("목", 4),
    FRI("금", 5)
}

class MyClassTimeTableActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyClassTimetableBinding
    private val TAG = "로그"

    // This property is only valid between onCreateView and OnDestroyView
    private val schedules = ArrayList<Schedule>()
    private lateinit var subjectInfoList: ArrayList<ItemSubjectInfo>

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
        subjectInfoList =
            SharedPrefManager.getUserLmsSubjectInfoList() as ArrayList<ItemSubjectInfo>

        try {
            // 현재 수강중인 과목 시간표에 추가
            subjectInfoList.forEach {
                val title = it.subjectName.split('(')[0] // 공학수학1(KME02103-0281)
                val professor = it.professor
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


        //.. add one or more schedules
        binding.timetableViewMyClassTimetableTimetable.add(schedules);
        binding.timetableViewMyClassTimetableTimetable.setHeaderHighlight(2)

        Log.d(
            "로그",
            binding.timetableViewMyClassTimetableTimetable.allSchedulesInStickers.toString()
        )
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

    // timetable에 추가
    private fun addTimetable(
        classTitle: String,
        classPlace: String,
        day: Int,
        startTime: Time,
        endTime: Time
    ) {
        schedules.add(Schedule().apply {
            this.classTitle = classTitle
            this.classPlace = classPlace
            this.day = day
            this.startTime = startTime
            this.endTime = endTime
        })
    }
}