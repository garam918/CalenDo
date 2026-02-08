package com.garam.shared.data.source.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.garam.shared.data.CategoryIconType

@Entity
    (
    tableName = "category"
)
data class LocalCategory(
    @PrimaryKey val categoryId : String,
    var title : String,
    var index : Int,
    var icon : CategoryIconType,
    var color : String,
    var userId : String
)
