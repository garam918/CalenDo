package com.garam.shared.notification

import com.garam.shared.data.source.TodoRepository


interface NotificationScheduler {
    // 1회성 알림 (날짜 지정)
    fun scheduleNotification(id: String, title: String, message: String, triggerAtMillis: Long)

    // 매일 반복 알림
    fun scheduleDailyNotification(id: String, type: String, hour: Int, minute: Int)

    // 알림 취소
    fun cancelNotification(id: String)

    fun cancelAllNotifications()
}

// 플랫폼별 구현체를 가져오기 위한 expect 함수
expect fun createNotificationScheduler(todoRepository: TodoRepository): NotificationScheduler