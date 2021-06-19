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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.database.entity.*
import com.mju.mtmi.util.AES128
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.collections.ArrayList

object FirebaseManager {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "로그"

    // firebase database path
    const val USER_PATH = "user"
    const val USER_PROFILE_IMAGES_PATH = "userProfileImageUrl"
    const val BOARD_PATH = "board"
    const val CHATTING_ROOM_PATH = "chattingRoom"
    const val CHATTING_MESSAGE_PATH = "chattingMessage"
    const val COMMENT_COUNT_PATH = "commentCount"
    const val COMMENT_PATH = "comment"
    const val LAST_CHATTING_PATH = "lastChatting"


    // 회원가입
    fun postNewAccount(
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
        this.firebaseAuth.createUserWithEmailAndPassword(id, AES128.encrypt(pw))
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
                    FirebaseFirestore.getInstance().collection("users")
                        .document(firebaseAuth.currentUser!!.uid).set(userData)

                    Log.d("로그", "FirebaseManager -createEmail() called / 회원 가입 성공")
                    dataBaseCallback.onCallback(true)
                } else {
                    dataBaseCallback.onCallback(false)
                    Log.d("로그", "FirebaseManager -createEmail() called / 회원 가입 성공")
                }
            }
    }

    // 로그인
    fun login(
        id: String,
        pw: String,
        activity: Activity,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) { // -> 로그인 메소드
        if (id.isEmpty() || pw.isEmpty()) {
            dataBaseCallback.onCallback(false)
        } else {
            this.firebaseAuth.signInWithEmailAndPassword(id, AES128.encrypt(pw))
                .addOnCompleteListener(activity) {
                    if (it.isSuccessful) {
                        dataBaseCallback.onCallback(true)
                    } else {
                        dataBaseCallback.onCallback(false)
                    }
                }
        }
    }

    // 사용자 정보 가져오기
    fun getUserData(userUid: String, dataBaseCallback: DataBaseCallback<UserData>) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(userUid)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    Log.d(
                        TAG,
                        "FireStoreManager -getUserData() called / Success to get UserData / userData: $document"
                    )
                    dataBaseCallback.onCallback(document.toObject(UserData::class.java)!!)
                }
            }
            .addOnFailureListener { exception ->
                Log.d(
                    TAG,
                    "FireStoreManager -getUserData() called / Failure to get UserData: $exception"
                )
            }
    }

    fun patchUserData(
        isImageChanged: Boolean,
        userData: UserData,
        dataBaseCallback: DataBaseCallback<Boolean>
    ) {
        val currentUid = firebaseAuth.currentUser!!.uid
        val imageFileName = "IMAGE_$currentUid.png" // 파이어베이스에 저장될 이미지 파일 이름
        val filePath = userData.userProfileImageUrl.toUri()
        userData.userProfileImageUrl = imageFileName

        FirebaseFirestore.getInstance().collection("users")
            .document(currentUid)
            .set(userData, SetOptions.merge())

        if (isImageChanged) { // 이미지가 변경됐을 때, 이미지 저장 (이미지 데이터 크기가 크므로 업데이트 완료 순간을 텍스트 데이터 보다는
            // 이미지 데이터로 완료 시점을 잡는 게 좋다.
            FirebaseStorage.getInstance().reference.child("user_profile_images/")
                .child(imageFileName)
                .putFile(filePath) // 이미지 storage에 저장
                .addOnSuccessListener { dataBaseCallback.onCallback(true) }
                .addOnFailureListener { dataBaseCallback.onCallback(false) }
        }

        // 유저 데이터 업데이트
        SharedPrefManager.setUserData(userData)
    }

    // 새로운 채팅방 만들기
    fun postNewChattingRoom(
        sendUser: String,
        receiveUser: String,
        chatRoomId: String
    ) { //채팅방 ID 생성 및 단일화
        val database = Firebase.database.getReference(this.CHATTING_ROOM_PATH)
        database.child(chatRoomId).setValue(ChattingRoom(chatRoomId, arrayListOf(sendUser, receiveUser)))
    }

    // 채팅방 중복 찾기
    fun checkRedundantChattingRoom(
        chattingRoomId: String,
        dataBaseCallback: DataBaseCallback<Boolean>
    ) {
        val database = Firebase.database.getReference(this.CHATTING_ROOM_PATH)
        var check = true
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.forEach() {
                    val existingChattingRoomId: String = it.key as String
                    if (existingChattingRoomId == chattingRoomId) {
                        check = false
                    }
                }
                dataBaseCallback.onCallback(check)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // 새로운 채팅 메시지 보내기
    fun postNewChattingMessage(
        chattingRoomId: String,
        name: String,
        message: String,
        userId: String,
        imageUri: String,
    ) {
        //밀리초 단위로 메시지 푸쉬 -> 키 값으로 사용
        val current = LocalDateTime.now() //현재 시간
        val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
        val uiFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
        var formatted = current.format(uiFormatter)

        val newChattingMessage =
            ChattingMessage(chattingRoomId, name, userId, message, imageUri, formatted) //메세지내용 전
        val lastChattingMessage = LastChatting(message = message, timeStamp = formatted)
        val database = Firebase.database.getReference(this.CHATTING_ROOM_PATH)
        formatted = current.format(dbSaveFormatter)

        database.child(chattingRoomId).child(this.CHATTING_MESSAGE_PATH).child(formatted)
            .setValue(newChattingMessage) // 메시지 데이터 post
        database.child(chattingRoomId).child(this.LAST_CHATTING_PATH).setValue(lastChattingMessage) // 마지막 메시지 데이터 post
    }

    // 새 게시글 작성
    fun postNewPost(
        subjectCode: String,
        userId: String,
        postTitle: String,
        postContent: String,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) {
        getUserData(userId, object : DataBaseCallback<UserData> {
            override fun onCallback(data: UserData) {
                val currentTime = LocalDateTime.now() //현재 시간
                val formattedTime =
                    currentTime.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분"))
                val dbSaveFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS") //밀리초 환산
                val formatted2 = currentTime.format(dbSaveFormatter)
                val saveIdx = (Long.MAX_VALUE - formatted2.toLong()).toString() // 역순출력처리
                val boardPost =
                    BoardPost(
                        subjectCode,
                        postTitle,
                        formattedTime,
                        postContent,
                        userId,
                        data.userName,
                        saveIdx,
                        0
                    )
                Firebase.database.getReference(this@FirebaseManager.BOARD_PATH)
                    .child(subjectCode)
                    .child(saveIdx)
                    .setValue(boardPost)

                dataBaseCallback.onCallback(true)
            }
        })
    }

    // 사용자 프로필이미지 url 가져오기
    fun getUserDataImageUri(userUid: String, dataBaseCallback: DataBaseCallback<String>) {
        Firebase.database.getReference(this.USER_PATH).child(userUid)
            .child(this.USER_PROFILE_IMAGES_PATH)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profileImageUrl: String = snapshot.value as String
                    dataBaseCallback.onCallback(profileImageUrl)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // 게시글 리스트 가져오기
    fun getListOfPosts(
        idx: String,
        dataBaseCallback: DataBaseCallback<ArrayList<BoardPost>>
    ) { // 과목별시판 불러오기
        Firebase.database.getReference("/${this.BOARD_PATH}/$idx")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val postList = ArrayList<BoardPost>()
                    dataSnapshot.children.forEach {
                        val post = it.getValue(BoardPost::class.java)
                        if (post != null) {
                            postList.add(post)
                        }
                    }
                    dataBaseCallback.onCallback(postList)
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
    }

    // 댓글 개수 업데이트
    fun patchCommentCount(boardPost: BoardPost) {
        boardPost.commentCount = boardPost.commentCount.plus(1)
        boardPost.subjectBoardIndex.let {
            boardPost.subjectCode.let { it1 ->
                Firebase.database.reference.child(this.BOARD_PATH)
                    .child(it1)
                    .child(it)
                    .child(this.COMMENT_COUNT_PATH).setValue(boardPost.commentCount)
            }
        }
    }

    // 새로운 댓글 추가
    fun postNewComment(
        subjectCode: String?,
        subjectIdx: String?,
        commentContent: String,
        commenterUid: String,
        dataBaseCallback: DataBaseCallback<Boolean>,
    ) {
        getUserData(commenterUid, object : DataBaseCallback<UserData> {
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
                    val database = Firebase.database.getReference(this@FirebaseManager.BOARD_PATH)

                    database.child(subjectCode).child(subjectIdx)
                        .child(this@FirebaseManager.COMMENT_PATH)
                        .child(
                            formatted2 // 정순 출력 처리
                        ).setValue(commentData)
                    dataBaseCallback.onCallback(true)
                }
            }
        })
    }

    // 댓글 개수 가져오기
    fun getNumOfComment(
        subjectCode: String?,
        subjectBoardIdx: String?,
        dataBaseCallback: DataBaseCallback<ArrayList<BoardComment>>,
    ) {
        Firebase.database.getReference("/${this.BOARD_PATH}/$subjectCode/$subjectBoardIdx/${this.COMMENT_PATH}")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val commentList = ArrayList<BoardComment>()
                    snapshot.children.forEach {
                        val comment = it.getValue(BoardComment::class.java)
                        if (comment != null) {
                            commentList.add(comment)
                        }
                    }
                    dataBaseCallback.onCallback(commentList)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // 내 모든 채팅방 리스트 가져오기
    fun getMyChattingRoomList(
        userId: String,
        dataBaseCallback: DataBaseCallback<ArrayList<ChattingRoomListForm>>
    ) {
        Firebase.database.getReference(this.CHATTING_ROOM_PATH)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chattingList = ArrayList<ChattingRoomListForm>()
                    snapshot.children.forEach {
                        if (it.key.toString().contains(userId)) {
                            val chattingListForm = it.getValue(ChattingRoomListForm::class.java)
                            if (chattingListForm != null) {
                                chattingList.add(chattingListForm)
                            }
                        }
                    }
                    dataBaseCallback.onCallback(chattingList)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }

    // 마지막 채팅 메시지 가져오기
    fun getLastChattingMessage(
        chattingRoomId: String,
        dataBaseCallback: DataBaseCallback<LastChatting>
    ) {
        val database =
            Firebase.database.getReference("${this.CHATTING_ROOM_PATH}/$chattingRoomId/${this.LAST_CHATTING_PATH}")
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val lastChatting: LastChatting? = snapshot.getValue<LastChatting>()
                if (lastChatting != null) {
                    dataBaseCallback.onCallback(lastChatting)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

}