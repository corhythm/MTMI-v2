package com.example.mtmimyeon_gitmi.db

import com.example.mtmimyeon_gitmi.db.Chat

data class UserData(
    var id: String,
    var pw: String,
    var student_id: String,
    var userName: String,
    var birth: String,
    var gender: String,
    var major: String,
    var userProfileImageUrl: String
) {
    constructor() : this("","","","","","","","",){}

    init {
        this.id = id
        this.pw = pw
        this.student_id = student_id
        this.userName = userName
        this.birth = birth
        this.gender = gender
        this.major = major
        this.userProfileImageUrl = ""
    }
}
