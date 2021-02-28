package com.rodrigoja.randomuser.data.database

import com.rodrigoja.randomuser.model.User
import com.rodrigoja.randomuser.model.UserId

fun UserEntity.toUser() = User(
    this.email,
    this.name,
    this.picture,
    this.phone,
    this.cell,
    UserId(this.idUser.split("-")[0], this.idUser.split("-")[1])
)

fun List<UserEntity>.toUserList() = this.map { it.toUser() }

fun User.toDataEntity() = UserEntity(
    email = this.email,
    name = this.name,
    picture = this.picture,
    phone = this.phone,
    cell = this.cell,
    idUser = "${this.id.name}-${this.id.value}"
)

fun List<User>.toDataEntityList() = this.map { it.toDataEntity() }