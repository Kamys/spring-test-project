package ru.my.test.model

class ContactView(
    val id: Long,
    val phone: String,
    val email: String,
)

class ContactEditRequest(
    val phone: String,
    val email: String,
)