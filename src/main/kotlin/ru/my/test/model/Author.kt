package ru.my.test.model

import org.hibernate.validator.constraints.UniqueElements
import javax.validation.constraints.NotNull

class AuthorView(
    val id: Long,
    val name: String,
    val bookIds: List<Long> = mutableListOf(),
)

class AuthorAddRequest(
    @field:NotNull
    val name: String,
    @field:UniqueElements
    val bookIds: List<Long> = mutableListOf(),
)

class AuthorEditRequest(
    val name: String,
    @field:UniqueElements
    val bookIds: List<Long> = mutableListOf(),
)