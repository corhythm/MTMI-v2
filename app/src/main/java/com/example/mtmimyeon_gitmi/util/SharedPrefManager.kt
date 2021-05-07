package com.example.mtmimyeon_gitmi.util

import android.content.Context

object SharedPrefManager {
    private const val SHARED_USER_LMS_ACCOUNT = "USER_LMS_ACCOUNT"
    private const val KEY_LMS_USER_ID = "LMS_USER_ID"
    private const val KEY_LMS_USER_PW = "LMS_USER_PW"

    fun setLmsUserId(userId: String) {
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.putString(KEY_LMS_USER_ID, userId)
        editor.apply() // 변경 사항 저장
    }

    fun getLmsUserId(): String {
        val shared = App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)
        return shared.getString(KEY_LMS_USER_ID, "")!!
    }

    fun setLmsUserPW(userPw: String) {
        // 쉐어드 가져오기
        val shared = App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)

        // 쉐어드 에디터 가져오기
        val editor = shared.edit()
        editor.putString(KEY_LMS_USER_PW, userPw)
        editor.apply() // 변경 사항 저장
    }

    fun getLmsUserPw(): String {
        val shared = App.instance.getSharedPreferences(SHARED_USER_LMS_ACCOUNT, Context.MODE_PRIVATE)
        return shared.getString(KEY_LMS_USER_PW, "")!!
    }


}