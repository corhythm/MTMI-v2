package com.mju.mtmi.database.entity

class BoardComment(
    val postIdx: String,
    val commentIdx: String,// 댓글 인덱스
    val writerIdx: String, // 댓글 작성자 uid (user)
    val timeStamp: String,  // 댓글 올린 날짜
    val content: String, // 댓글 내용
) {
    constructor() : this(
        postIdx = "",
        commentIdx = "",
        writerIdx = "",
        timeStamp = "",
        content = ""
    )
}