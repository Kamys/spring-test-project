package ru.my.test.model

import org.hibernate.validator.constraints.UniqueElements
import javax.validation.constraints.NotNull

class AuthorView(
    val id: Int,
    val name: String,
    val bookIds: List<Int>
)

class AuthorAddRequest(
    @field:NotNull
    val name: String,
    @field:UniqueElements
    val bookIds: List<Int>? = null
)

class AuthorEditRequest(
    val name: String? = null,
    @field:UniqueElements
    val bookIds: List<Int>? = null
)