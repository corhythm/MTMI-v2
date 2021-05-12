package com.example.mtmimyeon_gitmi.db

//
//data class UserData(var id:String,var pw:String,var student_id:String,var birth:String,var gender:String,var email:String) {
//    init{
//        this.id=id
//        this.pw=pw
//        this.student_id=student_id
//        this.birth=birth
//        this.gender=gender
//        this.email=email // id와 email에 필요사항 정의 할 필요 있음
//    }
//}
data class UserData(var id: String, var pw: String) {
    init {
        this.id = id
        this.pw = pw
    }
}