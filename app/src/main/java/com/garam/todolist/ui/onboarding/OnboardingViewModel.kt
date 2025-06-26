package com.garam.todolist.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.todolist.data.Category
import com.garam.todolist.data.Todo
import com.garam.todolist.data.UserData
import com.garam.todolist.data.source.network.NetworkDataSource
import com.garam.todolist.data.source.repository.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val settingRepository: SettingRepository
) : ViewModel() {

    val currentPage = MutableStateFlow(0)
    val isExistAccount = MutableStateFlow(false)


    suspend fun isExistAccount() = viewModelScope.launch {
        isExistAccount.value = settingRepository.isExistAccount()
    }

    fun setUserData(userData: UserData) = viewModelScope.launch {
        settingRepository.setUserInfo(userData)
        networkDataSource.setUserData(userData)
    }

    suspend fun saveTodoList() = viewModelScope.launch {

        settingRepository.getUserInfo().collectLatest {
            val uid = it?.uid.toString()
            settingRepository.saveTodoList(uid)
            settingRepository.saveCategoryList(uid)
            settingRepository.saveGoalList(uid)
        }


//        settingRepository.saveTodoList(settingRepository.getUserInfo()!!.uid)
//        settingRepository.saveCategoryList(settingRepository.getUserInfo()!!.uid)
//        settingRepository.saveGoalList(settingRepository.getUserInfo()!!.uid)


    }

    suspend fun saveTutorialTodo(category: Category, todoList: List<Todo>, uid: String) = viewModelScope.launch {
        todoList.forEachIndexed { index, todo ->
            settingRepository.upsertTodo(todo, uid)

            if(index == 2) settingRepository.upsertCategory(category, uid)
        }
    }
}