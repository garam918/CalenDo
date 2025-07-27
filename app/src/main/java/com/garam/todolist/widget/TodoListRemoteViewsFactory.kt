package com.garam.todolist.widget

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.garam.todolist.R
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.data.source.local.TodoDatabase
import com.garam.todolist.data.toExternal
import com.garam.todolist.util.functions.colorStringToColor
import com.garam.todolist.util.functions.filterTodosByDate
import com.garam.todolist.util.functions.iconToDrawable
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.time.LocalDate

class TodoListRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val items = mutableListOf<WidgetItem>()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    private val db by lazy {
        Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            "Todo.db"
        ).fallbackToDestructiveMigration().build()
    }

    override fun onCreate() {
        // 초기 데이터 설정

        loadData()
    }

    override fun getViewAt(position: Int): RemoteViews {
        val fillInIntent = Intent()

        val pkg = context.packageName

        val item = items[position]
        return when (item) {
            is WidgetItem.Divider -> {
                RemoteViews(pkg, R.layout.widget_item_divider).apply {
                    setTextViewText(R.id.widget_text_divider, item.label)
                    setTextColor(R.id.widget_text_divider, ContextCompat.getColor(context,R.color.color_gray40))
                    setOnClickFillInIntent(R.id.widget_text_divider, fillInIntent)
                }
            }
            is WidgetItem.Separator -> {
                RemoteViews(pkg, R.layout.widget_item_separator).apply {
                    setTextViewText(R.id.widget_separator, "")
                }
            }
            is WidgetItem.TodoItem -> {
                val checkRes = when(item.todo.status?.get(LocalDate.now().toString())) {
                    TodoStatus.NONE -> R.drawable.todo_status_none_icon
                    TodoStatus.IN_PROGRESS -> R.drawable.todo_status_in_progress_icon
                    TodoStatus.COMPLETED -> R.drawable.todo_status_completed_icon
                    else -> R.drawable.todo_status_none_icon

                }

                RemoteViews(pkg, R.layout.todo_list_widget_item_layout).apply {
                    setTextViewText(R.id.item_text, item.todo.title)
                    setImageViewResource(R.id.widget_todo_check_box,checkRes)
                    setOnClickFillInIntent(R.id.widget_todo_item_linear, fillInIntent)

                }
            }
            is WidgetItem.PlanItem -> {
                RemoteViews(pkg, R.layout.widget_item_plan_layout).apply {
                    setTextViewText(R.id.widget_item_plan_title_text, item.plan.title)
                    setInt(R.id.widget_plan_title_img,"setColorFilter", colorStringToColor(item.plan.color.toString(),context))
                    setImageViewResource(R.id.widget_plan_title_img, iconToDrawable(item.plan.icon!!))
                    setOnClickFillInIntent(R.id.widget_item_plan_linear, fillInIntent)

                }
            }
        }

//        return view
    }

    override fun getCount(): Int {
        return items.size
    }
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 5
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true
    override fun onDestroy() {}
    override fun onDataSetChanged() {

        loadData()

    }

    private fun loadData() {
        items.clear()

//        CoroutineScope(Dispatchers.IO).launch {

        runBlocking {
            val flow = db.todoDao().getAllTodoList(uid.toString())
            val todos = runBlocking {
                withContext(Dispatchers.IO) {
                    flow.first()
                }
            }

            val wholeTodos = todos.map { it.toExternal() }
            val todayTodos = filterTodosByDate( todos = wholeTodos.filter { it.categoryId != null }, LocalDate.now())
            val todayPlans = filterTodosByDate( todos = wholeTodos.filter { it.categoryId == null }, LocalDate.now())

            items.add(WidgetItem.Divider("할일"))
            val widgetTodos = todayTodos.map { WidgetItem.TodoItem(it) }
            items.addAll(widgetTodos)
            items.add(WidgetItem.Separator)

            val widgetPlans = todayPlans.map { WidgetItem.PlanItem(it) }
            items.add(WidgetItem.Divider("일정"))
            items.addAll(widgetPlans)

        }
    }
}