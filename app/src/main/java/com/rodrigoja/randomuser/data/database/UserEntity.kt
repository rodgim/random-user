package com.rodrigoja.randomuser.data.database

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.rodrigoja.randomuser.model.UserName
import com.rodrigoja.randomuser.model.UserPicture

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Int = 0,
    @ColumnInfo(name = "email")
    val email: String,
    @Embedded(prefix = "name")
    val name: UserName,
    @Embedded(prefix = "picture")
    val picture: UserPicture,
    @ColumnInfo(name = "phone")
    val phone: String,
    @ColumnInfo(name = "cell")
    val cell: String,
    @ColumnInfo(name = "idUser")
    val idUser: String
)
