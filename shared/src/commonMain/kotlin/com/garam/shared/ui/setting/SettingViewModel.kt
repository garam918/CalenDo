package com.garam.shared.ui.setting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.garam.shared.auth.AuthRepository
import com.garam.shared.data.Category
import com.garam.shared.data.source.SettingRepository
import com.garam.shared.data.source.TodoRepository
import com.garam.shared.data.source.local.AccountDao
import com.garam.shared.data.source.local.CategoryDao
import com.garam.shared.data.source.local.LocalCategory
import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.toExternal
import com.garam.shared.data.toLocal
import com.garam.shared.data.toNetwork
import com.garam.shared.notification.createNotificationScheduler
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SettingViewModel(
    private val authRepository: AuthRepository,
    private val accountDao: AccountDao,
    private val categoryDao : CategoryDao,
    private val todoRepository: TodoRepository,
    private val settingRepository: SettingRepository
) : ViewModel() {

    private val _userInfo = MutableStateFlow<LocalUserData?>(null)
    val userInfo = _userInfo.asStateFlow()

    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories = _categories.asStateFlow()

    init {

        getCurrentUser()

        viewModelScope.launch {
            userInfo.filterNotNull().collectLatest { user ->
                categoryDao.getAllCategory(user.uid).collectLatest { list ->
                    _categories.value = list.toExternal().sortedBy { it.index }
                }
            }
        }
//        getCategory()

    }

    fun getCurrentUser() {

        _userInfo.value = authRepository.currentUser()

//        accountDao.getUserInfo()

    }

    fun upsertCategory(category: Category) = viewModelScope.launch {
        settingRepository.upsertCategory(category, userInfo.value?.uid.toString())
    }

    fun deleteCategory(categoryId: String) = viewModelScope.launch {

        settingRepository.deleteCategory(categoryId, userInfo.value?.uid.toString())
    }

    fun deleteAccount() = viewModelScope.launch {

//        accountDao

        authRepository.deleteAccount()

    }

    fun signOut() = viewModelScope.launch {

        authRepository.signOut()

    }

    fun setNotification(type: String, time: String) {

        val scheduler = createNotificationScheduler(todoRepository)

        val amPm = time.split(" ")[0]
        val timeText = time.split(" ")[1]

        val hour = timeText.split(":")[0].toInt()
        val minute = timeText.split(":")[1].toInt()


        when(type) {
            "Todo" -> scheduler.scheduleDailyNotification("TodoNoti",type, hour = if(amPm == "오후") {
                if(hour == 12) hour
                else hour + 12
            }  else hour, minute = minute)
            "Plan" -> scheduler.scheduleDailyNotification("PlanNoti",type, hour = if(amPm == "오후") {
                if(hour == 12) hour
                else hour + 12
            }  else hour, minute = minute)
        }


    }

    fun cancelNotification(type: String) {

        val scheduler = createNotificationScheduler(todoRepository)

        when(type) {
            "Todo" -> scheduler.cancelNotification("TodoNoti")
            "Plan" -> scheduler.cancelNotification("PlanNoti")
        }


    }


}