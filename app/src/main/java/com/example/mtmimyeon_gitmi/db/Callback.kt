package com.example.mtmimyeon_gitmi.db

//임시용 콜백 인터페이스
interface Callback<T> {
    fun onCallback(data: T)
}