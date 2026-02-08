package com.garam.shared.widget

import androidx.room.Room
import com.garam.shared.data.Todo
import com.garam.shared.data.source.local.TodoDatabase
import com.garam.shared.data.toExternal
import com.garam.shared.util.functions.filterTodosByDate
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.flow.first
import kotlinx.datetime.LocalDate
import kotlin.getValue
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class WidgetRepository(private val database: TodoDatabase) {

    suspend fun getWidgetData(): List<Todo> {
        val todos = database.todoDao().getAllTodoList("").first()

        val wholeTodos = todos.map { it.toExternal() }
//        val todayTodos = filterTodosByDate( todos = wholeTodos.filter { it.categoryId != null }, LocalDate.now())
//        val todayPlans = filterTodosByDate( todos = wholeTodos.filter { it.categoryId == null }, LocalDate.now())

        return wholeTodos

    }
}