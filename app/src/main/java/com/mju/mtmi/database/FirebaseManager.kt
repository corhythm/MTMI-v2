package com.mju.mtmi.database

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.mju.mtmi.account.SignUpActivity
import com.mju.mtmi.util.SharedPrefManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.util.AES128
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

class FirebaseManager {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var database: DatabaseReference

    // 회원가입 메소드 ( 정보 기입 필요한 )
    fun createEmail(
        id: String,
        pw: String,
        name: String,
        studentId: String,
        major: String,
        birth: String,
        gender: String,
        context: Context,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) {  // -> 회원가입 메소드
        Log.d("로그", "FirebaseManager -createEmail() called / currentUser = ${firebaseAuth.currentUser!!.uid}")
        firebaseAuth.createUserWithEmailAndPassword(id, AES128.encrypt(pw))
            .addOnCompleteListener(context as SignUpActivity) { task ->
                if (task.isSuccessful) {
                    val profileImgUrl = if (gender == "남성")
                        "default_man_profile_image.png"
                    else
                        "default_woman_profile_image.png"
                    val userData =
                        UserData(
                            id,
                            AES128.encrypt(pw),
                            studentId,
                            name,
                            birth,
                            gender,
                            major,
                            profileImgUrl
                        )
                    Firebase.database.getReference("user")
                        .child(firebaseAuth.currentUser!!.uid)
                        .setValue(userData)

                    Log.d("로그", "FirebaseManager -createEmail() called / 회원 가입 성공")
                    dataBaseCallback.onCallback(true)
                } else {
                    dataBaseCallback.onCallback(false)
                    Log.d("로그", "FirebaseManager -createEmail() called / 회원 가입 성공")
                }
            }
    }

    fun loginEmail(
        id: String,
        pw: String,
        activity: Activity,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) { // -> 로그인 메소드
        if (id.isEmpty() || pw.isEmpty()) {
            dataBaseCallback.onCallback(false)
        } else {
            Log.d("로그", "FirebaseManager -loginEmail() called / ${AES128.encrypt(pw)}")
            firebaseAuth.signInWithEmailAndPassword(id, AES128.encrypt(pw))
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        dataBaseCallback.onCallback(true)
                    } else {
                        dataBaseCallback.onCallback(false)
                    }
                }
        }
    }

    fun makeChatRoom(sendUser: String, receiveUser: String, chatRoomId: String) { //채팅방 ID 생성 및 단일화
        database = Firebase.database.getReference("chat")
        database.child(chatRoomId).setValue(Chat(chatRoomId, sendUser, receiveUser))
    }

    //     채팅 중복 찾기
    fun checkChat(chatRoomId: String, dataBaseCallback: DataBaseCallback<Boolean>) {
        database = Firebase.database.getReference("chat")
        var check = true
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val chatId: String = it.key as String
                    if (chatId == chatRoomId) {
                        check = false
                    }
                }
                dataBaseCallback.onCallback(check)
            }

            override fun onCancelled(error: DatabaseError) {}
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
        val current = LocalDateTime.now() //현재 시간
        val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
        val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        var formatted = current.format(uiFormatter)
        val chatMessage =
            ChatMessage(chatRoomId, name, userId, message, imageUri, formatted) //메세지내용 전달
        val chatListForm =
            ChatListForm("image/IMAGE_$userId.png", name, message, formatted, chatRoomId)
        database = Firebase.database.getReference("chat") //chat reference
        formatted = current.format(dbSaveFormatter)
        database.child(chatRoomId).child("chatting").child(formatted).setValue(chatMessage) //db저장
        database.child(chatRoomId).child("lastChat").setValue(chatListForm)
    }

    fun writePost(
        idx: String,
        userId: String,
        postTitle: String,
        postContent: String,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) {
        callUserData(userId, object : DataBaseCallback<UserData> {
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
                dataBaseCallback.onCallback(true)
            }
        })
    }

    fun callUserData(userUid: String, dataBaseCallback: DataBaseCallback<UserData>) {
        Firebase.database.getReference("user").child(userUid)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val userData: UserData? = dataSnapshot.getValue<UserData>()
                    if (userData != null) {
                        Log.d("불러온 유저데이터이름", userData.userName)
                    }
                    if (userData != null) {
                        dataBaseCallback.onCallback(userData)
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.d("게시물 가져오기실패 :. ", "게시물을 가져오기 실패")
                }
            })
    }

    fun callUserDataImageUri(userUid: String, dataBaseCallback: DataBaseCallback<String>) {
        Firebase.database.getReference("user").child(userUid).child("userProfileImageUrl")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl: String = snapshot.value as String
                    dataBaseCallback.onCallback(profileImageUrl)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("사진 Url 불러오는중", "오류")
                }

            })
    }

    fun loadPostList(
        idx: String,
        dataBaseCallback: DataBaseCallback<ArrayList<BoardPost>>
    ) { // 과목별시판 불러오기
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
                    dataBaseCallback.onCallback(postList)
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
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) {
        callUserData(commenterUid, object : DataBaseCallback<UserData> {
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
                        .child(
                            formatted2 // 정순 출력 처리
                        ).setValue(commentData)
                    dataBaseCallback.onCallback(true)
                } else {
                    Log.d("해당 과목이 존재하지 않습니다.", "error")
                }
            }
        })
    }

    fun loadPostComment(
        subjectCode: String?,
        subjectBoardIdx: String?,
        dataBaseCallback: DataBaseCallback<ArrayList<BoardComment>>,
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
                    dataBaseCallback.onCallback(commentList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.d("comment", "not data")
                }
            })
    }

    fun loadChatList(userId: String, dataBaseCallback: DataBaseCallback<ArrayList<Chat>>) {
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
                dataBaseCallback.onCallback(chatList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방에러", "채팅방 오류")
            }
        })
    }

    fun loadLastChat(chatRoomId: String, dataBaseCallback: DataBaseCallback<ChatListForm>) {
        database = Firebase.database.getReference("chat/$chatRoomId/lastChat")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChat: ChatListForm? = snapshot.getValue<ChatListForm>()
                if (lastChat != null) {
                    dataBaseCallback.onCallback(lastChat)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("채팅방 리스트", "error")
            }

        })
    }

    fun editUserData(
        isImageChanged: Boolean,
        userData: UserData,
        dataBaseCallback: DataBaseCallback<Boolean>
    ) {
        val userUid = firebaseAuth.currentUser!!.uid
        val imageFileName = "IMAGE_$userUid.png" // 파이어베이스에 저장될 이미지 파일 이름
        val filePath = userData.userProfileImageUrl.toUri()
        userData.userProfileImageUrl = imageFileName

        if (isImageChanged) { // 이미지가 변경됐을 때
            FirebaseDatabase.getInstance().reference.child("user").child(userUid).setValue(userData)
            FirebaseStorage.getInstance().reference.child("image/").child(imageFileName)
                .putFile(filePath)
                .addOnSuccessListener { dataBaseCallback.onCallback(true) }
                .addOnFailureListener { dataBaseCallback.onCallback(false) }
        } else { // 이미지가 변경되지 않았을 때
            FirebaseDatabase.getInstance().reference.child("user").child(userUid).setValue(userData)
                .addOnSuccessListener { dataBaseCallback.onCallback(true) }
                .addOnFailureListener { dataBaseCallback.onCallback(false) }
        }

        // 유저 데이터 업데이트
        SharedPrefManager.setUserData(userData)


    }

}