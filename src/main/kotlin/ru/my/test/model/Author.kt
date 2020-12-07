package ru.my.test.model

import javax.validation.constraints.NotNull

class AuthorView(
    val id: Long,
    val name: String,
    val bookIds: List<Long> = mutableListOf(),
)

class AuthorAddRequest(
    @field:NotNull
    val name: String
)

class AuthorEditRequest(
    val name: String
)