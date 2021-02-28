package com.rodrigoja.randomuser.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class User(
    @SerializedName("email")
    val email: String = "",
    @SerializedName("name")
    val name: UserName,
    @SerializedName("picture")
    val picture: UserPicture,
    @SerializedName("phone")
    val phone: String = "",
    @SerializedName("cell")
    val cell: String = "",
    @SerializedName("id")
    val id: UserId
): Serializable

data class UserId(
    @SerializedName("id")
    val name: String = "",
    @SerializedName("value")
    val value: String = ""
): Serializable

data class UserName(
    @SerializedName("title")
    val title: String = "",
    @SerializedName("first")
    val first: String = "",
    @SerializedName("last")
    val last: String = ""
): Serializable

data class UserPicture(
    @SerializedName("large")
    val large: String = "",
    @SerializedName("medium")
    val medium: String = "",
    @SerializedName("thumbnail")
    val thumbnail: String = ""
): Serializable

data class UsersResult(
    @SerializedName("results")
    var results: List<User> = emptyList()
): Serializable