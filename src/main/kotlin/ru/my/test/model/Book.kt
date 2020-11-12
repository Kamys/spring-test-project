package ru.my.test.model

import javax.validation.constraints.*

class BookView(
    val id: Int,
    val name: String
)

class BookAddRequest(
    @field:Size(min=3, max=10)
    @field:NotNull(message = "stringValue has to be present")
    val name: String
)

class BookEditRequest(
    @NotBlank(message = "Need field name")
    val name: String
)