package ru.my.test.model

import org.hibernate.validator.constraints.Length
import ru.my.test.entity.BookRating
import javax.validation.constraints.NotNull

class ReviewView(
    val id: Long,
    val text: String,
    val rating: BookRating
)

class ReviewAddRequest(
    @field:Length(min = 5, max = 200)
    val text: String,
    @field:NotNull
    val rating: BookRating,
)

class ReviewEditRequest(
    @field:Length(min = 5, max = 200)
    val text: String,
    val rating: BookRating,
)