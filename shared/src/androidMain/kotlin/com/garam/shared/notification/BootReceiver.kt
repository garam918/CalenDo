package com.garam.shared.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.garam.shared.data.AppPreferences

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            val scheduler = AndroidNotificationScheduler(context)

            val isTodoNoti = AppPreferences.getBoolean("todo_noti",false)
            val isAllDayPlanNoti = AppPreferences.getBoolean("plan_noti",false)


            Log.e("Boot Receiver", "$isTodoNoti")
            Log.e("Boot Receiver", "$isAllDayPlanNoti")

            if(isTodoNoti) {
                val todoNotiTime = AppPreferences.getString("todo_noti_time", "오전 09:00") ?: "오전 09:00"

                val amPm = todoNotiTime.split(" ")[0]
                val timeText = todoNotiTime.split(" ")[1]

                val hour = timeText.split(":")[0].toInt()
                val minute = timeText.split(":")[1].toInt()

                scheduler.scheduleDailyNotification(id = "TodoNoti", type = "Todo", hour = if(amPm == "오후") {
                    if(hour == 12) hour
                    else hour + 12
                }  else hour, minute = minute)
            }

            if(isAllDayPlanNoti) {
                val allDayPlanNotiTime = AppPreferences.getString("plan_noti_time", "오전 09:00") ?: "오전 09:00"

                val amPm = allDayPlanNotiTime.split(" ")[0]
                val timeText = allDayPlanNotiTime.split(" ")[1]

                val hour = timeText.split(":")[0].toInt()
                val minute = timeText.split(":")[1].toInt()

                scheduler.scheduleDailyNotification(id = "PlanNoti", type = "Plan", hour = if(amPm == "오후") {
                    if(hour == 12) hour
                    else hour + 12
                }  else hour, minute = minute)

            }

        }
    }
}