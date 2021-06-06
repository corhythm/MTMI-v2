package com.example.mtmimyeon_gitmi.db

import android.os.Parcel
import android.os.Parcelable

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
    constructor() : this("","","","","","","",""){}

}
