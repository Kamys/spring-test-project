package ru.my.test.model

import org.hibernate.validator.constraints.Length
import ru.my.test.entity.BookRating
import javax.validation.constraints.NotNull

class ReviewView(
    val id: Int,
    val text: String,
    val rating: BookRating,
    val bookId: Int,
)

class ReviewAddRequest(
    @field:Length(min = 5, max=200)
    val text: String?,
    @field:NotNull
    val rating: BookRating,
    @field:NotNull
    val bookId: Int,
)

class ReviewEditRequest(
    @field:Length(min = 5, max=200)
    val text: String? = null,
    val rating: BookRating? = null,
    val bookId: Int? = null,
)