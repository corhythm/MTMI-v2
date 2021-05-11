package com.example.mtmimyeon_gitmi.item


data class ItemSubjectInfo(
    var nowAttendanceRate: String = "",
    var totalAttendanceRate: String = "",
    var subjectName: String = "",
    var professorName: String = "",
    var lectureTime: String = "",
    var subjectCode: String = "",
    val homeworkList: ArrayList<Homework> = ArrayList<Homework>()
)

data class Homework(
    var order: String = "",
    var title: String = "",
    var onGoing: String = "",
    var isSubmitted: String = "",
    var myScore: String = "",
    var totalScore: String = "",
    var deadline: String = ""
)