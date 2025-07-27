package com.garam.todolist.widget

import android.content.Intent
import android.widget.RemoteViewsService

class TodoListRemoteViewsService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return TodoListRemoteViewsFactory(applicationContext)
    }
}