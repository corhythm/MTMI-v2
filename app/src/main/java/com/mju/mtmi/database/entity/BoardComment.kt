package com.mju.mtmi.database.entity

class BoardComment(
    var commentBoardIndex: String = "",// 게시물 인덱스
    var commenterUid: String = "", // 댓글 작성자 uid (user)
    var userName: String = "",
    var day: String = "",  // 댓글 올린 날짜
    var content: String = "", // 댓글 내용
)