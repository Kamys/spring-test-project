package ru.my.test.model

import javax.validation.constraints.*

class BookView(
    val id: Int,
    val name: String,
    val authors: List<AuthorView> = emptyList()
)

class BookAddRequest(
    @field:NotNull
    val name: String
)

class BookEditRequest(
    @field:NotNull
    val name: String
)