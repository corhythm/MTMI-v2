package com.example.mtmimyeon_gitmi.db

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DatabaseManager {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    //  회원가입 메소드 ( 정보 기입 필요한 )
//    fun createEmail(
//        id: String,
//        pw: String,
//        confirm: String,
//        name: String,
//        studentId: String,
//        major: String,
//        birth: String,
//        gender: String,
//        activity: Activity,
//        callback: Callback<Boolean>
//    ) {  // -> 회원가입 메소드
//        Log.d(" id and password", "$id, $pw")
//
//        if (id.isEmpty() || pw.isEmpty() || confirm.isEmpty() || name.isEmpty() || studentId.isEmpty() || major.isEmpty() || birth.isEmpty() || gender.isEmpty()) {
//            callback.onCallback(false)
//            Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
//        } else {
//            Log.d("LOG", "회원가입 실행중입니다.")
//            firebaseAuth.createUserWithEmailAndPassword(id, pw)
//                .addOnCompleteListener(activity) {
//                    if (it.isSuccessful) {
//                        val user = firebaseAuth.currentUser
//                        callback.onCallback(true)
//                        Log.d("createEmail: ", "Sign up Successful")//가입성공
//                    } else {
//                        Log.d("createEmail: ", "Sign up Failed")//가입실패
//                        callback.onCallback(false)
//                        Toast.makeText(activity, "회원가입 실패: 다시 확인해주세요", Toast.LENGTH_LONG).show()
//                    }
//                }
//        }
//
//        Log.d("end if , else", ": 조건문 처리 종료")
//        Log.d("return chekck ", ": 체킹 값 출력")
//    }
    //정보 기입 필요없는 메소드 ( 테스트시 사용)
    fun createEmail(
        id: String,
        pw: String,
        confirm: String?,
        name: String?,
        studentId: String?,
        major: String?,
        birth: String?,
        gender: String?,
        activity: Activity,
        callback: Callback<Boolean>
    ) {  // -> 회원가입 메소드
        Log.d(" id and password", "$id, $pw")
        database = Firebase.database.getReference("user")
        if (id.isEmpty() || pw.isEmpty()) {
            callback.onCallback(false)
            Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
        } else {
            Log.d("LOG", "회원가입 실행중입니다.")
            firebaseAuth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userdata = UserData(id, pw)
                        database.child(user.uid).setValue(userdata)
                        callback.onCallback(true)
                        Log.d("createEmail: ", "Sign up Successful")//가입성공
                    } else {
                        Log.d("createEmail: ", "Sign up Failed")//가입실패
                        callback.onCallback(false)
                        Toast.makeText(activity, "회원가입 실패: 다시 확인해주세요", Toast.LENGTH_LONG).show()
                    }
                }
        }

        Log.d("end if , else", ": 조건문 처리 종료")
        Log.d("return chekck ", ": 체킹 값 출력")
    }

    fun loginEmail(
        id: String,
        pw: String,
        activity: Activity,
        callback: Callback<Boolean>
    ) { // -> 로그인 메소드
        firebaseAuth!!.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(activity) {
                if (it.isSuccessful) {
                    Log.d("loginEmail : ", "Login Success") //로그인 성공
                    val user = firebaseAuth?.currentUser
                    callback.onCallback(true)
                } else {
                    Log.d("loginEmail : ", "Login Failed") //로그인 실패
                    Toast.makeText(activity, "아이디와 비밀번호를 확인해주세요.", Toast.LENGTH_LONG).show()
                    callback.onCallback(false)
                }
            }
    }

    //    fun makeRoom(userId: String){
//        database=Firebase.database.getReference("user")
//
//        database=Firebase.database.getReference("chatRoom")
//        database.setValue()
//    }
    fun sendMessage(messageId: Int, message: String, time: Int, userId: String) {
        val chat_message = ChatMessage(message, time, userId)
        database = Firebase.database.getReference("chatRoom")
        database.child(messageId.toString()).setValue(chat_message)
    }
}