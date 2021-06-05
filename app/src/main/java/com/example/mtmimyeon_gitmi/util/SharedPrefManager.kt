package com.example.mtmimyeon_gitmi.util

import android.content.Context
import android.util.Log
import com.example.mtmimyeon_gitmi.db.UserData
import com.example.mtmimyeon_gitmi.myClass.ItemSubjectInfo
import com.google.gson.Gson

object SharedPrefManager {
    private const val SHARED_USER_LMS_ACCOUNT = "USER_LMS_ACCOUNT"
    private const val KEY_USER_LMS_ID = "USER_LMS_ID"
    private const val KEY_USER_LMS_PW = "USER_LMS_PW"

    // lms 정보(과목, 교수, 시간, 과제 ... )
    private const val SHARED_USER_LMS_INFO = "USER_LMS_INFO"
    private const val KEY_USER_LMS_SUBJECT_INFO_LIST =
        "USER_LMS_SUBJECT_INFO_LIST" // 과목코드, 과목, 교수, 시간

    // 앱 사용 권한 (위치, 전화, 카메라 ... etc)
    private const val SHARED_PERMISSIONS = "PERMISSIONS"
    private const val KEY_PERMISSION_ACCESS_FINE_LOCATION = "PERMISSION_ACCESS_FINE_LOCATION"

    private const val SHARED_MBTI_TYPE = "MBTI_TYPE"
    private const val KEY_MY_MBTI_TYPE = "MY_MBTI_TYPE"

    private const val SHARED_USER_DATA = "USER_DATA"
    private const val KEY_USER_DATA = "USER_DATA"

    fun setUserLmsId(userId: String) {
        val shared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_ACCOUNT,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        editor.putString(KEY_USER_LMS_ID, userId) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun getUserLmsId(): String {
        val shared =
            App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)
        return shared.getString(KEY_USER_LMS_ID, "")!!
    }

    fun setUserLmsPW(userPw: String) {
        val shared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_ACCOUNT,
            Context.MODE_PRIVATE
        ) // 쉐어드 에디터 가져오기

        val encryptedPw = AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(userPw)

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        editor.putString(KEY_USER_LMS_PW, encryptedPw) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun getUserLmsPw(): String {
        val shared =
            App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)

        val encryptedPw = shared.getString(KEY_USER_LMS_PW, "")!!
        Log.d("로그", "SharedPrefManager -getUserLmsPw() called / $encryptedPw")
        if (encryptedPw != "") {
            return AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(encryptedPw)
        }
        return encryptedPw
    }

    fun setUserLmsSubjectInfoList(itemSubjectInfoList: ArrayList<ItemSubjectInfo>) {

        val shared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_INFO,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        val itemSubjectInfoListJson = Gson().toJson(itemSubjectInfoList) // 배열 -> 문자열로 변환
        Log.d("로그", "SharedPrefManager -setUserLmsSubjectList() called / $itemSubjectInfoListJson ")
        editor.putString(KEY_USER_LMS_SUBJECT_INFO_LIST, itemSubjectInfoListJson) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun getUserLmsSubjectInfoList(): MutableList<ItemSubjectInfo> {
        val shared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_INFO,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val storedSubjectInfoListJson = shared.getString(KEY_USER_LMS_SUBJECT_INFO_LIST, "")!!
        var storedSubjectInfoList = ArrayList<ItemSubjectInfo>()

        // 과목 데이터가 있다면
        if (storedSubjectInfoListJson.isNotEmpty()) {
            // 저장된 문자열 -> 배열
            storedSubjectInfoList =
                Gson().fromJson(storedSubjectInfoListJson, Array<ItemSubjectInfo>::class.java)
                    .toMutableList() as ArrayList<ItemSubjectInfo>
        }

        return storedSubjectInfoList
    }

    fun clearAllLmsUserData() {

        // 쉐어드 모든 데이터 삭제
        val accountShared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_ACCOUNT,
            Context.MODE_PRIVATE
        ).edit().clear().apply()

        val lmsInfoShared = App.instance.getSharedPreferences(
            SHARED_USER_LMS_INFO,
            Context.MODE_PRIVATE
        ).edit().clear().apply()
    }

    fun getPermissionAccessFineLocation(): Boolean {
        val shared = App.instance.getSharedPreferences(
            SHARED_PERMISSIONS,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        return shared.getBoolean(KEY_PERMISSION_ACCESS_FINE_LOCATION, true)
    }

    fun setPermissionAccessFineLocation(permission: Boolean) {
         val shared = App.instance.getSharedPreferences(
            SHARED_PERMISSIONS,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        editor.putBoolean(KEY_PERMISSION_ACCESS_FINE_LOCATION, permission) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun clearAllPermissionData() {
        // 쉐어드 모든 데이터 삭제
        val accountShared = App.instance.getSharedPreferences(
            SHARED_PERMISSIONS,
            Context.MODE_PRIVATE
        ).edit().clear().apply()
    }

    fun setMyMbtiType(mbtiType: String) {
        val shared = App.instance.getSharedPreferences(
            SHARED_MBTI_TYPE,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        editor.putString(KEY_MY_MBTI_TYPE, mbtiType) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun getMyMbtiType(): String {
        val shared =
            App.instance.getSharedPreferences(SHARED_MBTI_TYPE, Context.MODE_PRIVATE)
        return shared.getString(KEY_MY_MBTI_TYPE, "")!!
    }

    fun clearAllMyMbtiData() {
        // 쉐어드 모든 데이터 삭제
        val accountShared = App.instance.getSharedPreferences(
            SHARED_MBTI_TYPE,
            Context.MODE_PRIVATE
        ).edit().clear().apply()
    }

    fun setUserData(userData: UserData) {

        val shared = App.instance.getSharedPreferences(
            SHARED_USER_DATA,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val editor = shared.edit() // 쉐어드 에디터 가져오기
        val userDataJson = Gson().toJson(userData) // 배열 -> 문자열로 변환
        editor.putString(KEY_USER_DATA, userDataJson) // 쉐어드에 저장
        editor.apply() // 변경 사항 저장
    }

    fun getUserData(): UserData {
        val shared = App.instance.getSharedPreferences(
            SHARED_USER_DATA,
            Context.MODE_PRIVATE
        ) // 쉐어드 가져오기

        val userDataJson = shared.getString(KEY_USER_DATA, "")!!

        return Gson().fromJson(userDataJson, UserData::class.java)
    }


}