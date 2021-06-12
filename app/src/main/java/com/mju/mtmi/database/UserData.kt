package com.mju.mtmi.database

data class UserData(
    var id: String?,
    var pw: String,
    var student_id: String,
    var userName: String,
    var birth: String,
    var gender: String,
    var major: String,
    var userProfileImageUrl: String = ""
){
    constructor() : this("","","","","","","","")
}