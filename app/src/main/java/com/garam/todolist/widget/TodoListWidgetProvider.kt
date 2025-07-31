package com.garam.todolist.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.room.Room
import com.garam.todolist.R
import com.garam.todolist.data.Todo
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.data.source.local.TodoDatabase
import com.garam.todolist.data.toLocal
import com.garam.todolist.ui.todoList.TodoListActivity
import com.garam.todolist.util.functions.dateToString
import com.garam.todolist.util.functions.localDateToDateString
import com.garam.todolist.util.functions.localDateToString
import com.garam.todolist.util.functions.localDateToWidgetDateString
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import kotlin.collections.set

class TodoListWidgetProvider: AppWidgetProvider() {

    companion object {
        const val ACTION_REFRESH = "com.garam.todolist.ACTION_REFRESH"
        const val ACTION_TOGGLE_CHECK = "com.garam.todolist.ACTION_TOGGLE_CHECK"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        val mgr = AppWidgetManager.getInstance(context)
        val ids = mgr.getAppWidgetIds(ComponentName(context!!, TodoListWidgetProvider::class.java))
        if (intent?.action == AppWidgetManager.ACTION_APPWIDGET_UPDATE
            || intent?.action == AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED
            || intent?.action == ACTION_REFRESH) {

            mgr.notifyAppWidgetViewDataChanged(ids,R.id.widget_today_text)
            mgr.notifyAppWidgetViewDataChanged(ids, R.id.widget_todo_list_view)
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        for (widgetId in appWidgetIds) {
            val intent = Intent(context, TodoListRemoteViewsService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId)
                data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
            }


            val views = RemoteViews(context.packageName, R.layout.todo_list_widget)
            val today = LocalDate.now()
            views.setTextViewText(R.id.widget_today_text, localDateToWidgetDateString(today) + " 오늘")
            views.setRemoteAdapter(widgetId,R.id.widget_todo_list_view, intent)

            val openIntent = Intent(context, TodoListActivity::class.java)
            val openPendingIntent = PendingIntent.getActivity(
                context, 0, openIntent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, openPendingIntent)

            val refreshIntent = Intent(context, TodoListWidgetProvider::class.java).apply {
                action = ACTION_REFRESH
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_btn, refreshPendingIntent)

            val templateIntent = Intent(context, TodoListActivity::class.java)
            val pendingTemplate = PendingIntent.getActivity(
                context, 0, templateIntent, PendingIntent.FLAG_IMMUTABLE
            )
            views.setPendingIntentTemplate(R.id.widget_todo_list_view, pendingTemplate)

            appWidgetManager.updateAppWidget(widgetId, views)
        }
    }
}