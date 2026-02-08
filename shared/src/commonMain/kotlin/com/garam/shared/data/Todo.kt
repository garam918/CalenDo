package com.garam.shared.data

import kotlinx.serialization.Serializable

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
    val savedTime : Long
)

@Serializable
enum class TodoStatus {
    NONE,       // 체크 해제 상태
    IN_PROGRESS, // 진행 중
    COMPLETED;   // 완료

    companion object {
        // 💡 모든 상수를 Map<String, TodoStatus>에 저장하여 리플렉션 없이 조회
        private val nameMap = TodoStatus.entries.associateBy(TodoStatus::name)

        // Map을 조회하여 상수를 반환하는 함수
        fun fromName(name: String): TodoStatus {
            // Map에서 조회하고, 없으면 예외 발생 (기존 valueOf와 동일한 동작)
            return nameMap[name] ?: throw IllegalArgumentException("Invalid TodoStatus name: $name")
        }
    }
}
