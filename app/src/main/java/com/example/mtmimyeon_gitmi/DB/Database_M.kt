package com.example.mtmimyeon_gitmi.DB

import android.app.Activity
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.*

class Database_M {
    private var firebaseAuth : FirebaseAuth= FirebaseAuth.getInstance()

<<<<<<< Updated upstream
    fun createEmail(id:String,pw:String,activity:Activity) : Boolean {  // -> 회원가입 메소드
        var check=true
        Log.d(" id and password",id+" , "+pw)
        if(id.length==0||pw.length==0){
            check=false
            Toast.makeText(activity,"아이디 혹은 비밀번호를 입력해주세요.",Toast.LENGTH_LONG).show()
        }else{
            firebaseAuth!!.createUserWithEmailAndPassword(id,pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful){
                        val user = firebaseAuth?.currentUser
                        Log.d("createEmail : ", "Sign up Successful")//가입성공

                    } else {
                        Log.d("createEmail : ", "Sign up Failed")//가입실패
                        check=false
=======
    fun createEmail(id: String, pw: String, activity: Activity,callback: Callback<Boolean>){  // -> 회원가입 메소드
        Log.d(" id and password", "$id, $pw")
        runBlocking {
            if (id.isEmpty() || pw.isEmpty()) {
                callback.onCallback(false)
                Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
            } else {
                Log.d("LOG", "회원가입 실행중입니다.")
                firebaseAuth.createUserWithEmailAndPassword(id, pw)
                    .addOnCompleteListener(activity) {
                        if (it.isSuccessful) {
                            val user = firebaseAuth.currentUser
                            callback.onCallback(true)
                            Log.d("createEmail: ", "Sign up Successful")//가입성공
                        } else {
                            Log.d("createEmail: ", "Sign up Failed")//가입실패
                            callback.onCallback(false)
                            Toast.makeText(activity, "회원가입 오류", Toast.LENGTH_LONG).show()
                        }
>>>>>>> Stashed changes
                    }
            }
        }
        Log.d("end if , else", ": 조건문 처리 종료")
        Log.d("return chekck ", ": 체킹 값 출력")
    }
    fun loginEmail(id:String,pw:String,activity:Activity) :Boolean{ // -> 로그인 메소드
        var check=true
        firebaseAuth!!.signInWithEmailAndPassword(id,pw)
            .addOnCompleteListener(activity){
                if(it.isSuccessful){
                    Log.d("loginEmail : ","Login Success") //로그인 성공
                    val user=firebaseAuth?.currentUser

                }
                else{
                    Log.d("loginEmail : ","Login Failed") //로그인 실패
                    check=false
                }
            }
        return check
    }
}