package com.example.mtmimyeon_gitmi.db

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
            Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
            callback.onCallback(false)

        } else {
            Log.d("LOG", "회원가입 실행중입니다.")
            firebaseAuth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userdata = UserData(id, pw,null)
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
        if(id.isEmpty()|| pw.isEmpty()){
            callback.onCallback(false)
        }
        else {
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
    }
    fun writePost(subjectCode: String,subjectBoard: String,title: String,day: String,content: String,writerUid: String){
        var userUid=firebaseAuth.currentUser.uid
//        var
//        var board=BoardSubject(subjectCode,title,day,content
        database = Firebase.database.getReference("board")
//        database.child(subjectBoard).
//        database.child("subjectBoard").
    }

    //    fun makeRoom(userId: String){
//        database=Firebase.database.getReference("user")
//
//        database=Firebase.database.getReference("chatRoom")
//        database.setValue()
//    }
    fun makeChatRoom(sendUser: String, receiveUser: String): String {
        var chatRoomId = sendUser + "-" + receiveUser
        database = Firebase.database.getReference("chat")
        var newChat = Chat(chatRoomId, sendUser, receiveUser)
        database.child(newChat.chatRoomId).setValue(newChat)
        return chatRoomId
    }

    fun sendMessage(
        chatRoomId: String,
        name: String,
        message: String,
        userId: String,
        imageUri: String
    ) {
        //밀리초 단위로 메시지 푸쉬
        val current = LocalDateTime.now() //현재사간
        val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
        val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        var formatted = current.format(uiFormatter)
        val chat_message =
            ChatMessage(chatRoomId, name, userId, message, imageUri, formatted) //메세지내용 전달
        database = Firebase.database.getReference("chat") //chat reference
        formatted = current.format(dbSaveFormatter)
        database.child(chatRoomId).child("chatting").child(formatted).setValue(chat_message) //db저장
    }

    fun writePost(
        idx: String,
        subjectName: String,
        userId: String,
        postTitle: String,
        postContent: String
    ) {
        val current = LocalDateTime.now() //현재사간
        val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        var formatted = current.format(uiFormatter)
        val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
        var formatted2 = current.format(dbSaveFormatter)
        database = Firebase.database.getReference("board")
        var boardIdx = Board(idx)
        var boardPost = BoardPost(idx, postTitle,formatted,postContent,userId,formatted2)

        database.child(boardIdx.subjectCode).child(formatted).setValue(boardPost)
    }
}