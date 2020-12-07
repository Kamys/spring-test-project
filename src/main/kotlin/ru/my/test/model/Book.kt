package ru.my.test.model

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import org.hibernate.validator.constraints.UniqueElements
import ru.my.test.helper.validation.UniqueBookName
import javax.validation.constraints.NotNull

@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator::class, property = "id")
class BookView(
    val id: Long,
    val name: String,
    val authorIds: List<Long> = mutableListOf(),
    val reviews: List<ReviewView> = mutableListOf(),
)

class BookAddRequest(
    @field:NotNull
    @field:UniqueBookName
    val name: String,
    @field:UniqueElements
    val authorIds: List<Long> = mutableListOf(),
)

class BookEditRequest(
    @field:UniqueBookName
    val name: String,
    @field:UniqueElements
    val authorIds: List<Long> = mutableListOf(),
)