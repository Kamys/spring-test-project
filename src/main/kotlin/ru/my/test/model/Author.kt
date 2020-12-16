package ru.my.test.model

import javax.validation.constraints.NotNull

class AuthorView(
    val id: Long,
    val name: String,
    val bookIds: List<Long> = mutableListOf(),
)

class AuthorAddRequest(
    // эта аннотация работает?
    // насколько помню, jackson не сможет преобразовать json в этот объект, если поле не придёт,
    // а hibernate validator не сможет проверить, что поле не null, пока не сформирован объект
    // если это всё таки работает, то почему этой аннотации нет на AuthorEditRequest?
    @field:NotNull
    val name: String
)

class AuthorEditRequest(
    val name: String
)