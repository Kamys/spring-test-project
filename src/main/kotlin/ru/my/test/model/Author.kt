package ru.my.test.model

import java.time.OffsetDateTime
import javax.validation.constraints.*

class AuthorView(
    val id: Int,
    val name: String,
    val dateOfBirth: OffsetDateTime,
    val books: List<BookView>
)

class AuthorAddRequest(
    @field:NotNull
    val name: String,
    val dateOfBirth: OffsetDateTime,
    val bookIds: List<String>
)

class AuthorEditRequest(
    @field:NotNull
    val name: String,
    val dateOfBirth: OffsetDateTime,
    val bookIds: List<String>
)