package com.garam.todolist.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent


fun widgetUpdate(context: Context) {
    val intent = Intent(context, TodoListWidgetProvider::class.java).apply {
        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
            AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, TodoListWidgetProvider::class.java)
            ))
    }
    context.sendBroadcast(intent)
}