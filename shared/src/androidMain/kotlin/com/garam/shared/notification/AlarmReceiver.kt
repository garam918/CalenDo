@file:OptIn(ExperimentalTime::class)

package com.garam.shared.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.garam.shared.data.AppPreferences
import com.garam.shared.data.Todo
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.toExternal
import com.garam.shared.util.functions.filterTodosByDate
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDate
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.time.ExperimentalTime

class AlarmReceiver : BroadcastReceiver(), KoinComponent {

    private val todoDao: TodoDao by inject()
    private val todoRepository: TodoRepository by inject()

    override fun onReceive(context: Context, intent: Intent) {


        val uid = FirebaseAuth.getInstance().uid

        val flow = todoDao.getAllTodoList(uid.toString())
        val todos = runBlocking {
            withContext(Dispatchers.IO) {
                flow.first()
            }
        }

        val type = intent.getStringExtra("type").toString()

        val todoList = filterTodosByDate(todos.toExternal(), LocalDate.now())
            .filter { if (type == "Todo") it.categoryId != null else it.categoryId == null }

        showNotification(
            context = context,
            todoList = todoList,
            type = type
        )

    }

    fun showNotification(context: Context, todoList: List<Todo>, type: String) {

        val title = when (type) {
            "Todo" -> "오늘의 할일을 알려드려요"
            "Plan" -> "오늘의 일정을 알려드려요"
            else -> "오늘의 할일을 알려드려요"
        }

        val message = when (type) {
            "Todo" -> "${todoList.maxBy { it.savedTime }.title} 외 ${todoList.size - 1}개의 할일이 있습니다"
            "Plan" -> "${todoList.maxBy { it.savedTime }.title} 외 ${todoList.size - 1}개의 일정이 있습니다"
            else -> "${todoList.maxBy { it.savedTime }.title} 외 ${todoList.size - 1}개의 할일이 있습니다"
        }


        // 알림 클릭 시 Activity 실행 해야됨
//        val intent = Intent(context, MainActivity::class.java)
//
//        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(context, "todo_channel_id")
            .setSmallIcon(

                android.R.drawable.ic_dialog_info
            ) // 아이콘 설정 필수
//            .setFullScreenIntent()
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        try {
            if (todoList.isNotEmpty()) NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), builder.build())

            setAlarm(type)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun setAlarm(type: String) {
        val scheduler = createNotificationScheduler(todoRepository)

        val time = AppPreferences.getString(
            if(type == "Todo") "todo_noti_time" else "plan_noti_time") ?: "오전 09:00"
        val amPm = time.split(" ")[0]
        val timeText = time.split(" ")[1]

        val hour = timeText.split(":")[0].toInt()
        val minute = timeText.split(":")[1].toInt()

        scheduler.scheduleDailyNotification(id = if(type == "Todo") "TodoNoti" else "PlanNoti"
            , type = type, hour = if(amPm == "오후") {
                if(hour == 12) hour
                else hour + 12
            }  else hour, minute = minute)



    }
}