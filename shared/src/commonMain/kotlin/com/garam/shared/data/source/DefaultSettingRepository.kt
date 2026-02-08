package com.garam.shared.data.source

import com.garam.shared.data.Category
import com.garam.shared.data.Todo
import com.garam.shared.data.source.local.AccountDao
import com.garam.shared.data.source.local.CategoryDao
import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.toLocal
import com.garam.shared.data.toNetwork
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

class DefaultSettingRepository(
    private val networkDataSource: NetworkDataSource,
    private val accountDao : AccountDao,
    private val todoDao : TodoDao,
    private val categoryDao : CategoryDao,

    ) : SettingRepository {

    override suspend fun isExistAccount(): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun setUserInfo(userData: LocalUserData) {
        TODO("Not yet implemented")
    }

    override suspend fun getUserInfo(): Flow<LocalUserData?> {
        TODO("Not yet implemented")
    }

    override suspend fun logoutAccount(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAccount(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun upsertCategory(
        category: Category,
        uid: String
    ) {
        categoryDao.upsertCategory(
            category.toLocal(uid)
        )
        networkDataSource.updateCategory(category.toNetwork())
    }

    override suspend fun deleteCategory(categoryId: String, uid: String) {
        categoryDao.deleteCategory(categoryId)
        networkDataSource.deleteCategory(categoryId, uid)
    }

    override suspend fun saveTodoList(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveCategoryList(uid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun saveGoalList(uid: String) {
        TODO("Not yet implemented")
    }
}