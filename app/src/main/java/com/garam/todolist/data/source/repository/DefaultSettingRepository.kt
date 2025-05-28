package com.garam.todolist.data.source.repository

import com.garam.todolist.data.UserData
import com.garam.todolist.data.UserIdProvider
import com.garam.todolist.data.source.SharedPreferenceStorage
import com.garam.todolist.data.source.local.AccountDao
import com.garam.todolist.data.source.local.TodoDao
import com.garam.todolist.data.source.network.FirebaseUserData
import com.garam.todolist.data.source.network.NetworkCategory
import com.garam.todolist.data.source.network.NetworkGoal
import com.garam.todolist.data.source.network.NetworkTodo
import com.garam.todolist.data.toExternal
import com.garam.todolist.data.toLocal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DefaultSettingRepository @Inject constructor(
    private val accountDataSource : AccountDao,
    private val todoDao : TodoDao,
    private val userIdProvider: UserIdProvider,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val preferenceStorage: SharedPreferenceStorage

) : SettingRepository {

    override suspend fun isExistAccount(): Boolean = firestore.collection("users").document(auth.currentUser?.uid.toString()).get().await().exists()


    override suspend fun setUserInfo(userData: UserData) = accountDataSource.saveUserData(userData.toLocal())

    override suspend fun getUserInfo(): Flow<UserData?> {

        return flowOf(firestore.collection("users").document(auth.currentUser?.uid.toString()).get().await().toObject(
            FirebaseUserData::class.java)?.toExternal())

//        accountDataSource.getUserInfo(userIdProvider.currentUserId)?.toExternal()

    }


    override suspend fun logoutAccount(uid: String) {

        auth.signOut()

    }

    override suspend fun deleteAccount(uid: String) {

        val user = auth.currentUser
        user?.delete()?.await()
        firestore.collection("users").document(uid).delete().await()

    }

    override suspend fun saveTodoList(uid: String) {
        firestore.collection("users").document(uid).collection("todos").get().addOnSuccessListener { result ->

            val todos = result.documents.mapNotNull { it.toObject(NetworkTodo::class.java) }
            val localTodos = todos.map { it.toLocal().toLocal(uid) }

            CoroutineScope(Dispatchers.IO).launch {
                todoDao.upsertTodoList(localTodos)
            }

        }
    }

    override suspend fun saveCategoryList(uid: String) {
        firestore.collection("users").document(uid).collection("categories").get().addOnSuccessListener { result ->

            val categories = result.documents.mapNotNull { it.toObject(NetworkCategory::class.java) }
            val localCategories = categories.map { it.toLocal(uid) }

            CoroutineScope(Dispatchers.IO).launch {
                todoDao.upsertCategoryList(localCategories)
            }

        }
    }

    override suspend fun saveGoalList(uid: String) {
        firestore.collection("users").document(uid).collection("goals").get().addOnSuccessListener { result ->

            val goals = result.documents.mapNotNull { it.toObject(NetworkGoal::class.java) }
            val localGoals = goals.map { it.toLocal(uid) }

            CoroutineScope(Dispatchers.IO).launch {
                todoDao.upsertGoalList(localGoals)
            }

        }
    }

    override suspend fun getStartMode(): StateFlow<String?> = preferenceStorage.getStartScreenModeFlow()

    override suspend fun setStartMode(mode: String) {
        preferenceStorage.saveStartScreenMode(mode)
    }

    override suspend fun getSortMode(): StateFlow<String?> = preferenceStorage.getSortModeFlow()


    override suspend fun setSortMode(mode: String) = preferenceStorage.saveSortMode(mode)

    override suspend fun getFirstDayOfWeek(): StateFlow<String?> = preferenceStorage.getFirstDayOfWeekFlow()


    override suspend fun setFirstDayOfWeek(firstDayOfWeek: String) = preferenceStorage.saveFirstDayOfWeek(firstDayOfWeek)


}