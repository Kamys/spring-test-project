package ru.my.test.model

import javax.validation.constraints.NotNull

class UserView(
    val id: Int,
    val name: String,
)

class userAddRequest(
    @field:NotNull
    val name: String,
)

class userEditRequest(
    @field:NotNull
    val name: String,
)