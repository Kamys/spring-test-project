package ru.my.test.model

import javax.validation.constraints.*

class AuthorView(
    val id: Int,
    val name: String
)

class AuthorAddRequest(
    @field:NotNull
    val name: String,
    val bookIds: List<String>?
)

class AuthorEditRequest(
    @field:NotNull
    val name: String,
    @field:NotNull
    val bookIds: List<String>?
)