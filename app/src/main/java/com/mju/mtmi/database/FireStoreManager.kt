package com.mju.mtmi.database

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.core.net.toUri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.mju.mtmi.account.SignUpActivity
import com.mju.mtmi.database.entity.BoardPost
import com.mju.mtmi.database.entity.UserData
import com.mju.mtmi.util.AES128
import com.mju.mtmi.util.SharedPrefManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object FireStoreManager {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "로그"



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
                val boardIdx = (Long.MAX_VALUE - formatted2.toLong()).toString() // 역순출력처리
                val boardPost =
                    BoardPost(
                        subjectCode = subjectCode,
                        title = postTitle,
                        timeStamp = formattedTime,
                        content = postContent,
                        writerUid = userId,
                        commentCount = 0
                    )

                FirebaseFirestore.getInstance()
                    .collection("boards")
                    .document(subjectCode)
                    .collection(boardIdx)
                    .document(boardIdx)
                    .set(boardPost)
                    .addOnSuccessListener { dataBaseCallback.onCallback(true) }
                    .addOnFailureListener {  dataBaseCallback.onCallback(false) }
            }
        })
    }



}