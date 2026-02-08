import UIKit
import BackgroundTasks
import UserNotifications
import Shared

class NotificationWorker {
    static let shared = NotificationWorker()
    let taskId = "com.garam.todolist.notification"
    
    var todoNotiTime = AppPreferences.shared.getString(key: "todo_noti_time", defaultValue: "오전 09:00")
    
    var planNotiTime = AppPreferences.shared.getString(key: "plan_noti_time", defaultValue: "오전 09:00")
   
//    let todoNotiTimeText = todoNotiTime?.split(separator: " ")
//    let planNotiTimeText = planNotiTime?.split(separator: " ")
    
    
    
    var targetHour: Int = 9
    var targetMinute: Int = 0

    // 1. 초기화 및 등록 (AppDelegate에서 호출)
    func registerTask() {

        BGTaskScheduler.shared.register(forTaskWithIdentifier: taskId, using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }

    // 2. 백그라운드 작업 수행 (실제 로직)
    private func handleAppRefresh(task: BGAppRefreshTask) {
        // 다음 실행 예약 (오늘 작업이 끝나기 전에 내일 거 예약)
        scheduleNextBackgroundTask()
        

        let diHelper = DiHelper()
        let scheduler = diHelper.getNotificationScheduler()
//
//        let repository = diHelper.getTodoRepository()
        
        // 시간이 오래 걸리면 시스템이 강제 종료하므로 취소 핸들러 설정
        task.expirationHandler = {
            task.setTaskCompleted(success: false)
        }

        // 비동기 작업 시작
        Task {
            do {
//                let todoList = TodoDao.getAllTodoList(<#T##self: TodoDao##TodoDao#>)
//                let scheduler = diHelper.IosNotificationScheduler()
                
                let todoNotiTimeText = todoNotiTime?.split(separator: " ")

                let planNotiTimeText = todoNotiTime?.split(separator: " ")

                scheduler.scheduleDailyNotification(id: "TodoNoti", type: "Todo", hour: 22, minute: 50)
                scheduler.scheduleDailyNotification(id: "PlanNoti", type: "Plan", hour: 22, minute: 50)
                

//                sendNotifications(todoCount: 1, scheduleCount: 2)
                print("test")
        

                task.setTaskCompleted(success: true)
            } catch {
                print("Background Fetch 실패: \(error)")
                task.setTaskCompleted(success: false)
            }
        }
    }

    // 3. 다음 백그라운드 작업 스케줄링
    func scheduleNextBackgroundTask() {
        let request = BGAppRefreshTaskRequest(identifier: taskId)

        // 중요: 알림 설정 시간보다 '2시간 전'에 깨워달라고 요청
        // 예: 9시 알림이면 7시쯤 깨워서 준비시킴
        var components = Calendar.current.dateComponents([.year, .month, .day], from: Date())
        components.hour = targetHour - 2
        if components.hour! < 0 { components.hour! += 24 } // 날짜 계산 필요하지만 간단히 처리

        // 내일 날짜 계산
        guard let tomorrowWakeUpTime = Calendar.current.date(from: components)?.addingTimeInterval(86400) else { return }

        request.earliestBeginDate = tomorrowWakeUpTime

        do {
            try BGTaskScheduler.shared.submit(request)
            print("다음 백그라운드 작업 예약됨: \(tomorrowWakeUpTime)")
        } catch {
            print("스케줄링 실패: \(error)")
        }
    }

    // 4. 실제 알림 등록 (오늘 날짜에 대한 1회성 알림)
    private func sendNotifications(todoCount: Int, scheduleCount: Int) {
        let center = UNUserNotificationCenter.current()

        // 기존 알림 정리
        center.removeAllPendingNotificationRequests()

        // 알림을 띄울 시간 설정 (오늘 targetHour:targetMinute)
        var triggerDate = Calendar.current.dateComponents([.year, .month, .day], from: Date())
        triggerDate.hour = targetHour
        triggerDate.minute = targetMinute

        let trigger = UNCalendarNotificationTrigger(dateMatching: triggerDate, repeats: false)

        // 할일 알림
        if todoCount > 0 {
            let content = UNMutableNotificationContent()
            content.title = "오늘의 할일을 알려드려요"
            content.body = "오늘 처리해야 할 할일이 \(todoCount)개 있습니다."
            content.sound = .default

            let request = UNNotificationRequest(identifier: "TODO_TODAY", content: content, trigger: trigger)
            center.add(request)
        }

        // 일정 알림
        if scheduleCount > 0 {
            let content = UNMutableNotificationContent()
            content.title = "오늘의 일정을 알려드려요"
            content.body = "오늘 예정된 일정이 \(scheduleCount)개 있습니다."
            content.sound = .default

            let request = UNNotificationRequest(identifier: "SCHEDULE_TODAY", content: content, trigger: trigger)
            center.add(request)
        }
    }

    // 시간 설정 변경 시 호출될 메서드
    func updateTimeAndReschedule(hour: Int, minute: Int) {
        self.targetHour = hour
        self.targetMinute = minute

        // 시간을 바꿨으니 다음 백그라운드 작업 시간도 변경해서 재예약
        // (기존 작업 취소 후 재등록)
        BGTaskScheduler.shared.cancel(taskRequestWithIdentifier: taskId)
        scheduleNextBackgroundTask()
    }
}
