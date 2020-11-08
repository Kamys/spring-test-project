package ru.my.test.model

import javax.validation.constraints.NotBlank

class BookView(
    val id: Int,
    val name: String
)

class BookAddRequest(
    @NotBlank(message = "Need field name")
    val name: String
)

class BookEditRequest(
        @NotBlank(message = "Need field name")
        val name: String
)