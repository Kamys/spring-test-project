package ru.my.test.model

import javax.validation.constraints.Positive

class ContactView(
    val id: Int,
    val phone: String,
    val email: String,
    val authorId: Int
)

class ContactAddRequest(
    @field:Positive
    val authorId: Int,
    val phone: String? = null,
    val email: String? = null
)

class ContactEditRequest(
    val phone: String? = null,
    val email: String? = null
)