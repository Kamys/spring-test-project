package ru.my.test.entity

import org.hibernate.annotations.Fetch
import org.hibernate.annotations.FetchMode
import javax.persistence.*

@Entity
@Table(name = "books")
class Book(
    id: Long = 0,
    var name: String,
    @ManyToMany(cascade = [CascadeType.PERSIST])
    @JoinTable(
        name = "author_book",
        joinColumns = [JoinColumn(name = "book_id")],
        inverseJoinColumns = [JoinColumn(name = "author_id")],
    )
    @Fetch(FetchMode.SUBSELECT)
    var authors: MutableList<Author> = mutableListOf(),
    @OneToMany(mappedBy = "book", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    @Fetch(FetchMode.SUBSELECT)
    var reviews: MutableList<Review> = mutableListOf(),
) : BaseEntity(id) {

    fun addAuthor(author: Author) {
        authors.add(author)
    }

    fun addReview(review: Review) {
        reviews.add(review)
        review.book = this
    }
}