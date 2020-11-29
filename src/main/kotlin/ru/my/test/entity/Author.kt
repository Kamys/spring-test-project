package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "authors")
class Author(
    id: Long = 0,
    var name: String,
    @ManyToMany
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "author_id")],
        inverseJoinColumns = [JoinColumn(name = "book_id")]
    )
    var books: List<Book> = emptyList(),
) : BaseEntity(id) {
    @OneToOne(cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "contact_id")
    var contact: Contact? = null
        set(value) {
            value?.author = this
            field = value
        }
}