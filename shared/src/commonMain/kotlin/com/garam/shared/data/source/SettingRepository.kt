package com.garam.shared.data.source

import com.garam.shared.data.Category
import com.garam.shared.data.Todo
import com.garam.shared.data.source.local.LocalUserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface SettingRepository {
    suspend fun isExistAccount() : Boolean

    suspend fun setUserInfo(userData: LocalUserData)
    suspend fun getUserInfo() : Flow<LocalUserData?>

    suspend fun logoutAccount(uid: String)
    suspend fun deleteAccount(uid: String)

    suspend fun upsertCategory(category: Category, uid: String)

    suspend fun deleteCategory(categoryId : String, uid: String)

    suspend fun saveTodoList(uid: String)
    suspend fun saveCategoryList(uid: String)
    suspend fun saveGoalList(uid: String)
}