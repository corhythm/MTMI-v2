package com.mju.mtmi.database.entity

class BoardComment(
    val boardIdx: String,
    val commentIdx: String,// 게시물 인덱스
    val commenterUid: String, // 댓글 작성자 uid (user)
    val timeStamp: String,  // 댓글 올린 날짜
    val content: String, // 댓글 내용
) {
    constructor() : this(
        boardIdx = "",
        commentIdx = "",
        commenterUid = "",
        timeStamp = "",
        content = ""
    )
}