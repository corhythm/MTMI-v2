package com.mju.mtmi.database

interface DataBaseCallback<T> {
    fun onCallback(data: T)
}