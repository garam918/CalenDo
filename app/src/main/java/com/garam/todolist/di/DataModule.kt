package com.garam.todolist.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.garam.todolist.data.CategoryIconType
import com.garam.todolist.data.source.repository.DefaultTodoRepository
import com.garam.todolist.data.source.repository.TodoRepository
import com.garam.todolist.data.TodoStatus
import com.garam.todolist.data.UserIdProvider
import com.garam.todolist.data.source.SharedPreferenceStorage
import com.garam.todolist.data.source.local.AccountDao
import com.garam.todolist.data.source.local.CategoryDao
import com.garam.todolist.data.source.local.GoalDao
import com.garam.todolist.data.source.local.LocalCategory
import com.garam.todolist.data.source.local.LocalTodo
import com.garam.todolist.data.source.local.TodoDao
import com.garam.todolist.data.source.local.TodoDatabase
import com.garam.todolist.data.source.network.DefaultNetworkMonitor
import com.garam.todolist.data.source.network.FirebaseUserIdProvider
import com.garam.todolist.data.source.network.FirestoreDataSource
import com.garam.todolist.data.source.network.NetworkDataSource
import com.garam.todolist.data.source.network.NetworkMonitor
import com.garam.todolist.data.source.repository.DefaultSettingRepository
import com.garam.todolist.data.source.repository.SettingRepository
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindTodoRepository(repository : DefaultTodoRepository): TodoRepository

    @Singleton
    @Binds
    abstract fun bindSettingRepository(repository : DefaultSettingRepository): SettingRepository
}


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    fun provideNetworkDataSource(
        firestore: FirebaseFirestore,
        userIdProvider : UserIdProvider
    ): NetworkDataSource = FirestoreDataSource(firestore, userIdProvider)

}

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    fun provideUserIdProvider(): UserIdProvider = FirebaseUserIdProvider()

}

@Module
@InstallIn(SingletonComponent::class)
object NetworkMonitorModule {

    @Provides
    fun provideNetworkMonitor(@ApplicationContext context: Context): NetworkMonitor = DefaultNetworkMonitor(context)
}

@Module
@InstallIn(SingletonComponent::class)
object SharePreferenceModule {

    @Provides
    fun provideSharedPreferenceStorage(@ApplicationContext context: Context) = SharedPreferenceStorage(context)

}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context : Context) : TodoDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            TodoDatabase::class.java,
            "Todo.db"
        ).addCallback(object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // DB 생성 시 기본 데이터 삽입 : 카테고리, 일정
                CoroutineScope(Dispatchers.IO).launch {

                    val preCategory = LocalCategory(
                        categoryId = UUID.randomUUID().toString(),
                        title = "카테고리",
                        index = 0,
                        icon = CategoryIconType.HOME,
                        color = "default_color_1",
                        userId = ""
                    )
//                    provideDataBase(context).categoryDao().insertCategory(preCategory)

                    val preTodoList = listOf(
                        LocalTodo(
                            id = "pre_todo_1",
                            title = "",
                            categoryId = preCategory.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            userId = "",
                            savedTime = Timestamp.now().toDate().time
                        ),
                        LocalTodo(
                            id = "pre_todo_2",
                            title = "",
                            categoryId = preCategory.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.NONE),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            userId = "",
                            savedTime = Timestamp.now().toDate().time
                        ),
                        LocalTodo(
                            id = "pre_todo_3",
                            title = "",
                            categoryId = preCategory.categoryId,
                            startDate = LocalDate.now().toString(),
                            endDate = LocalDate.now().toString(),
                            repeatRule = null,
                            status = mutableMapOf(LocalDate.now().toString() to TodoStatus.COMPLETED),
                            priority = false,
                            memo = "",
                            icon = null,
                            color = null,
                            startTime = null,
                            index = null,
                            userId = "",
                            savedTime = Timestamp.now().toDate().time
                        ))


//                    preTodoList.forEach {
//                        provideDataBase(context).todoDao().saveTodo(it)
//                    }

                }
            }
        }).build()

    }

    @Provides
    fun provideTodoDao(database : TodoDatabase) : TodoDao = database.todoDao()

    @Provides
    fun provideCategoryDao(database: TodoDatabase) : CategoryDao = database.categoryDao()

    @Provides
    fun provideGoalDao(database: TodoDatabase) : GoalDao = database.goalDao()

    @Provides
    fun provideAccountDao(database: TodoDatabase) : AccountDao = database.accountDao()

    @Provides
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()

    @Provides
    fun provideUserId() = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User is not logged in")

}