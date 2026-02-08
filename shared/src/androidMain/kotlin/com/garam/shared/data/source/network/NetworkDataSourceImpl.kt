package com.garam.shared.data.source.network

import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.source.network.NetworkCategory
import com.garam.shared.data.source.network.NetworkDataSource
import com.garam.shared.data.source.network.NetworkGoal
import com.garam.shared.data.source.network.NetworkTodo
import com.garam.shared.data.toLocal
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class NetworkDataSourceImpl(private val todoDao: TodoDao) : NetworkDataSource {
    val firestore = FirebaseFirestore.getInstance()
    val currentUser = Firebase.auth

    override suspend fun setUserData(userData: LocalUserData) {
        firestore.collection("users").document(userData.uid).set(userData)
    }

    override suspend fun saveTodo(todo: NetworkTodo, uid: String) {
        firestore.collection("users").document(currentUser.uid.toString())
            .collection("todos").document(todo.id).set(todo)
    }

    override suspend fun getTodoList(): List<NetworkTodo> = firestore.collection("users")
        .document(currentUser.uid.toString()).collection("todos").get().await().toObjects(NetworkTodo::class.java)

    override suspend fun deleteTodo(todoId: String) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("todos").document(todoId).delete()
    }

    override suspend fun updateTodo(todo: NetworkTodo, uid: String) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("todos").document(todo.id).set(todo,
            SetOptions.merge()
        )
    }

    override suspend fun saveGoal(goal: NetworkGoal, uid: String) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("goals")
            .document(goal.goalId).set(goal)
    }

    override suspend fun saveCategory(category: NetworkCategory, uid: String) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("categories")
            .document(category.categoryId).set(category)
    }

    override suspend fun updateCategory(category: NetworkCategory) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("categories")
            .document(category.categoryId).set(category, SetOptions.merge())
    }

    override suspend fun deleteCategory(categoryId: String, uid: String) {
        firestore.collection("users").document(currentUser.uid.toString()).collection("categories")
            .document(categoryId).delete()
    }

    override suspend fun saveTodoList(uid: String) {
        firestore.collection("users").document(uid).collection("todos").get().addOnSuccessListener { result ->

            val todos = result.documents.mapNotNull { it.toObject(NetworkTodo::class.java) }
            val localTodos = todos.map { it.toLocal(uid) }

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
}