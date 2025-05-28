package com.garam.todolist.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface AccountDao {

    @Upsert
    suspend fun saveUserData(userData : LocalUserData)

    @Query("SELECT * FROM user WHERE uid = :uid")
    suspend fun getUserInfo(uid : String) : LocalUserData?

}