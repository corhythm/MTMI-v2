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
import com.mju.mtmi.database.entity.UserData
import com.mju.mtmi.util.AES128
import com.mju.mtmi.util.SharedPrefManager

object FireStoreManager {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val TAG = "로그"

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


}