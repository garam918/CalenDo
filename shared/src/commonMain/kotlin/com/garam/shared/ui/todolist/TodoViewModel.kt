package com.garam.shared.ui.todolist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.shared.auth.AuthRepository
import com.garam.shared.data.Category
import com.garam.shared.data.Goal
import com.garam.shared.data.GoalType
import com.garam.shared.data.Todo
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.notification.createNotificationScheduler
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.ExperimentalTime

class TodoViewModel(
    private val authRepository: AuthRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {

    @OptIn(ExperimentalTime::class)
    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate = _selectedDate.asStateFlow()

    private val _currentUserData = MutableStateFlow<LocalUserData?>(null)
    val currentUserData = _currentUserData.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    private val _categories = MutableStateFlow<List<Category>>(listOf())
    val categories = _categories.asStateFlow()

    private val _todoList = MutableStateFlow(mutableMapOf<Category, List<Todo>>())
    val todoList = _todoList.asStateFlow()

    private val _planList = MutableStateFlow<List<Todo>>(listOf())
    val planList = _planList.asStateFlow()

    private val _currentGoal = MutableStateFlow<Goal?>(null)
    val currentGoal = _currentGoal.asStateFlow()

    private val _todoListInGoal = MutableStateFlow<List<Todo>>(listOf())
    val todoListInGoal = _todoListInGoal.asStateFlow()


    init {
        getCurrentUser()
        getCategory()

//        val (startDate, endDate) = getWeekStartEnd(selectedDate.value.toString())
//        getCurrentGoal(startDate, endDate, GoalType.WEEKLY).invokeOnCompletion {
//            getTodoInGoal()
//        }
        getPlanList()
    }

    fun getCurrentUser() = viewModelScope.launch {

        try {
            _currentUserData.value = authRepository.currentUser()

        } catch (e: Exception) {
            println(e.message)
        }


    }

    fun getCategory() = viewModelScope.launch {

        try {
            todoRepository.getCategoryList(currentUserData.value?.uid.toString())
                .collect {
                    _categories.value = it.sortedBy { it.index }

                    it.forEach { category ->
                        getTodoList(category.categoryId)
                    }
                }

        } catch (e: Exception) {
            println(e.message)
        }

    }

    fun getPlanList() = viewModelScope.launch {

        try {

            todoRepository.getPlanList(currentUserData.value?.uid.toString()).collect {

                _planList.value = it

            }

        } catch (e: Exception) {
            println(e.message)
        }


    }

    fun insertCategory(category: Category) = viewModelScope.launch {
        todoRepository.saveCategory(category, currentUserData.value?.uid.toString())
    }

    fun getCurrentGoal(startDate: String, endDate: String, type: GoalType) = viewModelScope.launch {

        try {

            _currentGoal.value = todoRepository.getGoal(
                startDate,
                endDate,
                type,
                currentUserData.value?.uid.toString()
            )

            // 26.01.14 임시 주석 처리, 목표 안에 할일 구현할 때 다시 사용
//            getTodoInGoal()

        } catch (e: Exception) {

            println(e.message)

        }
    }

    fun getTodoInGoal() = viewModelScope.launch {

        try {

            _todoListInGoal.value = todoRepository.getTodoByGoal(_currentGoal.value?.goalId.toString(), currentUserData.value?.uid.toString())
//                todoDao.getTodoByGoal(
//                goalId = _currentGoal.value?.goalId.toString(),
//                uid = currentUserData.value?.uid.toString()
//            ).toExternal()

        } catch (e: Exception) {
            println(e.message)
        }
    }

    fun setGoal(goal: Goal?) {
        _currentGoal.value = goal
    }

    fun addTodo(todo: Todo) {
        _todoListInGoal.update { current ->
            current + todo
        }
    }

    fun deleteTodoInGoal(id: String) {
        _todoListInGoal.update { current ->
            current.filter { it.id != id }
        }
    }

    fun updateTodoInGoal(todo: Todo) {
        _todoListInGoal.update { current ->
            val list = current.toMutableList()
            val findTodo = list.find { it.id == todo.id }!!
            val index = list.indexOf(findTodo)

            val editTodo = findTodo.copy(memo = todo.memo, priority = todo.priority)
            list.removeAt(index)
            list.add(index, editTodo)
            list
        }

    }

    fun upsertTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.saveTodo(todo, currentUserData.value?.uid.toString())
    }


    fun deleteTodo(id: String) = viewModelScope.launch {

        todoRepository.deleteTodo(id, currentUserData.value?.uid.toString())

    }

    fun getTodoList(categoryId: String) = viewModelScope.launch {

        try {

            todoRepository.getTodoByCategory(categoryId, currentUserData.value?.uid.toString())
                .collect {
                    val currentMap = _todoList.value.toMutableMap()

                    currentMap[categories.value.find { it.categoryId == categoryId }
                        ?: return@collect] = it

                    _todoList.value = currentMap
                }

        } catch (e: Exception) {
            println(e.message)
        }


    }


    fun upsertGoal(goal: Goal) = viewModelScope.launch {

        todoRepository.saveGoal(goal, currentUserData.value?.uid.toString())

        _currentGoal.value = goal
    }

    fun setTodoPlanNoti() {

        val scheduler = createNotificationScheduler(todoRepository)

        scheduler.scheduleDailyNotification("TodoNoti", "Todo", 9, 0)
        scheduler.scheduleDailyNotification("PlanNoti", "Plan", 9, 0)

    }


}
