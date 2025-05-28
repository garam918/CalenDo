package com.garam.todolist.data.source.repository

import com.garam.todolist.data.UserData
import com.garam.todolist.data.source.local.LocalUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingRepository {

    suspend fun isExistAccount() : Boolean

    suspend fun setUserInfo(userData: UserData)
    suspend fun getUserInfo() : Flow<UserData?>

    suspend fun logoutAccount(uid: String)
    suspend fun deleteAccount(uid: String)


    suspend fun saveTodoList(uid: String)
    suspend fun saveCategoryList(uid: String)
    suspend fun saveGoalList(uid: String)

    suspend fun getStartMode() : StateFlow<String?>
    suspend fun setStartMode(mode: String)

    suspend fun getSortMode() : StateFlow<String?>
    suspend fun setSortMode(mode: String)

    suspend fun getFirstDayOfWeek() : StateFlow<String?>
    suspend fun setFirstDayOfWeek(firstDayOfWeek: String)
}