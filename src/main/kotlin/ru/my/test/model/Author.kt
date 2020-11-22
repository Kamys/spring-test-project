package ru.my.test.model

import org.hibernate.validator.constraints.UniqueElements
import ru.my.test.entity.User
import javax.validation.constraints.NotNull

class AuthorView(
    val id: Int,
    val descriptionOfWrittenStyle: String?,
    val bookIds: List<Int>,
    val user: User
)

class AuthorAddRequest(
    @field:NotNull
    val userId: Int,
    @field:NotNull
    val descriptionOfWrittenStyle: String,
    @field:UniqueElements
    val bookIds: List<Int>? = null
)

class AuthorEditRequest(
    val descriptionOfWrittenStyle: String? = null,
    @field:UniqueElements
    val bookIds: List<Int>? = null
)