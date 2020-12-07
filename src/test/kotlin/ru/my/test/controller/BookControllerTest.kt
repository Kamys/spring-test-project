package ru.my.test.controller

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import ru.my.test.AbstractIntegrationTest
import ru.my.test.entity.Author
import ru.my.test.entity.Book
import ru.my.test.entity.BookRating
import ru.my.test.entity.Review
import ru.my.test.model.*
import ru.my.test.service.AuthorRepository
import ru.my.test.service.BookRepository
import ru.my.test.service.ReviewRepository
import ru.my.test.service.findOrException


class BookControllerTest : AbstractIntegrationTest() {
    @Autowired
    private lateinit var bookRepository: BookRepository

    @Autowired
    private lateinit var authorRepository: AuthorRepository

    @Autowired
    private lateinit var reviewRepository: ReviewRepository

    @BeforeEach
    fun beforeEach() {
        bookRepository.deleteAll()
        authorRepository.deleteAll()
        reviewRepository.deleteAll()
    }

    @Test
    fun `GET all exist books`() {

        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.map { it.name }.shouldContainExactly(listOf(bookFirst.name, bookSecond.name))
    }

    @Test
    fun `GET books with author ids`() {
        val bookFirst = transactional {
            val book =  Book(name = "Book 1").apply {
                addAuthor(Author(name = "Author 1"))
                addAuthor(Author(name = "Author 2"))
            }
            bookRepository.save(book)
        }
        val bookSecond = modelHelper.createBook()

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.findOrException { it.id == bookFirst.id }
            .authorIds
            .shouldContainExactly(bookFirst.authors.map { it.id })
        response.findOrException { it.id == bookSecond.id }
            .authorIds
            .shouldBeEmpty()
    }

    @Test
    fun `GET books with reviews`() {
        val (bookFirst, bookSecond) = transactional {
            val bookFirst = modelHelper.createBook().apply {
                addReview(Review(text = "Review 1", rating = BookRating.GOOD))
                addReview(Review(text = "Review 2", rating = BookRating.NORMAL))
                bookRepository.save(this)
            }
            val bookSecond = modelHelper.createBook()

            Pair(bookFirst, bookSecond)
        }

        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.size.shouldBe(2)
        response.findOrException { it.id == bookFirst.id }
            .reviews
            .map { it.id }
            .shouldContainExactly(bookFirst.reviews.map { it.id })
        response.findOrException { it.id == bookSecond.id }
            .authorIds
            .shouldBeEmpty()
    }

    @Test
    fun `GET book with author ids`() {
        val book = transactional {
            val book =  Book(name = "Book 1").apply {
                addAuthor(Author(name = "Author 1"))
                addAuthor(Author(name = "Author 2"))
            }
            bookRepository.save(book)
        }

        val response = mvc.get("/books/${book.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.authorIds
            .shouldContainExactly(book.authors.map { it.id })
    }

    @Test
    fun `GET book by id`() {
        val book = modelHelper.createBook()

        val response = mvc.get("/books/${book.id}")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(book.id)
        response.name.shouldBe(book.name)

        transactional {
            val allBooks = bookRepository.findAll()
            allBooks.size.shouldBe(1)
            allBooks.first().id.shouldBe(book.id)
            allBooks.first().name.shouldBe(book.name)
        }
    }

    @Test
    fun `GET book by nonexistent ID should return 404`() {
        mvc.get("/books/1").andExpect(status().isNotFound)
    }

    @Test
    fun `GET empty array if books not exist`() {
        val response = mvc.get("/books")
            .andExpect(status().isOk)
            .andReturn()
            .asObject<List<BookView>>()

        response.shouldBeEmpty()
    }

    @Test
    fun `POST created new book`() {
        val bookTitle = faker.book().title()
        val bookRequest = BookAddRequest(bookTitle)

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<BookView>()

        response.name.shouldBe(bookTitle)

        transactional {
            val allBooks = bookRepository.findAll()
            allBooks.size.shouldBe(1)
            allBooks.first().name.shouldBe(bookTitle)
        }
    }

    @Test
    fun `POST return 400 if name not unique`() {
        val bookFirst = modelHelper.createBook()

        val request = BookEditRequest(bookFirst.name)

        val response = mvc.post("/books", request.asJson())
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiValidationError>()

        response.violations.first().message.shouldBe("Имя уже используется")
    }

    @Test
    fun `POST created new book with authors`() {
        val authorFirst = modelHelper.createAuthor()
        val authorSecond = modelHelper.createAuthor()

        val authorIds = listOf(authorFirst.id, authorSecond.id)
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = authorIds
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<BookView>()

        response.authorIds.shouldContainExactly(authorIds)

        transactional {
            val allBooks = bookRepository.findAll()
            allBooks.size.shouldBe(1)
            allBooks.first().authors.map { it.id }.shouldContainExactly(authorIds)
        }
    }

    @Test
    fun `POST created new book with empty authors`() {
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = emptyList()
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isCreated)
            .andReturn()
            .asObject<BookView>()

        response.authorIds.shouldBeEmpty()

        transactional {
            val allBooks = bookRepository.findAll()
            allBooks.size.shouldBe(1)
            allBooks.first().authors.shouldBeEmpty()
        }
    }

    @Test
    fun `POST return 404 if authorIds has nonexistent ID`() {
        val bookRequest = BookAddRequest(
            name = "Book",
            authorIds = listOf(99)
        )

        val response = mvc.post("/books", bookRequest.asJson())
            .andExpect(status().isNotFound)
            .andReturn()
            .asObject<ApiError>()

        response.detail.shouldBe("Не удалось найти автора с id: 99")
    }

    @Test
    fun `POST return 400 if request not correct`() {
        val response = mvc.post("/books", "{}")
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiError>()

        response.title.shouldBe(ErrorMessages.JSON_NOT_VALID.text)
    }

    @Test
    fun `PUT book by nonexistent ID should return 404`() {
        val request = BookEditRequest("Book new name")

        mvc.put("/books/99", request.asJson()).andExpect(status().isNotFound)
    }

    @Test
    fun `PUT book return edited book`() {
        val editedBook = modelHelper.createBook("Book old name")
        val bookSecond = modelHelper.createBook()

        val request = BookEditRequest("Book new name")

        val response = mvc.put("/books/${editedBook.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(editedBook.id)
        response.name.shouldBe(request.name)

        transactional {
            bookRepository.findOrException(editedBook.id).name.shouldBe(request.name)
            bookRepository.findOrException(bookSecond.id).name.shouldBe(bookSecond.name)
        }
    }

    @Test
    fun `PUT book edit authorIds`() {
        val editedBook = transactional {
            Book(name = "Book 1").apply {
                addAuthor(Author(name = "Author 1"))
                addAuthor(Author(name = "Author 2"))
                addAuthor(Author(name = "Author 3"))
                bookRepository.save(this)
            }
        }

        val bookSecond = modelHelper.createBook()

        val newAuthorIds = listOf(editedBook.authors[2].id)
        val request = BookEditRequest(authorIds = newAuthorIds, name = "New name")

        val response = mvc.put("/books/${editedBook.id}", request.asJson())
            .andExpect(status().isOk)
            .andReturn()
            .asObject<BookView>()

        response.id.shouldBe(editedBook.id)
        response.authorIds.shouldContainExactly(newAuthorIds)

        transactional {
            bookRepository.findOrException(editedBook.id).authors
                .map { it.id }
                .shouldContainExactly(newAuthorIds)
            bookRepository.findOrException(bookSecond.id).authors.shouldBeEmpty()
        }
    }

    @Test
    fun `PUT return 400 if new name not unique`() {
        val bookFirst = modelHelper.createBook()
        val bookSecond = modelHelper.createBook()

        val request = BookEditRequest(bookSecond.name)

        val response = mvc.put("/books/${bookFirst.id}", request.asJson())
            .andExpect(status().isBadRequest)
            .andReturn()
            .asObject<ApiValidationError>()

        response.violations.first().message.shouldBe("Имя уже используется")
    }

    @Test
    fun `DELETE book by nonexistent ID should return 404`() {
        mvc.delete("/books/1").andExpect(status().isNotFound)
    }

    @Test
    fun `DELETE return 200 if book deleted`() {
        val bookForDelete = modelHelper.createBook()
        val book = modelHelper.createBook()

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isNoContent)

        transactional {
            val allBook = bookRepository.findAll()
            allBook.size.shouldBe(1)
            allBook.first().id.shouldBe(book.id)
            allBook.first().name.shouldBe(book.name)
        }
    }

    @Test
    fun `DELETE book with authors`() {
        val bookForDelete = transactional {
            val book = Book(name = "Book 1").apply {
                addAuthor(Author(name = "Author 1"))
                addAuthor(Author(name = "Author 2"))
            }
            bookRepository.save(book)
        }

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isNoContent)

        transactional {
            val allBook = bookRepository.findAll()
            allBook.shouldBeEmpty()

            val allAuthors = authorRepository.findAll()
            allAuthors.size.shouldBe(2)
        }
    }

    @Test
    fun `DELETE book also delete review`() {
        val bookForDelete = transactional {
            val book = modelHelper.createBook().apply {
                addReview(Review(text = "Review 1", rating = BookRating.GOOD))
                addReview(Review(text = "Review 2", rating = BookRating.NORMAL))
                bookRepository.save(this)
            }
            book
        }

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isNoContent)

        transactional {
            bookRepository.count().shouldBe(0)
            reviewRepository.count().shouldBe(0)
        }
    }

    @Test
    fun `DELETE book don't delete author`() {
        val bookForDelete = transactional {
            Book(name = "Book 1").apply {
                addAuthor(Author(name = "Author 1"))
                addAuthor(Author(name = "Author 2"))
                bookRepository.save(this)
            }
        }

        mvc.delete("/books/${bookForDelete.id}").andExpect(status().isNoContent)

        transactional {
            bookRepository.count().shouldBe(0)
            authorRepository.count().shouldBe(2)
        }
    }
}