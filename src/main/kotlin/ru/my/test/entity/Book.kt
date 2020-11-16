package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "books")
class Book(
    id: Int = 0,
    var name: String,
    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")]
    )
    var authors: List<Author> = emptyList()
) : BaseEntity(id)