package com.mju.mtmi.database

//임시용 콜백 인터페이스
interface Callback<T> {
    fun onCallback(data: T)
}