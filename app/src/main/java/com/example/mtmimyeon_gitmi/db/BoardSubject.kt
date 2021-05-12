package com.example.mtmimyeon_gitmi.db

data class BoardSubject(
    var subjectCode: String, // 과목코드
    var title: String, // 게시물 제목
    var day: String, // 게시물 날짜
    var content: String, // 게시물 내용
    var writerUid: String, // 작성자 uid (user)
    var subjectBoardIndex: Int, // 게시물의 인덱스
    var status: Boolean // 게시물 상태 ( 공개-> 비공개 혹은 삭제)
) {
    init {
        this.subjectCode = subjectCode
        this.title = title
        this.day = day
        this.content = content
        this.writerUid = writerUid
        this.subjectBoardIndex = subjectBoardIndex
        this.status = status
    }
}
