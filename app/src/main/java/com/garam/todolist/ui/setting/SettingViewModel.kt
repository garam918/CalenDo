package com.garam.todolist.ui.setting

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.todolist.data.Category
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.UserData
import com.garam.todolist.data.source.network.NetworkDataSource
import com.garam.todolist.data.source.repository.SettingRepository
import com.garam.todolist.data.source.repository.TodoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val networkDataSource: NetworkDataSource,
    private val settingRepository: SettingRepository,
    private val todoRepository: TodoRepository
) : ViewModel() {


    val userInfo = MutableStateFlow<UserData?>(null)
    val isExistAccount = MutableStateFlow(false)
    private val _categoryList = MutableStateFlow<List<Category>>(emptyList())
    val categoryList : StateFlow<List<Category>> = _categoryList

    val startModeFlow = MutableStateFlow<String?>("CalenDo")
    val sortModeFlow = MutableStateFlow<String?>("Saved")
    val firstDayOfWeekFlow = MutableStateFlow<String?>("Mon")

    init {

        getUserInfoData()
        getCategoryList()

        getStartMode()
        getSortMode()
        getFirstDayOfWeek()
    }


    suspend fun isExistAccount() = viewModelScope.launch {
        isExistAccount.value = settingRepository.isExistAccount()
    }

    suspend fun setUserData(userData: UserData) = viewModelScope.launch {
        settingRepository.setUserInfo(userData)
        networkDataSource.setUserData(userData)
    }

    suspend fun saveTodoList() = viewModelScope.launch {

        val uid = userInfo.value?.uid.toString()
        settingRepository.saveTodoList(uid)
        settingRepository.saveCategoryList(uid)
        settingRepository.saveGoalList(uid)


    }

    fun addCategory(title: String, categoryIconType: CategoryIconType, categoryColor : String) = viewModelScope.launch {
        todoRepository.saveCategory(
            Category(categoryId = UUID.randomUUID().toString()
                , title = title,
                index = categoryList.value.size , icon = categoryIconType,
                color = categoryColor)
        )
    }

    fun updateCategory(category: Category) = viewModelScope.launch {

        todoRepository.updateCategory(category)

    }

    fun deleteCategory(categoryId: String) = viewModelScope.launch {

        todoRepository.deleteCategory(categoryId)
        networkDataSource.deleteCategory(categoryId)

    }

    fun getUserInfoData() {

        viewModelScope.launch {
            settingRepository.getUserInfo().collectLatest {
                userInfo.value = it
//                {
//                    it
//                }
            }

        }

    }

    fun getCategoryList() = viewModelScope.launch(Dispatchers.IO) {
         // flow는 collect를 해야 값이 지속적으로 업데이트 됨. value에 직접 값을 넣으면 단순 값을 복사하게 됨
        todoRepository.getCategoryList().collectLatest { list ->
            _categoryList.update {
                list
            }

        }
    }

    fun getStartMode() = viewModelScope.launch {

        settingRepository.getStartMode().collectLatest {

            startModeFlow.value = it
        }

    }

    fun getSortMode() = viewModelScope.launch {

        settingRepository.getSortMode().collectLatest {

            sortModeFlow.value = it
        }

    }

    fun getFirstDayOfWeek() = viewModelScope.launch {

        settingRepository.getFirstDayOfWeek().collectLatest {

            firstDayOfWeekFlow.value = it

        }

    }

    fun saveStartMode(mode: String) = viewModelScope.launch {
        settingRepository.setStartMode(mode)
    }

    fun saveSortMode(mode: String) = viewModelScope.launch {

        settingRepository.setSortMode(mode)

    }

    fun saveFirstDayOfWeek(firstDayOfWeek: String) = viewModelScope.launch {

        settingRepository.setFirstDayOfWeek(firstDayOfWeek)
    }

    fun logoutAccount(uid: String) = viewModelScope.launch {

        settingRepository.logoutAccount(uid)

    }

    fun deleteAccount(uid: String) = viewModelScope.launch {

        settingRepository.deleteAccount(uid)

    }

}