package com.example.mtmimyeon_gitmi.db

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.core.net.toUri
import com.example.mtmimyeon_gitmi.account.SignUpActivity
import com.example.mtmimyeon_gitmi.util.SharedPrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class DatabaseManager {

    private var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference
    private var storageRef = FirebaseStorage.getInstance()
    private var storage = Firebase.storage

    //      회원가입 메소드 ( 정보 기입 필요한 )
    fun createEmail(
        id: String,
        pw: String,
        name: String,
        studentId: String,
        major: String,
        birth: String,
        gender: String,
        context: Context,
        callback: Callback<Boolean>,
    ) {  // -> 회원가입 메소드
        database = Firebase.database.getReference("user")

        firebaseAuth.createUserWithEmailAndPassword(id, pw)
            .addOnCompleteListener(context as SignUpActivity) {
                if (it.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    val userdata =
                        UserData(id, pw, studentId, name, birth, gender, major, "empty")
                    database.child(user.uid).setValue(userdata)
                    callback.onCallback(true)
                    Log.d("createEmail: ", "Sign up Successful")//가입성공
                } else {
                    Log.d("createEmail: ", "Sign up Failed")//가입실패
                    callback.onCallback(false)
                }
            }
    }

    fun loginEmail(
        id: String,
        pw: String,
        activity: Activity,
        callback: Callback<Boolean>,
    ) { // -> 로그인 메소드
        if (id.isEmpty() || pw.isEmpty()) {
            callback.onCallback(false)
        } else {
            firebaseAuth.signInWithEmailAndPassword(id, pw)
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        val user = firebaseAuth.currentUser
                        callback.onCallback(true)
                    } else {
                        callback.onCallback(false)
                    }
                }
        }
    }

    fun makeChatRoom(sendUser: String, receiveUser: String, chatRoomId: String) { //채팅방 ID 생성 및 단일화
        database = Firebase.database.getReference("chat")
        var newChat = Chat(chatRoomId, sendUser, receiveUser)
        database.child(chatRoomId).setValue(newChat)
    }
//    fun callChatRoomData(roomId: String,callback: Callback<Chat>){
//       database = Firebase.database.getReference("chat").child(roomId)
//       database.addListenerForSingleValueEvent(object : ValueEventListener{
//           override fun onDataChange(snapshot: DataSnapshot) {
//               var roomData: Chat? = snapshot.getValue<Chat>()
//               if(roomData != null){
//                   callback.onCallback(roomData)
//               }else{
//                   Log.d("callChatRoomData","오류")
//               }
//           }
//
//           override fun onCancelled(error: DatabaseError) {
//               Log.d("방정보","없음")
//           }
//       })
//    }

    //     채팅 중복 찾기
    fun checkChat(chatRoomId: String, callback: Callback<Boolean>) {
        database = Firebase.database.getReference("chat")
        var check = true
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val chatId: String = it.key as String
                    Log.d("가져온 키", chatId)
                    if (chatId == chatRoomId) {
                        check = false
                    }
                }
                callback.onCallback(check)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방에러", "채팅방 오류")
            }
        })
    }

    fun sendMessage(
        chatRoomId: String,
        name: String,
        message: String,
        userId: String,
        imageUri: String,
    ) {
        //밀리초 단위로 메시지 푸쉬
        val current = LocalDateTime.now() //현재사간
        val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
        val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        var formatted = current.format(uiFormatter)
        val chat_message =
            ChatMessage(chatRoomId, name, userId, message, imageUri, formatted) //메세지내용 전달
        val chatListForm =
            ChatListForm("image/IMAGE_$userId.png", name, message, formatted, chatRoomId)
        database = Firebase.database.getReference("chat") //chat reference
        formatted = current.format(dbSaveFormatter)
        database.child(chatRoomId).child("chatting").child(formatted).setValue(chat_message) //db저장
        database.child(chatRoomId).child("lastChat").setValue(chatListForm)
    }

    fun writePost(
        idx: String,
        userId: String,
        postTitle: String,
        postContent: String,
        callback: Callback<Boolean>,
    ) {
        callUserData(userId, object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                val current = LocalDateTime.now() //현재사간
                val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                val formatted = current.format(uiFormatter)
                val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
                val formatted2 = current.format(dbSaveFormatter)
                val saveIdx = (99999999999999999 - formatted2.toLong()).toString() // 역순출력처리
                database = Firebase.database.getReference("board")
                val boardIdx = idx
                val boardPost =
                    BoardPost(
                        idx,
                        postTitle,
                        formatted,
                        postContent,
                        userId,
                        data.userName,
                        saveIdx,
                        0
                    ) //테스터 대신 userName 넣어야함. data.userName
                database.child(boardIdx).child(saveIdx).setValue(boardPost)
                callback.onCallback(true)
            }
        })
    }

    fun callUserData(userUid: String, callback: Callback<UserData>) {
        Firebase.database.getReference("user").child(userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userData: UserData? = dataSnapshot.getValue<UserData>()
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

    fun callUserDataImageUri(userUid: String, callback: Callback<String>) {
        Firebase.database.getReference("user").child(userUid).child("userProfileImageUrl")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl: String = snapshot.value as String
                    callback.onCallback(profileImageUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("사진 Url 불러오는중", "오류")
                }

            })
    }

    fun loadPostList(idx: String, callback: Callback<ArrayList<BoardPost>>) { // 과목별시판 불러오기
        Firebase.database.getReference("/board/$idx")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val postList = ArrayList<BoardPost>()
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

    fun postViewCount(boardPost: BoardPost) {
        boardPost.view = boardPost.view?.plus(1)
        boardPost.subjectBoardIndex?.let {
            boardPost.subjectCode?.let { it1 ->
                Firebase.database.reference.child("board")
                    .child(it1)
                    .child(it)
                    .child("view").setValue(boardPost.view)
            }
        }
    }

    fun postLeaveComment(
        subjectCode: String?,
        subjectIdx: String?,
        commentContent: String,
        commenterUid: String,
        callback: Callback<Boolean>,
    ) {
        callUserData(commenterUid, object : Callback<UserData> {
            override fun onCallback(data: UserData) {
                val current = LocalDateTime.now() //현재시간
                val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                val formatted = current.format(uiFormatter)
                val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
                val formatted2 = current.format(dbSaveFormatter)
                if (subjectCode != null && subjectIdx != null) {
                    val commentData = BoardComment(
                        subjectIdx,
                        commenterUid,
                        data.userName,
                        formatted,
                        commentContent
                    )
                    database = Firebase.database.getReference("board")

                    database.child(subjectCode).child(subjectIdx).child("comment")
                        .child(formatted2 // 정순 출력 처리
                        ).setValue(commentData)
                    callback.onCallback(true)
                } else {
                    Log.d("해당 과목이 존재하지 않습니다.", "error")
                }
            }
        })
    }

    fun loadPostComment(
        subjectCode: String?,
        subjectBoardIdx: String?,
        callback: Callback<ArrayList<BoardComment>>,
    ) {
        Log.d("loadPostComment", "실행중")
        Firebase.database.getReference("/board/$subjectCode/$subjectBoardIdx/comment")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentList = ArrayList<BoardComment>()
                    snapshot.children.forEach {
                        val comment = it.getValue(BoardComment::class.java)
                        Log.d("코멘트", comment.toString())
                        if (comment != null) {
                            Log.d("코멘트 리스트", comment.content)
                            commentList.add(comment)
                        }
                    }
                    Log.d("loadPostComment", "callback")
                    callback.onCallback(commentList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("comment", "not data")
                }
            })
    }

    fun loadChatList(userId: String, callback: Callback<ArrayList<Chat>>) {
        database = Firebase.database.getReference("chat")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val chatList = ArrayList<Chat>()
                snapshot.children.forEach() {
                    if (it.key.toString().contains(userId)) {
                        val chat = it.getValue(Chat::class.java)
                        if (chat != null) {
                            chatList.add(chat)
                        }
                    }
                }
                callback.onCallback(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방에러", "채팅방 오류")
            }
        })
    }

    fun loadLastChat(chatRoomId: String, callback: Callback<ChatListForm>) {
        database = Firebase.database.getReference("chat/$chatRoomId/lastChat")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChat: ChatListForm? = snapshot.getValue<ChatListForm>()
                if (lastChat != null) {
                    callback.onCallback(lastChat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방 리스트", "error")
            }

        })
    }

    fun editUserData(isImageChanged: Boolean, userData: UserData, callback: Callback<Boolean>) {
        val userUid = firebaseAuth.currentUser.uid
        val imageFileName = "IMAGE_$userUid.png" // 파이어베이스에 저장될 이미지 파일 이름
        val filePath = userData.userProfileImageUrl.toUri()
        userData.userProfileImageUrl = imageFileName

        if (isImageChanged) { // 이미지가 변경됐을 때
            FirebaseDatabase.getInstance().reference.child("user").child(userUid).setValue(userData)
            FirebaseStorage.getInstance().reference.child("image/").child(imageFileName)
                .putFile(filePath)
                .addOnSuccessListener { callback.onCallback(true) }
                .addOnFailureListener { callback.onCallback(false) }
        } else { // 이미지가 변경되지 않았을 때
            FirebaseDatabase.getInstance().reference.child("user").child(userUid).setValue(userData)
                .addOnSuccessListener { callback.onCallback(true) }
                .addOnFailureListener { callback.onCallback(false) }
        }

        // 유저 데이터 업데이트
        SharedPrefManager.setUserData(userData)


    }

}