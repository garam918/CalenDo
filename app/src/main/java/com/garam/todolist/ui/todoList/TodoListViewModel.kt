package com.garam.todolist.todoList

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.CategoryWithTodo
import com.garam.todolist.data.Goal
import com.garam.todolist.data.Todo
import com.garam.todolist.data.source.repository.TodoRepository
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.data.source.SharedPreferenceStorage
import com.garam.todolist.util.functions.getWeekStartEnd
import com.garam.todolist.util.functions.localDateToString
import com.google.firebase.Timestamp
import com.kizitonwose.calendar.core.yearMonth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class TodoListViewModel @Inject constructor(
    private val todoRepository: TodoRepository,
    private val preferenceStorage: SharedPreferenceStorage
) : ViewModel() {

    val currentMode : MutableStateFlow<String> = MutableStateFlow("CalenDo")

    val startModeFlow = MutableStateFlow<String?>("CalenDo")
    val sortModeFlow = MutableStateFlow<String?>("Saved")
    val firstDayOfWeekFlow = MutableStateFlow<String?>("Mon")

    val isWeekMode : MutableStateFlow<Boolean> = MutableStateFlow(true)

    val isExpandTodoListInGoal = MutableStateFlow(false)

    val currentMonth : MutableStateFlow<YearMonth> = MutableStateFlow(LocalDate.now().yearMonth)
    val currentMonthString : MutableStateFlow<String> = MutableStateFlow("")

    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList : StateFlow<List<Category>> = _categoryList

    private val _goalList = MutableStateFlow<List<Goal>>(emptyList())
    val goalList : StateFlow<List<Goal>> = _goalList

    private val _currentGoal : MutableStateFlow<Goal?> = MutableStateFlow(null)
    val currentGoal = _currentGoal

    private val _currentTodoInGoal = MutableStateFlow<List<Todo>>(emptyList())
    val currentTodoInGoal = _currentTodoInGoal

    private val _todoList = MutableStateFlow<List<Todo>>(emptyList())
    val todoList : StateFlow<List<Todo>> = _todoList

    private val _categoryTodoMap = MutableStateFlow<List<CategoryWithTodo>>(emptyList())
    val categoryTodoMap : StateFlow<List<CategoryWithTodo>> = _categoryTodoMap

    private val dateTodoList = MutableStateFlow<MutableMap<LocalDate, List<Todo>>>(mutableMapOf())
    val dateTodoCountMap = MutableStateFlow<MutableMap<String, MutableMap<Category,List<Todo>>>>(mutableMapOf())

    private val _selectedDatePlanList = MutableStateFlow<List<Todo>>(emptyList())
    val selectedDatePlanList : StateFlow<List<Todo>> = _selectedDatePlanList

    val selectedCategoryTodoList = MutableStateFlow<List<Todo>>(emptyList())

    val datePlanMap = MutableStateFlow<MutableMap<String, List<Todo>>>(mutableMapOf())

    val selectedDate = MutableStateFlow<String?>(LocalDate.now().toString())

    init {

        getStartMode()
        getSortMode()
        getFirstDayOfWeek()

        currentMode.value = startModeFlow.value.toString()

        isWeekMode.value = true
        selectedDate.value = LocalDate.now().toString()
        currentMonthString.value = localDateToString(LocalDate.parse(selectedDate.value))
        getCategoryList()
        getPlanList()
        val (stDate, edDate) = getWeekStartEnd(selectedDate.value.toString())
        getGoal(stDate,edDate)
    }

    private fun getCategoryList() {

        viewModelScope.launch(Dispatchers.IO) {

            // flow는 collect를 해야 값이 지속적으로 업데이트 됨. value에 직접 값을 넣으면 단순 값을 복사하게 됨
            todoRepository.getCategoryList().collectLatest {
                _categoryTodoMap.update { emptyList() }


                val categoryList = it.sortedBy { it.index }
                _categoryList.update {
                    categoryList
                }

                _categoryList.value.forEach {
                    getTodoByCategory(it)
                }

            }
        }
    }

    fun addCategory(title: String, categoryIconType: CategoryIconType, categoryColor : String) = viewModelScope.launch {
        todoRepository.saveCategory(Category(categoryId = UUID.randomUUID().toString()
            , title = title,
            index = categoryList.value.size , icon = categoryIconType,
            color = categoryColor))
    }

    private fun getTodoList() {

        viewModelScope.launch(Dispatchers.IO) {

            todoRepository.getTodoList().collectLatest {
                _todoList.value = it
            }

        }
    }

    fun addGoal(goal : Goal) = viewModelScope.launch(Dispatchers.IO) {

        todoRepository.saveGoal(goal = goal)
    }

    fun getGoal(startDate : String, endDate : String) = viewModelScope.launch(Dispatchers.IO) {

        val goal = todoRepository.getGoal(startDate = startDate, endDate = endDate)
        currentGoal.value = if(goal == null) {
            null
        }
        else goal
    }



    fun getTodoByCategory(category : Category) {
        viewModelScope.launch(Dispatchers.IO) {

            todoRepository.getTodoByCategory(category.categoryId).collectLatest { todos ->
                // MutableStateFlow를 업데이트 하는 방식으로
                _categoryTodoMap.update { currentMap ->
                    val updatedList = currentMap
                        .filterNot { it.category.categoryId == category.categoryId } // 기존 데이터 제거
                        .plus(CategoryWithTodo(category, todos)) // 새로운 데이터 추가
                    updatedList
                }
            }

        }
    }

    fun getTodoByGoal(goalId : String) = viewModelScope.launch(Dispatchers.IO) {

        todoRepository.getTodoByGoal(goalId = goalId).collectLatest {
            _currentTodoInGoal.value = emptyList<Todo>()
            _currentTodoInGoal.value = it

            Log.e("currentTodoList",_currentTodoInGoal.value.toString())
        }
    }

    fun addTodo(selectedDate : String, categoryId : String?, title : String) = viewModelScope.launch {
        todoRepository.saveTodo(
            Todo(id = UUID.randomUUID().toString(),
                categoryId = categoryId, title = title, startDate = selectedDate,
                endDate = selectedDate, repeatRule = null,status = mutableMapOf(selectedDate to TodoStatus.NONE),
                priority = false, memo = "", color = null, icon = null, index = null, startTime = null, savedTime = Timestamp.now()
            ))
    }

    fun addPlan(selectedDate: String, title : String, repeatRule : String?, color: String,
                icon : CategoryIconType, startTime : String?) = viewModelScope.launch {

        todoRepository.saveTodo(Todo(
            id = UUID.randomUUID().toString(),
            categoryId = null, title = title, startDate = selectedDate,
            endDate = selectedDate, repeatRule = repeatRule, status = null,
            priority = false, memo = "" , color = color, icon = icon, index = null, startTime = startTime, savedTime = Timestamp.now()
        ))


    }

    fun addTodoInGoal(startDate: String, endDate: String, goalId: String) = viewModelScope.launch {
        val statusMap = mutableMapOf<String, TodoStatus>()

//        for(i in 0 .. LocalDate.parse(endDate) - LocalDate.parse(startDate)) {
//            statusMap[]
//        }


        if(currentTodoInGoal.value.isEmpty()) {

        }
        else if(currentTodoInGoal.value.first().categoryId != goalId) currentTodoInGoal.value = emptyList()

        todoRepository.saveTodo(
            Todo(id = UUID.randomUUID().toString(),
                categoryId = goalId, title = "", startDate = startDate,
                endDate = endDate, repeatRule = null,status = mutableMapOf(startDate to TodoStatus.NONE),
                priority = false, memo = "", color = null, icon = null, index = null, startTime = null,
                savedTime = Timestamp.now()))


    }

    fun updateTodo(todo: Todo) = viewModelScope.launch {
        todoRepository.updateTodo(todo)
    }


    fun deleteTodo(todoId: String) = viewModelScope.launch {
        todoRepository.deleteTodo(todoId)
    }


    fun getPlanList() {

        viewModelScope.launch(Dispatchers.IO) {
            todoRepository.getPlanList().collectLatest { todoList ->
                _selectedDatePlanList.update {
                    todoList
                }
            }

        }

    }

    fun getStartMode() = viewModelScope.launch {

        preferenceStorage.getStartScreenModeFlow().collectLatest {

            startModeFlow.value = it

        }

    }

    fun getSortMode() = viewModelScope.launch {

        preferenceStorage.getSortModeFlow().collectLatest {

            sortModeFlow.value = it

        }

    }

    fun getFirstDayOfWeek() = viewModelScope.launch {
        preferenceStorage.getFirstDayOfWeekFlow().collectLatest {

            firstDayOfWeekFlow.value = it

        }
    }

}