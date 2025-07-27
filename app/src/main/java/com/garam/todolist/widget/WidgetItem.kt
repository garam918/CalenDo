package com.garam.todolist.widget

import com.garam.todolist.data.Todo

sealed class WidgetItem {
    data class TodoItem(val todo: Todo) : WidgetItem()
    object Separator : WidgetItem()
    data class PlanItem(val plan: Todo) : WidgetItem()
    data class Divider(val label: String) : WidgetItem()
}