package com.garam.shared.data.source.network

import cocoapods.FirebaseAuth.FIRAuth
import cocoapods.FirebaseFirestoreInternal.FIRDocumentSnapshot
import cocoapods.FirebaseFirestoreInternal.FIRFirestore
import com.garam.shared.data.source.local.LocalUserData
import com.garam.shared.data.source.local.TodoDao
import com.garam.shared.data.toLocal
import com.garam.shared.util.functions.safeToBoolean
import com.garam.shared.util.functions.safeToInt
import com.garam.shared.util.functions.safeToLong
import com.garam.shared.util.functions.safeToMapStringString
import com.garam.shared.util.functions.toMap
import com.garam.shared.util.functions.toNSDictionary
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.internal.firebaseSerializer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.Foundation.NSDictionary
import kotlin.toString

@OptIn(ExperimentalForeignApi::class)
class NetworkDataSourceImpl(private val todoDao: TodoDao) : NetworkDataSource {

    val firestore = Firebase.firestore

    val currentUser = FIRAuth.auth().currentUser()

    override suspend fun setUserData(userData: LocalUserData) {

        suspendCancellableCoroutine { continuation ->

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(userData.uid)
                .setData(
                    mapOf(
                        "email" to userData.email,
                        "loginType" to userData.loginType,
                        "uid" to userData.uid
                    )
                )

//            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(userData.uid)
//                .setData(userData.toNSDictionary())

//            firestore.collection("users").document(userData.uid).set(userData)

            continuation.resumeWith(Result.success(Unit))

        }


    }

    override suspend fun saveTodo(todo: NetworkTodo, uid: String) {
        suspendCancellableCoroutine { continuation ->

            FIRFirestore.firestore().collectionWithPath("users")
                .documentWithPath(uid)
                .collectionWithPath("todos").documentWithPath(todo.id)
                .setData(todo.toMap())
//            firestore.collection("users").document(currentUser?.uid().toString())
//                    .collection("todos").document(todo.id).set(todo.toMap())
            continuation.resumeWith(Result.success(Unit))


        }
    }

    override suspend fun getTodoList(): List<NetworkTodo> = firestore.collection("users")
        .document(currentUser?.uid().toString()).collection("todos")
        .get().documents.map { it.data<NetworkTodo>() }

    override suspend fun deleteTodo(todoId: String) {
        suspendCancellableCoroutine { continuation ->

            FIRFirestore.firestore().collectionWithPath("users")
                .documentWithPath(currentUser?.uid().toString())
                .collectionWithPath("todos").documentWithPath(todoId).deleteDocument()

//            suspend {
//        firestore.collection("users").document(currentUser?.uid().toString()).collection("todos")
//            .document(todoId).delete()

            continuation.resumeWith(Result.success(Unit))

//            }
        }
    }

    override suspend fun updateTodo(todo: NetworkTodo, uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("todos").documentWithPath(todo.id)
                .setData(todo.toMap(), merge = true)

//            firestore.collection("users").document(currentUser?.uid().toString())
//                .collection("todos").document(todo.id).set(todo, merge = true)
            it.resumeWith(Result.success(Unit))
        }
    }

    override suspend fun saveGoal(goal: NetworkGoal, uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users")
                .documentWithPath(uid).collectionWithPath("goals")
                .documentWithPath(goal.goalId).setData(goal.toMap())


//            suspend {
//                firestore.collection("users").document(currentUser?.uid().toString())
//                    .collection("goals")
//                    .document(goal.goalId).set(goal)
            it.resumeWith(Result.success(Unit))
//            }
        }
    }

    override suspend fun saveCategory(category: NetworkCategory, uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("categories").documentWithPath(category.categoryId)
                .setData(category.toMap())

//            firestore.collection("users").document(currentUser?.uid().toString())
//                .collection("categories")
//                .document(category.categoryId).set(category)


            it.resumeWith(Result.success(Unit))
        }
    }

    override suspend fun updateCategory(category: NetworkCategory) {
        suspendCancellableCoroutine {
            suspend {
                firestore.collection("users").document(currentUser?.uid().toString())
                    .collection("categories")
                    .document(category.categoryId).set(category, merge = true)
                it.resumeWith(Result.success(Unit))
            }
        }
    }

    override suspend fun deleteCategory(categoryId: String, uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("categories").documentWithPath(categoryId).deleteDocument()

            it.resumeWith(Result.success(Unit))
        }
    }

    override suspend fun saveTodoList(uid: String) {

        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("todos").getDocumentsWithCompletion { documents, error ->

                    try {


                        val todos = documents?.documents?.mapNotNull {

                            val snapshot = it as FIRDocumentSnapshot

                            val todo = snapshot.data() as Map<*, *>
                            println("network : $todo")

                            NetworkTodo(
                                id = todo["id"] as? String ?: "",
                                title = todo["title"].toString(),
                                categoryId = todo["categoryId"].toString(),
                                startDate = todo["startDate"].toString(),
                                endDate = todo["endDate"].toString(),
                                repeatRule = if(todo["repeatRule"] == null) null else todo["repeatRule"].toString(),
                                status = todo["status"].safeToMapStringString(),
                                priority = todo["priority"].safeToBoolean(),
                                memo = todo["memo"].toString(),
                                icon = todo["icon"].toString(),
                                color = todo["color"].toString(),
                                startTime = todo["startTime"].toString(),
                                index = todo["index"].safeToInt(),
                                savedTime = todo["savedTime"].safeToLong() ?: 0L
                            )
                        }


                        val localTodos = todos?.map { it.toLocal(uid) }

                        CoroutineScope(Dispatchers.IO).launch {
                            localTodos?.let {
                                it.forEach { todo->
                                    println("room : $todo")
                                }
                                todoDao.upsertTodoList(it)
                            }

                        }

                        it.resumeWith(Result.success(Unit))
                    }
                    catch (e: Throwable) {

                        it.resumeWith(Result.failure(e))
                    }
                }
        }
    }

    override suspend fun saveCategoryList(uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("categories").getDocumentsWithCompletion { documents, error ->


                    val categories = documents?.documents?.mapNotNull {

                        val snapshot = it as FIRDocumentSnapshot

                        val category = snapshot.data() as Map<*, *>
                        NetworkCategory(
                            categoryId = category["categoryId"].toString(),
                            color = category["color"].toString(),
                            title = category["title"].toString(),
                            icon = category["icon"].toString(),
                            index = category["index"].toString().toInt()
                        )
                    }

                    val localCategories = categories?.map { it.toLocal(uid) }


                    CoroutineScope(Dispatchers.IO).launch {
                        localCategories?.let {
                            todoDao.upsertCategoryList(it)
                        }

                    }
                }

            it.resumeWith(Result.success(Unit))
        }
    }

    override suspend fun saveGoalList(uid: String) {
        suspendCancellableCoroutine {

            FIRFirestore.firestore().collectionWithPath("users").documentWithPath(uid)
                .collectionWithPath("goals").getDocumentsWithCompletion { documents, error ->

                    val goals = documents?.documents?.mapNotNull {

                        val snapshot = it as FIRDocumentSnapshot


                        val goal = snapshot.data() as Map<*,*>
                        NetworkGoal(goalId = goal["goalId"].toString(),
                            title = goal["title"].toString(),
                            startDate = goal["startDate"].toString(),
                            endDate = goal["endDate"].toString(),
                            type = goal["type"].toString())
                    }

                    val localGoals = goals?.map { it.toLocal(uid) }

                    CoroutineScope(Dispatchers.IO).launch {
                        localGoals?.let {
                            todoDao.upsertGoalList(it)
                        }

                    }
                }

            it.resumeWith(Result.success(Unit))
        }
    }
}
