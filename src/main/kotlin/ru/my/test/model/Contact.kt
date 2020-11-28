package ru.my.test.model

class ContactView(
    val id: Int,
    val phone: String,
    val email: String,
)

class ContactEditRequest(
    val phone: String,
    val email: String,
)