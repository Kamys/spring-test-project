package ru.my.test.model

import org.hibernate.validator.constraints.UniqueElements
import ru.my.test.helper.validation.UniqueBookName
import javax.validation.constraints.NotNull

class BookView(
    val id: Int,
    val name: String,
    val authorIds: List<Int> = emptyList()
)

class BookAddRequest(
    @field:NotNull
    @field:UniqueBookName
    val name: String,
    @field:UniqueElements
    val authorIds: List<Int>? = null,
)

class BookEditRequest(
    @field:UniqueBookName
    val name: String? = null,
    @field:UniqueElements
    val authorIds: List<Int>? = null,
)