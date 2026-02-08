package com.garam.shared.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.shared.auth.AuthRepository
import com.garam.shared.data.Category
import com.garam.shared.data.CategoryIconType
import com.garam.shared.data.Todo
import com.garam.shared.data.TodoStatus
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.AccountDao
import com.garam.shared.data.source.local.CategoryDao
import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.toLocal
import com.garam.shared.data.toNetwork
import com.kizitonwose.calendar.core.now
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalTime::class, ExperimentalUuidApi::class)
class LoginViewModel(
//    private val repo : AuthRepository,
    private val todoDao: TodoDao,
    private val categoryDao : CategoryDao,
    private val accountDao: AccountDao,
    private val todoRepository : TodoRepository,
    private val firebaseRepo: NetworkDataSource
) : ViewModel() {

    val category = Category(
        categoryId = Uuid.random().toString(),
        title = "카테고리",
        index = 0,
        icon = CategoryIconType.HOME,
        color = "default_color_1"
    )

    val tutorialTodoList = listOf(
        Todo(
            id = "pre_todo_1",
            title = "",
            categoryId = category.categoryId,
            startDate = LocalDate.now().toString(),
            endDate = LocalDate.now().toString(),
            repeatRule = null,
            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
            priority = false,
            memo = "",
            icon = null,
            color = null,
            startTime = null,
            index = null,
            savedTime = Clock.System.now().epochSeconds
        ),
        Todo(
            id = "pre_todo_2",
            title = "",
            categoryId = category.categoryId,
            startDate = LocalDate.now().toString(),
            endDate = LocalDate.now().toString(),
            repeatRule = null,
            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
            priority = false,
            memo = "",
            icon = null,
            color = null,
            startTime = null,
            index = null,
            savedTime = Clock.System.now().epochSeconds + 10
        ),
        Todo(
            id = "pre_todo_3",
            title = "",
            categoryId = category.categoryId,
            startDate = LocalDate.now().toString(),
            endDate = LocalDate.now().toString(),
            repeatRule = null,
            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.COMPLETED),
            priority = false,
            memo = "",
            icon = null,
            color = null,
            startTime = null,
            index = null,
            savedTime = Clock.System.now().epochSeconds + 20
        )

    )

    fun saveTutorialTodo(uid: String) = viewModelScope.launch {

        tutorialTodoList.forEachIndexed { index, todo->

            todoDao.upsertTodo(todo.toLocal(uid))
            firebaseRepo.saveTodo(todo.toNetwork(), uid)

            if(index == 2) {
                categoryDao.upsertCategory(category.toLocal(uid))
                firebaseRepo.saveCategory(category.toNetwork(), uid)
            }


        }


    }

    fun saveUserData(userData: LocalUserData) = viewModelScope.launch {
        accountDao.saveUserData(userData)
        firebaseRepo.setUserData(userData)
    }


    fun saveTodoList(uid: String) = viewModelScope.launch {

        firebaseRepo.saveTodoList(uid)

    }

    fun saveCategoryList(uid: String) = viewModelScope.launch {
        firebaseRepo.saveCategoryList(uid)
    }

    fun saveGoalList(uid: String) = viewModelScope.launch {

        firebaseRepo.saveGoalList(uid)

    }




}