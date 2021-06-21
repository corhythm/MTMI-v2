package com.mju.mtmi.database.entity

data class UserData(
    var id: String,
    var pw: String,
    var student_id: String,
    var userName: String,
    var birth: String,
    var gender: String,
    var major: String,
    var mbtiType: String,
    var userProfileImageUrl: String = ""
) {
    constructor() : this(
        id = "",
        pw = "",
        student_id = "",
        userName = "",
        birth = "",
        gender = "",
        major = "",
        mbtiType = "",
        userProfileImageUrl = ""
    )
}
