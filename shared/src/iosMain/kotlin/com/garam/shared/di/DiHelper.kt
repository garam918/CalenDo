package com.garam.shared.di

import com.garam.shared.data.source.TodoRepository
import com.garam.shared.notification.NotificationScheduler
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class DiHelper: KoinComponent {

    // Swift에서 꺼내 쓸 객체들을 함수 형태로 정의합니다.
    fun getNotificationScheduler(): NotificationScheduler {
        return get() // Koin에서 주입받은 인스턴스 반환
    }

    fun getTodoRepository(): TodoRepository {
        return get()
    }
}