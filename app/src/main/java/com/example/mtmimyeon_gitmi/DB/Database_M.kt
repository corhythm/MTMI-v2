package com.example.mtmimyeon_gitmi.DB

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth

class Database_M {
    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun createEmail(id: String, pw: String, activity: Activity): Boolean {  // -> 회원가입 메소드
        var check = true
        Log.d(" id and password", id + " , " + pw)
        if (id.length == 0 || pw.length == 0) {
            check = false
            Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
        } else {
            firebaseAuth!!.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val user = firebaseAuth?.currentUser
                        Log.d("createEmail : ", "Sign up Successful")//가입성공
                    } else {
                        Log.d("createEmail : ", "Sign up Failed")//가입실패
                        check = false
                    }
                }
        }
        return check
    }

    fun loginEmail(id: String, pw: String, activity: Activity): Boolean { // -> 로그인 메소드
        var check = true
        firebaseAuth!!.signInWithEmailAndPassword(id, pw)
            .addOnCompleteListener(activity) {
                if (it.isSuccessful) {
                    Log.d("loginEmail : ", "Login Success") //로그인 성공
                    val user = firebaseAuth?.currentUser

                } else {
                    Log.d("loginEmail : ", "Login Failed") //로그인 실패
                    check = false
                }
            }
        return check
    }
}