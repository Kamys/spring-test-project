import com.fasterxml.jackson.annotation.JsonValue

enum class ErrorType {
    SERVER_ERROR,
    VALIDATION_ERROR,
    PROCESS_ERROR;

    @JsonValue
    fun toJsonValue(): String {
        return this.toString().toLowerCase()
    }
}

class ApiError(
    val type: ErrorType,
    val status: Int,
    val title: String,
    val detail: String?
)