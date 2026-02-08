package com.garam.shared.notification

import cocoapods.FirebaseAuth.FIRAuth
import com.garam.shared.data.AppPreferences
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.toExternal
import com.garam.shared.util.functions.filterTodosByDate
import com.kizitonwose.calendar.core.now
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.plus
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import org.koin.compose.koinInject
import org.koin.core.component.inject
import platform.BackgroundTasks.BGAppRefreshTask
import platform.BackgroundTasks.BGAppRefreshTaskRequest
import platform.BackgroundTasks.BGTaskScheduler
import platform.Foundation.NSCalendar
import platform.Foundation.*
import platform.Foundation.NSDate
import platform.Foundation.dateWithTimeIntervalSince1970
import platform.UserNotifications.UNCalendarNotificationTrigger
import platform.UserNotifications.UNMutableNotificationContent
import platform.UserNotifications.UNNotificationRequest
import platform.UserNotifications.UNNotificationSound
import platform.UserNotifications.UNUserNotificationCenter
import kotlin.getValue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

actual fun createNotificationScheduler(todoRepository: TodoRepository): NotificationScheduler {
    return IosNotificationScheduler(todoRepository)
}

@OptIn(ExperimentalForeignApi::class, ExperimentalTime::class)
class IosNotificationScheduler(private val todoRepository: TodoRepository) : NotificationScheduler {

//    private val todoDao = todoDao
    private val center = UNUserNotificationCenter.currentNotificationCenter()
    private val taskId = "com.garam.todolist.notification"
    private val uid = FIRAuth.auth().currentUser()?.uid() ?: ""

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun scheduleNotification(id: String, title: String, message: String, triggerAtMillis: Long) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound)
        }

        val date = NSDate.dateWithTimeIntervalSince1970(triggerAtMillis / 1000.0)
        val components = NSCalendar.currentCalendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay or
                    NSCalendarUnitHour or NSCalendarUnitMinute,
            fromDate = date
        )

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(
            components, repeats = false
        )

        val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)
        center.addNotificationRequest(request, null)
    }

    override fun scheduleDailyNotification(id: String, type: String, hour: Int, minute: Int) {
        // BGTaskScheduler로 대체 — UNCalendarNotificationTrigger(repeats=true) 제거

        registerTasks(type)

        scope.launch {
            performNotificationUpdate(type, hour, minute)
        }


//        val request = BGAppRefreshTaskRequest(identifier = taskId)
//        println("id : $id")
//        val date = nextTriggerDate(hour, minute)
//        println(date)
//        request.earliestBeginDate = date
//        BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)
    }

    override fun cancelNotification(id: String) {
        center.removePendingNotificationRequestsWithIdentifiers(listOf(id))
    }

    override fun cancelAllNotifications() {
        center.removeAllPendingNotificationRequests()
        BGTaskScheduler.sharedScheduler.cancelAllTaskRequests()
    }

    private fun nextTriggerDate(hour: Int, minute: Int): NSDate {
        val calendar = NSCalendar.currentCalendar
        val now = NSDate.now

        val components = calendar.components(
            NSCalendarUnitHour or NSCalendarUnitMinute or NSCalendarUnitSecond,
            fromDate = now
        )

        components.hour = hour.toLong()
        components.minute = minute.toLong()
        components.second = 0

        var date = calendar.dateFromComponents(components)!!

        // 이미 지나간 시간이면 +1일
        if (date.timeIntervalSinceNow <= 0) {
            date = date.dateByAddingTimeInterval((60 * 60 * 24).toDouble())
        }
        return date
    }

    fun registerTasks(type: String) {
        handleBackgroundRefresh(type)

    }
    private fun handleBackgroundRefresh(type: String) {

        val time = AppPreferences.getString(
            if(type == "Todo") "todo_noti_time" else "plan_noti_time") ?: "오전 09:00"
        val amPm = time.split(" ")[0]
        val timeText = time.split(" ")[1]

        val hour = timeText.split(":")[0].toInt()
        val minute = timeText.split(":")[1].toInt()

        println("$type : $time")

        val nextDate = nextTriggerDate(if(amPm == "오후") {
            if(hour == 12) hour
            else hour + 12
        }  else hour, minute)

        println("nDate $nextDate")
        val request = BGAppRefreshTaskRequest(taskId).apply {
            earliestBeginDate = nextDate
        }
//        BGTaskScheduler.sharedScheduler.submitTaskRequest(request, null)

//        val task = BGAppRefreshTask(taskId)
//
//
//        // 2. 현재 시점의 Room 데이터를 조회하여 알림 갱신
//        task.expirationHandler = {
//            task.setTaskCompletedWithSuccess(false)
//        }
//
//        scope.launch {
//            try {
//                // 오늘 또는 내일 알림 예약 로직 실행
//                performNotificationUpdate(type, hour, minute)
//
//            } catch (e: Exception) {
//                println("ios notification fail" + e.message)
//            }
//        }
    }

    /**
     * 설정 시간 변경 시 또는 백그라운드 갱신 시 실행되는 핵심 로직
     */
    private suspend fun performNotificationUpdate(type: String, hour: Int, minute: Int) {
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val today = now.date

        // 현재 시간과 설정 시간 비교
        val targetToday = LocalDateTime(today.year, today.month.number, today.day, hour, minute)

        // 설정 시간이 현재보다 미래면 오늘, 아니면 내일 날짜 선택
        val targetDate = if (targetToday.toInstant(TimeZone.currentSystemDefault()) > now.toInstant(TimeZone.currentSystemDefault())) {
            today
        } else {
            today.plus(DatePeriod(days = 1))
        }

        println("targetToday $targetToday")
        println("targetDate $targetDate")

        val flow = todoRepository.getTodoList(uid)
        val todos = runBlocking {
            withContext(Dispatchers.IO) {
                flow.first()
            }
        }

        val todoList = filterTodosByDate(todos, LocalDate.now())
            .filter { if (type == "Todo") it.categoryId != null else it.categoryId == null }

        println("ios noti todos : $todoList")

        // 2. 알림 예약 (기존 "DAILY_ALARM" ID가 있으면 덮어씌워짐)
        if (type == "Todo") {
            scheduleIosNotification(
                id = "TodoNoti",
                title = "오늘의 할일을 알려드려요",
                message = if(todoList.isNotEmpty()) "${todoList.first().title}외 할일이 ${todoList.size-1}개 있습니다."
                else "오늘은 할일이 없어요!",
                date = targetDate, hour = hour, minute = minute
            )
        }
        else {
            scheduleIosNotification(
                id = "PlanNoti",
                title = "오늘의 일정을 알려드려요",
                message = if(todoList.isNotEmpty()) "${todoList.first().title}외 일정이 ${todoList.size-1}개 있습니다."
                else "오늘은 일정이 없어요!",
                date = targetDate, hour = hour, minute = minute
            )
        }
    }

    private fun scheduleIosNotification(id: String, title: String, message: String, date: LocalDate, hour: Int, minute: Int) {
        val content = UNMutableNotificationContent().apply {
            setTitle(title)
            setBody(message)
            setSound(UNNotificationSound.defaultSound)
        }

        val components = NSDateComponents().apply {
            setYear(date.year.toLong())
            setMonth(date.month.number.toLong())
            setDay(date.day.toLong())
            setHour(hour.toLong())
            setMinute(minute.toLong())
        }

        val trigger = UNCalendarNotificationTrigger.triggerWithDateMatchingComponents(components, true)
        val request = UNNotificationRequest.requestWithIdentifier(id, content, trigger)
        center.addNotificationRequest(request, null)

    }
}
