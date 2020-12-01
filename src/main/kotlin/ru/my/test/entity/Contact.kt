package ru.my.test.entity

import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "contacts")
class Contact(
    id: Long = 0,
    var phone: String,
    var email: String,
) : BaseEntity(id) {
    @OneToOne
    @JoinColumn(name = "author_id")
    lateinit var author: Author
}