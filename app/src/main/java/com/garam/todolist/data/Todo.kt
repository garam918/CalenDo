package com.garam.todolist.data

import com.google.firebase.Timestamp

// 아이콘, 색상, 시작시간, 인덱스 추가 해야 함
// 카테고리 아이디 유무로 할일, 일정 구분
data class Todo(
    val id : String,
    val categoryId : String?,
    val title : String,
    val startDate : String,
    val endDate : String,
    val repeatRule : String?,
    val status : MutableMap<String,TodoStatus>?,
    val priority : Boolean,
    val memo : String,
    val icon : CategoryIconType?,
    val color : String?,
    val startTime : String?,
    val index : Int?,
    val savedTime : Timestamp
)

enum class TodoStatus {
    NONE,       // 체크 해제 상태
    IN_PROGRESS, // 진행 중
    COMPLETED   // 완료
}