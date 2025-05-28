package com.garam.todolist.data.source.network

import com.garam.todolist.data.UserData
import com.garam.todolist.data.UserIdProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val userIdProvider: UserIdProvider) : NetworkDataSource {


    override suspend fun saveTodo(todo: NetworkTodo) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("todos").document(todo.id).set(todo)
    }

    override suspend fun getTodoList(): List<NetworkTodo> = firestore.collection("users")
        .document(userIdProvider.currentUserId).collection("todos").get().await().toObjects(NetworkTodo::class.java)

    override suspend fun setUserData(userData : UserData) {
        firestore.collection("users").document(userIdProvider.currentUserId).set(userData)
    }

    override suspend fun deleteTodo(todoId: String) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("todos").document(todoId).delete()
    }

    override suspend fun updateTodo(todo: NetworkTodo) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("todos").document(todo.id).set(todo,
            SetOptions.merge()
        )
    }

    override suspend fun saveGoal(goal: NetworkGoal) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("goals")
            .document(goal.goalId).set(goal)
    }

    override suspend fun saveCategory(category: NetworkCategory) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("categories")
            .document(category.categoryId).set(category)
    }

    override suspend fun updateCategory(category: NetworkCategory) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("categories")
            .document(category.categoryId).set(category, SetOptions.merge())
    }

    override suspend fun deleteCategory(categoryId: String) {
        firestore.collection("users").document(userIdProvider.currentUserId).collection("categories")
            .document(categoryId).delete()
    }
}