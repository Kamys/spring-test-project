package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "contacts")
class Contact(
    id: Int = 0,
    var phone: String,
    var email: String,
    @OneToOne(mappedBy = "contact", orphanRemoval = true)
    var author: Author
) : BaseEntity(id)