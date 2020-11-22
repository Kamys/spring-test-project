package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "users")
class User(
    id: Int = 0,
    var name: String,
) : BaseEntity(id)