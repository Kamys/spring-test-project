package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "authors")
class Author(
    id: Int = 0,
    var descriptionOfWrittenStyle: String? = null,
    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "author_id")],
        inverseJoinColumns = [JoinColumn(name = "book_id")]
    )
    var books: List<Book> = emptyList(),
    @OneToOne
    @JoinColumn(name = "user_id")
    var user: User
) : BaseEntity(id)