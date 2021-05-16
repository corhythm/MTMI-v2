package com.example.mtmimyeon_gitmi.db

data class BoardPost(
    var subjectCode: String?, // 과목코드
    var title: String?, // 게시물 제목
    var day: String?, // 게시물 날짜
    var content: String?, // 게시물 내용
    var writerUid: String?, // 작성자 uid (user)
    var subjectBoardIndex: String?, // 게시물의 인덱스

) {
    constructor() : this("","","","","","") {}


    init {
        this.subjectCode = subjectCode
        this.title = title
        this.day = day
        this.content = content
        this.writerUid = writerUid
        this.subjectBoardIndex = subjectBoardIndex
    }
}
