package com.example.mtmimyeon_gitmi.db

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class DatabaseManager {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    //      회원가입 메소드 ( 정보 기입 필요한 )
    fun createEmail(
        id: String,
        pw: String,
        confirm: String,
        name: String,
        studentId: String,
        major: String,
        birth: String,
        gender: String,
        activity: Activity,
        callback: Callback<Boolean>
    ) {  // -> 회원가입 메소드
        Log.d(" id and password", "$id, $pw")
        database = Firebase.database.getReference("user")
        if (id.isEmpty() || pw.isEmpty() || confirm.isEmpty() || name.isEmpty() || studentId.isEmpty() || major.isEmpty() || birth.isEmpty() || gender.isEmpty()) {
            callback.onCallback(false)
            Toast.makeText(activity, "아이디 혹은 비밀번호를 입력해주세요.", Toast.LENGTH_LONG).show()
        } else if (pw != confirm) {
            callback.onCallback(false)
            Toast.makeText(activity, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_LONG).show()

        } else {
            Log.d("LOG", "회원가입 실행중입니다.")
            firebaseAuth.createUserWithEmailAndPassword(id, pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        val userdata = UserData(id, pw, studentId, name, birth, gender, major, "")
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
        if (id.isEmpty() || pw.isEmpty()) {
            callback.onCallback(false)
        } else {
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

    fun makeChatRoom(sendUser:String,receiveUser:String,chatRoomId: String) { //채팅방 ID 생성 및 단일화
        database = Firebase.database.getReference("chat")
        var newChat = Chat(chatRoomId, sendUser, receiveUser)
        database.child(chatRoomId).setValue(newChat)
    }

//     채팅 중복 찾기
    fun checkChat(chatRoomId: String,callback: Callback<Boolean>){
        database = Firebase.database.getReference("chat")
        var check = true
        database.addListenerForSingleValueEvent(object :  ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach(){
                    var chatId:String = it.key as String
                    Log.d("가져온 키",chatId)
                    if(chatId == chatRoomId){
                        check = false
                    }
                }
                callback.onCallback(check)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방에러","채팅방 오류")
            }
        })
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
        callUserData(userId, object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                if (data != null) {
                    val current = LocalDateTime.now() //현재사간
                    val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                    var formatted = current.format(uiFormatter)
                    val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
                    var formatted2 = current.format(dbSaveFormatter)
                    val saveIdx = (99999999999999999 - formatted2.toLong()).toString() // 역순출력처리
                    database = Firebase.database.getReference("board")
                    var boardIdx = idx
                    var boardPost =
                        BoardPost(
                            idx,
                            postTitle,
                            formatted,
                            postContent,
                            userId,
                            data.userName,
                            saveIdx
                        ) //테스터 대신 userName 넣어야함. data.userName
                    database.child(boardIdx).child(saveIdx).setValue(boardPost)
                }
            }
        })
    }

    private fun callUserData(userUid: String, callback: Callback<UserData>) {
        Firebase.database.getReference("user").child(userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var userData: UserData? = dataSnapshot.getValue<UserData>()
                    if (userData != null) {
                        Log.d("불러온 유저데이터이름", userData.userName)
                    }
                    if (userData != null) {
                        callback.onCallback(userData)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("게시물 가져오기실패 :. ", "게시물을 가져오기 실패")
                }
            })
    }

    fun loadPostList(idx: String, callback: Callback<ArrayList<BoardPost>>) { // 과목별시판 불러오기
        Firebase.database.getReference("/board/$idx")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var postList = ArrayList<BoardPost>()
                    dataSnapshot.children.forEach {
                        Log.d("new", it.toString())
                        Log.d("key", it.key.toString()) // 이건좋음
                        Log.d("value", it.value.toString())
                        val post = it.getValue(BoardPost::class.java)
                        if (post != null) {
                            postList.add(post)
                        }
                    }
                    callback.onCallback(postList)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("게시물 가져오기실패 :. ", "게시물을 가져오기 실패")
                }
            })
    }

    fun postLeaveComment(
        subjectCode: String?,
        subjectIdx: String?,
        commentContent: String,
        commenterUid: String
    ) {
        callUserData(commenterUid, object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                if (data != null) {
                    val current = LocalDateTime.now() //현재사간
                    val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                    var formatted = current.format(uiFormatter)
                    val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
                    var formatted2 = current.format(dbSaveFormatter)
                    val saveIdx = formatted2 // 정순 출력 처리
                    if (subjectCode != null && subjectIdx != null) {
                        var commentData = BoardComment(
                            subjectIdx,
                            commenterUid,
                            data.userName,
                            formatted,
                            commentContent
                        )
                        database = Firebase.database.getReference("board")

                        database.child(subjectCode).child(subjectIdx).child("comment").child(saveIdx).setValue(commentData)
                    } else {
                        Log.d("해달 과목이 존재하지 않습니다.", "error")
                    }
                } else {
                    Log.d("현재상태는 댓글을 달 수 없습니다.", "error")
                }
            }
        })
    }

    fun loadPostComment(subjectCode: String?,subjectBoardIdx: String?, callback: Callback<ArrayList<BoardComment>>) {
        Firebase.database.getReference("/board/$subjectCode/$subjectBoardIdx/comment").addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                var commentList = ArrayList<BoardComment>()
                snapshot.children.forEach {
                    val comment = it.getValue(BoardComment::class.java)
                    if (comment != null) {
                        commentList.add(comment)
                    }
                }
                callback.onCallback(commentList)
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun callChatList(uid: String,callback: Callback<ArrayList<String>>){
        Firebase.database.getReference("/chat",)
    }

//    fun loadPost()
}