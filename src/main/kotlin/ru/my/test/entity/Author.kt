package ru.my.test.entity

import java.time.OffsetDateTime
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Entity
import javax.persistence.Table

@Entity
@Table(name = "authors")
class Author(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Int = 0,
    var name: String,
    val dateOfBirth: OffsetDateTime,
) {

}