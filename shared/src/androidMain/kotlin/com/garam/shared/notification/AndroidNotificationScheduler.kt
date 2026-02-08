package com.garam.shared.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.util.Calendar
import com.garam.shared.AppContext
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.TodoDao

actual fun createNotificationScheduler(todoRepository: TodoRepository): NotificationScheduler {
    return AndroidNotificationScheduler(AppContext.get())
}

class AndroidNotificationScheduler(private val context: Context) : NotificationScheduler {
    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    override fun scheduleNotification(id: String, title: String, message: String, triggerAtMillis: Long) {
        val pendingIntent = createPendingIntent(id, title)

        println("title $title")
        println("time $triggerAtMillis")
        // 정확한 시간에 알림 (절전 모드 무시)
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
    }

    override fun scheduleDailyNotification(id: String, type: String, hour: Int, minute: Int) {
        val pendingIntent = createPendingIntent(id, type)

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_MONTH, 1)
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )

//        alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent
//        )
    }

    override fun cancelNotification(id: String) {
        val pendingIntent = createPendingIntent(id, "")
        alarmManager.cancel(pendingIntent)
    }

    private fun createPendingIntent(id: String, type: String): PendingIntent {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.garam.todolist.DAILY_NOTIFICATION"
            putExtra("type",type)
        }
        // id.hashCode()를 사용해 각 알림을 구분합니다.
        return PendingIntent.getBroadcast(
            context, id.hashCode(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun cancelAllNotifications() {
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            action = "com.garam.todolist.DAILY_NOTIFICATION"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }
}