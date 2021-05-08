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
            // subject timetable에 추가
            subjectInfoList.forEach {
                if (it.lectureTime.contains(',')) { // 강의 시간대가 두 개면
                    it.lectureTime.split(',')[0].subSequence(2, 4).toString()
                    schedules.add(Schedule().apply {
                        // 수(1000-1050),금(0900-1050) 문자열 자르기(코드가 상당히 더럽고, 에러 발생 확률이 높다)
                        classTitle = it.subjectName.split('(')[0] // 공학수학1(KME02103-0281)
                        classPlace = it.professor
//                        professorName = it.professor
                        day = it.lectureTime.split(',')[0].subSequence(0, 1).toString().getDay()
                        startTime = Time(
                            it.lectureTime.split(',')[0].subSequence(2, 4).toString().toInt(),
                            it.lectureTime.split(',')[0].subSequence(4, 6).toString().toInt()
                        )
                        endTime = Time(
                            it.lectureTime.split(',')[0].subSequence(7, 9).toString().toInt(),
                            it.lectureTime.split(',')[0].subSequence(9, 11).toString().toInt()
                        )
                        Log.d(TAG, it.lectureTime.split(',')[0].subSequence(0, 1).toString())
                    })

                    schedules.add(Schedule().apply {
                        // 수(1000-1050),금(0900-1050) 문자열 자르기(코드가 상당히 더럽고, 에러 발생 확률이 높다)
                        classTitle = it.subjectName.split('(')[0] // 공학수학1(KME02103-0281)
                        classPlace = it.professor
//                        professorName = it.professor
                        day = it.lectureTime.split(',')[1].subSequence(0, 1).toString().getDay()
                        startTime = Time(
                            it.lectureTime.split(',')[1].subSequence(2, 4).toString().toInt(),
                            it.lectureTime.split(',')[1].subSequence(4, 6).toString().toInt()
                        )
                        endTime = Time(
                            it.lectureTime.split(',')[1].subSequence(7, 9).toString().toInt(),
                            it.lectureTime.split(',')[1].subSequence(9, 11).toString().toInt()
                        )
                        Log.d(TAG, it.lectureTime.split(',')[1].subSequence(0, 1).toString())
                    })
                } else { // 강의 시간대가 1개일 때
                    schedules.add(Schedule().apply {
                        classTitle = it.subjectName.split('(')[0]
                        classPlace = it.professor
                        day = it.lectureTime.subSequence(0, 1).toString().getDay()
                        startTime = Time(
                            it.lectureTime.subSequence(2, 4).toString().toInt(),
                            it.lectureTime.subSequence(4, 6).toString().toInt()
                        )
                        endTime = Time(
                            it.lectureTime.subSequence(7, 9).toString().toInt(),
                            it.lectureTime.subSequence(9, 11).toString().toInt()
                        )
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

        // test code
//        var schedule = Schedule()
//        schedule.classTitle = "Data Structure"; // sets subject
//        schedule.classPlace = "5401"; // sets place
//        schedule.professorName = "류연승"; // sets professor
//        schedule.startTime = Time(10, 0); // sets the beginning of class time (hour,minute)
//        schedule.endTime = Time(13, 30); // sets the end of class time (hour,minute)
//        schedules.add(schedule);
//
//        schedule = Schedule()
//        schedule.classTitle = "시스템클라우드보안"; // sets subject
//        schedule.classPlace = "5407"; // sets place
//        schedule.professorName = "조민경"; // sets professor
//        schedule.day = 2
//        schedule.startTime = Time(14, 0); // sets the beginning of class time (hour,minute)
//        schedule.endTime = Time(16, 50); // sets the end of class time (hour,minute)
//        schedules.add(schedule);
//
//        schedule = Schedule()
//        schedule.classTitle = "고급객체지향프로그래밍"; // sets subject
//        schedule.classPlace = "5401"; // sets place
//        schedule.professorName = "조세형"; // sets professor
//        schedule.day = 3
//        schedule.startTime = Time(9, 0); // sets the beginning of class time (hour,minute)
//        schedule.endTime = Time(10, 50); // sets the end of class time (hour,minute)
//        schedules.add(schedule);
//
//        schedule = Schedule()
//        schedule.classTitle = "Algorithm"; // sets subject
//        schedule.classPlace = "5403"; // sets place
//        schedule.professorName = "주우석"; // sets professor
//        schedule.day = 4
//        schedule.startTime = Time(11, 0); // sets the beginning of class time (hour,minute)
//        schedule.endTime = Time(3, 50); // sets the end of class time (hour,minute)
//        schedules.add(schedule);
//
//        schedule = Schedule()
//        schedule.classTitle = "공학수학1"; // sets subject
//        schedule.classPlace = "h1021"; // sets place
//        schedule.professorName = "소순태"; // sets professor
//        schedule.day = 3
//        schedule.startTime = Time(11, 0); // sets the beginning of class time (hour,minute)
//        schedule.endTime = Time(3, 50); // sets the end of class time (hour,minute)
//        schedules.add(schedule);


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