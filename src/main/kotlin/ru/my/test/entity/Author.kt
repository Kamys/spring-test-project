package ru.my.test.entity

import javax.persistence.*

@Entity
@Table(name = "authors")
class Author(
    id: Long = 0,
    var name: String,
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "author_id")],
        inverseJoinColumns = [JoinColumn(name = "book_id")]
    )
    var books: MutableList<Book> = mutableListOf(),
) : BaseEntity(id) {
    @OneToOne(mappedBy = "author", cascade = [CascadeType.ALL], orphanRemoval = true)
    var contact: Contact? = null
        set(value) {
            value?.author = this
            field = value
        }
}